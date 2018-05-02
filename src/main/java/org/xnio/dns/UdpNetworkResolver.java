/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xnio.dns;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.Executor;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.nio.channels.Channel;
import java.io.IOException;
import org.xnio.BufferAllocator;
import org.xnio.IoFuture;
import org.xnio.ChannelListener;
import org.xnio.Pool;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.ChannelSource;
import org.xnio.FutureResult;
import org.jboss.logging.Logger;
import org.xnio.Pooled;
import org.xnio.channels.MulticastMessageChannel;
import org.xnio.channels.SocketAddressBuffer;

/**
 * A network resolver which uses UDP to contact a remote server.
 */
public final class UdpNetworkResolver extends AbstractNetworkResolver {

    private static final Logger log = Logger.getLogger("org.xnio.dns.resolver.udp");

    private final Pool<ByteBuffer> bufferPool;
    private final ChannelSource<MulticastMessageChannel> channelSource;
    private final Executor executor;
    private final Random random;

    /**
     * Construct a new UDP network resolver.  In order to provide resilient security, the given channel source
     * should choose port numbers at random.
     *
     * @param executor the executor to use for asynchronous notifications
     * @param channelSource the channel source to use to create new UDP client channels
     * @param random the RNG to use to generate request IDs
     */
    public UdpNetworkResolver(final Executor executor, final ChannelSource<MulticastMessageChannel> channelSource, final Random random) {
        this(Buffers.allocatedBufferPool(BufferAllocator.BYTE_BUFFER_ALLOCATOR, 512), channelSource, executor, random);
    }

    /**
     * Construct a new UDP network resolver.  In order to provide resilient security, the given channel source
     * should choose port numbers at random.
     *
     * @param bufferPool the buffer pool to use
     * @param executor the executor to use for asynchronous notifications
     * @param channelSource the channel source to use to create new UDP client channels
     * @param random the RNG to use to generate request IDs
     */
    public UdpNetworkResolver(final Pool<ByteBuffer> bufferPool, final ChannelSource<MulticastMessageChannel> channelSource, final Executor executor, final Random random) {
        this.bufferPool = bufferPool;
        this.channelSource = channelSource;
        this.executor = executor;
        this.random = random;
    }

    /** {@inheritDoc} */
    public Resolver resolverFor(final SocketAddress server) {
        return new ResolverImpl((InetSocketAddress) server);
    }

    private class ResolverImpl extends AbstractResolver implements Resolver {
        private final InetSocketAddress serverAddress;

        ResolverImpl(final InetSocketAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        public IoFuture<Answer> resolve(final Domain name, final RRClass rrClass, final RRType rrType, final Set<Query.Flag> flags) {
            final int id = random.nextInt() & 0xffff;
            final FutureResult<Answer> manager = new FutureResult<Answer>(executor);
            final IoFuture<? extends MulticastMessageChannel> futureChannel = channelSource.open(new ChannelListener<MulticastMessageChannel>() {
                public void handleEvent(final MulticastMessageChannel channel) {
                    channel.getCloseSetter().set(new ChannelListener<Channel>() {
                        public void handleEvent(final Channel channel) {
                            // cancel request if it isn't done
                            manager.setCancelled();
                        }
                    });
                    manager.addCancelHandler(IoUtils.closingCancellable(channel));
                    channel.getReadSetter().set(new ReadListener(id, manager, name, rrClass, rrType));
                    channel.resumeReads();
                    final Pooled<ByteBuffer> pooled = bufferPool.allocate();
                    final ByteBuffer buffer = pooled.getResource();
                    buffer.putShort((short) id);
                    buffer.putShort((short) (flags.contains(Query.Flag.NO_RECURSION) ? 0 : 1 << 7));
                    buffer.putShort((short) 1);
                    buffer.putShort((short) 0);
                    buffer.putShort((short) 0);
                    buffer.putShort((short) 0);
                    for (Domain.Label label : name.getParts()) {
                        final byte[] bytes = label.getBytes();
                        buffer.put((byte) bytes.length);
                        buffer.put(bytes);
                    }
                    buffer.put((byte) 0);
                    buffer.putShort((short) rrType.getId());
                    buffer.putShort((short) rrClass.getId());
                    buffer.flip();
                    try {
                        channel.sendTo(serverAddress, buffer);
                    } catch (IOException e) {
                        manager.setException(e);
                        IoUtils.safeClose(channel);
                    }
                }
            });
            manager.addCancelHandler(futureChannel);
            futureChannel.addNotifier(new IoFuture.HandlingNotifier<Channel, FutureResult<Answer>>() {
                public void handleCancelled(final FutureResult<Answer> attachment) {
                    attachment.setCancelled();
                }
            }, manager);
            return manager.getIoFuture();
        }
    }

