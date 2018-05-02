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

import org.xnio.Bits;
import org.xnio.IoFuture;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.dns.record.NsRecord;
import org.xnio.dns.record.ARecord;
import org.xnio.dns.record.AaaaRecord;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.net.InetAddress;

/**
 * A resolver which queries servers iteratively until the complete answer is acquired.
 */
public final class IterativeResolver extends AbstractResolver {

    private final NetworkResolver networkResolver;
    private final Resolver localResolver;
    private final Executor executor;

    public IterativeResolver(final NetworkResolver networkResolver, final Resolver localResolver, final Executor executor) {
        this.networkResolver = networkResolver;
        this.localResolver = localResolver;
        this.executor = executor;
    }

    public IoFuture<Answer> resolve(final Query query) {
        final Domain name = query.getDomain();
        final int rrClass = query.getRRClass();
        final int rrType = query.getRRType();
        final int flags = query.getQueryFlags();
        if (name.equals(Domain.ROOT) || Bits.allAreSet(flags, Query.Flag.NO_RECURSION)) {
            return new FinishedIoFuture<Answer>(
                    Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.NXDOMAIN).create()
            );
        }
        final FutureResult<Answer> futureResult = new FutureResult<Answer>(executor);
        final IoFuture<Answer> futureParentNs = localResolver.resolve(name.getParent(), RRClass.IN, RRType.NS);
        futureParentNs.addNotifier(new IoFuture.HandlingNotifier<Answer, FutureResult<Answer>>() {
            // todo configurable?
            private final AtomicInteger ttl = new AtomicInteger(16);

            public void handleCancelled(final FutureResult<Answer> result) {
                result.setCancelled();
            }

            public void handleFailed(final IOException exception, final FutureResult<Answer> result) {
                result.setException(exception);
            }

            public void handleDone(final Answer answer, final FutureResult<Answer> result) {
                if (answer.getResultCode() != ResultCode.NOERROR) {
                    result.setResult(answer);
                    return;
                }
                if (ttl.decrementAndGet() == 0) {
                    result.setResult(Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.SERVER_FAILURE).create());
                }
                final List<Record> answerRecords = answer.getAnswerRecords();
                if (answerRecords.isEmpty()) {
                    // iteration needed...
                    final List<Record> additionalRecords = answer.getAdditionalRecords();
                    final Map<Domain, InetAddress> possibleServers = new HashMap<Domain, InetAddress>();
                    for (Record record : additionalRecords) {
                        if (record instanceof ARecord) {
                            possibleServers.put(record.getName(), ((ARecord)record).getAddress());
                        } else if (record instanceof AaaaRecord) {
                            possibleServers.put(record.getName(), ((AaaaRecord)record).getAddress());
                        }
                    }
                    final List<InetAddress> serversToTry = new ArrayList<InetAddress>();
                    for (Record record : answer.getAuthorityRecords()) {
                        if (record instanceof NsRecord) {
                            final NsRecord nsRecord = (NsRecord) record;
                            if (name.isSubdomainOf(record.getName())) {
                                // try that server next!
                                final Domain server = nsRecord.getServer();
                                // but first, get the IP...
                                final InetAddress address = possibleServers.get(server);
                                if (address != null) {
                                    serversToTry.add(address);
                                }
                            }
                        }
                    }
                    if (serversToTry.isEmpty()) {
                        result.setResult(Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.SERVER_FAILURE).create());
                    }
                    // todo - use first server, but we should have a better algo
                    final Resolver resolver = networkResolver.resolverFor(serversToTry.get(0));
                    final IoFuture<Answer> recursion = resolver.resolve(name, rrClass, rrType);
                    recursion.addNotifier(this, result);
                    result.addCancelHandler(recursion);
                } else {
                    // got an answer!
                    result.setResult(answer);
                }
            }
        }, futureResult);
        futureResult.addCancelHandler(futureParentNs);
        return futureResult.getIoFuture();
    }
}
