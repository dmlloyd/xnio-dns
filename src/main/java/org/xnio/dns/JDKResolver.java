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

import org.xnio.IoFuture;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.dns.record.PtrRecord;
import org.xnio.dns.record.ARecord;
import org.xnio.dns.record.AaaaRecord;
import java.util.Set;
import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Inet4Address;
import java.net.Inet6Address;

/**
 * A resolver which uses the JDK facility to answer queries.  The JDK facility can only do simple forward and reverse
 * host/IP address lookups.
 */
public final class JDKResolver extends AbstractResolver {

    private final Executor queryExecutor;

    /**
     * Construct a new instance.
     *
     * @param queryExecutor the executor to use to execute asynchronous queries
     */
    public JDKResolver(final Executor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /** {@inheritDoc} */
    public IoFuture<Answer> resolve(final Domain name, final int rrClass, final int rrType, final Set<Query.Flag> flags) {
        if (rrClass != RRClass.ANY && rrClass != RRClass.IN || rrType != RRType.ANY && rrType != RRType.A && rrType != RRType.AAAA) {
            return new FinishedIoFuture<Answer>(Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.NOERROR).create());
        }
        final Answer.Builder builder = Answer.builder();
        builder.setHeaderInfo(name, rrClass, rrType, ResultCode.UNKNOWN);
        final FutureResult<Answer> answerManager = new FutureResult<Answer>();
        queryExecutor.execute(new QueryTask(answerManager, builder, name));
        return answerManager.getIoFuture();
    }

    private static class QueryTask implements Runnable {

        private final FutureResult<Answer> answerManager;
        private final Answer.Builder builder;
        private final Domain name;

        public QueryTask(final FutureResult<Answer> answerManager, final Answer.Builder builder, final Domain name) {
            this.answerManager = answerManager;
            this.builder = builder;
            this.name = name;
        }

        public void run() {
            if (name.isReverseArpa()) {
                try {
                    builder.addAnswerRecord(new PtrRecord(name, Domain.fromString(InetAddress.getByAddress(name.getReverseArpaBytes()).getHostAddress())));
                } catch (UnknownHostException e) {
                } catch (IllegalArgumentException e) {
                }
            } else {
                try {
                    for (InetAddress address : InetAddress.getAllByName(name.getHostName())) {
                        if (address instanceof Inet4Address) {
                            builder.addAnswerRecord(new ARecord(name, (Inet4Address) address));
                        } else if (address instanceof Inet6Address) {
                            builder.addAnswerRecord(new AaaaRecord(name, (Inet6Address) address));
                        }
                        // else ignore
                    }
                } catch (UnknownHostException e) {
                }
            }
            answerManager.setResult(builder.create());
        }
    }
}