    private class ReadListener implements ChannelListener<MulticastMessageChannel> {

        private final int id;
        private final Domain name;
        private final RRClass rrClass;
        private final RRType rrType;
        private final FutureResult<Answer> request;

        ReadListener(final int id, final FutureResult<Answer> request, final Domain name, final RRClass rrClass, final RRType rrType) {
            this.id = id;
            this.request = request;
            this.name = name;
            this.rrClass = rrClass;
            this.rrType = rrType;
        }

        public void handleEvent(final MulticastMessageChannel channel) {
            final Pooled<ByteBuffer> pooled = bufferPool.allocate();
            try {
                final ByteBuffer buffer = pooled.getResource();
                if (buffer == null) {
                    // todo - delay for a time?
                    request.setException(new IOException("No buffers available to receive reply"));
                    IoUtils.safeClose(channel);
                    return;
                }
                final SocketAddressBuffer addressBuffer = new SocketAddressBuffer();
                try {
                    channel.receiveFrom(addressBuffer, buffer);
                } catch (IOException e) {
                    log.error("Closing channel '%s' due to I/O error on read: %s", channel, e);
                    IoUtils.safeClose(channel);
                }
                buffer.flip();
                try {
                    final int id = buffer.getShort() & 0xffff;
                    if (id != this.id) {
                        // ignore wrong reply ID
                        channel.resumeReads();
                        return;
                    }
                    final int flags = buffer.getShort() & 0xffff;
                    if ((flags & 1 << 0) == 0) {
                        // ignore query
                        channel.resumeReads();
                        return;
                    }
                    final Answer.Builder builder = Answer.builder();
                    if (((flags & 1 << 6) != 0)) {
                        // todo truncation request - handle via TCP some other time
                        request.setResult(builder.setHeaderInfo(name, rrClass, rrType, ResultCode.FORMAT_ERROR).create());
                        IoUtils.safeClose(channel);
                        return;
                    }
                    builder.setResultCode(ResultCode.fromInt(flags >> 12));
                    if (((flags & 1 << 5) != 0)) builder.addFlag(Answer.Flag.AUTHORITATIVE);
                    if (((flags & 1 << 8) != 0)) builder.addFlag(Answer.Flag.RECURSION_AVAILABLE);
                    final int qcnt = buffer.getShort() & 0xffff;
                    if (qcnt != 1) {
                        // ignore bogus reply
                        channel.resumeReads();
                        return;
                    }
                    final int ancnt = buffer.getShort() & 0xffff;
                    final int nscnt = buffer.getShort() & 0xffff;
                    final int arcnt = buffer.getShort() & 0xffff;
                    builder.setQueryDomain(Domain.fromBytes(buffer));
                    builder.setQueryRRType(RRType.fromInt(buffer.getShort() & 0xffff));
                    builder.setQueryRRClass(RRClass.fromInt(buffer.getShort() & 0xffff));
                    for (int i = 0; i < ancnt; i ++) {
                        builder.addAnswerRecord(Record.fromBytes(buffer));
                    }
                    for (int i = 0; i < nscnt; i ++) {
                        builder.addAuthorityRecord(Record.fromBytes(buffer));
                    }
                    for (int i = 0; i < arcnt; i ++) {
                        builder.addAdditionalRecord(Record.fromBytes(buffer));
                    }
                    request.setResult(builder.create());
                    IoUtils.safeClose(channel);
                } catch (BufferUnderflowException e) {
                    request.setResult(Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.FORMAT_ERROR).create());
                    IoUtils.safeClose(channel);
                }
            } finally {
                pooled.free();
            }
        }
    }
}
