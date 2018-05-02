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
import org.xnio.FutureResult;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;
import java.io.IOException;

public final class CachingResolver extends AbstractResolver implements Resolver {

    private final Map<RecordIdentifier, FutureResult<Answer>> cache;
    private final Resolver realResolver;
    private final Executor executor;

    public CachingResolver(final Resolver resolver, final Executor executor, final int cacheSize) {
        cache = new CacheMap<RecordIdentifier, FutureResult<Answer>>(cacheSize);
        realResolver = resolver;
        this.executor = executor;
    }

    /** {@inheritDoc} */
    public IoFuture<Answer> resolve(final Domain name, final int rrClass, final int rrType, final int flags) {
        if (Bits.allAreSet(flags, Query.Flag.BYPASS_CACHE)) {
            // skip the cache, do not record results
            return realResolver.resolve(name, rrClass, rrType, flags);
        } else {
            final RecordIdentifier key = new RecordIdentifier(name, rrClass, rrType);
            final FutureResult<Answer> newAnswer;
            synchronized (cache) {
                final FutureResult<Answer> future = cache.get(key);
                if (future != null) {
                    final IoFuture.Status status = future.getIoFuture().getStatus();
                    if (status == IoFuture.Status.WAITING) {
                        // still waiting for result
                        return future.getIoFuture();
                    } else if (status == IoFuture.Status.DONE) {
                        try {
                            boolean expired = false;
                            for (Record record : future.getIoFuture().get().getAnswerRecords()) {
                                if (record.getTtlSpec().isExpired()) {
                                    expired = true;
                                    break;
                                }
                            }
                            if (! expired) {
                                return future.getIoFuture();
                            }
                        } catch (IOException e) {
                            // fall out and re-query, the future has gone bad
                            // technically shouldn't be possible because status was "done"
                        }
                    }
                }
                newAnswer = new FutureResult<Answer>(executor);
                cache.put(key, newAnswer);
            }
            final IoFuture<Answer> realFuture = realResolver.resolve(name, rrClass, rrType, flags);
            realFuture.addNotifier(new IoFuture.HandlingNotifier<Answer, FutureResult<Answer>>() {
                public void handleCancelled(final FutureResult<Answer> attachment) {
                    synchronized (cache) {
                        if (cache.get(key).equals(attachment)) {
                            cache.remove(key);
                        }
                    }
                    attachment.setCancelled();
                }

                public void handleFailed(final IOException exception, final FutureResult<Answer> attachment) {
                    synchronized (cache) {
                        if (cache.get(key).equals(attachment)) {
                            cache.remove(key);
                        }
                    }
                    attachment.setException(exception);
                }

                public void handleDone(final Answer result, final FutureResult<Answer> attachment) {
                    attachment.setResult(result);
                }
            }, newAnswer);
            return newAnswer.getIoFuture();
        }
    }

    private static final class CacheMap<K, V> extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 733255475501069288L;

        private final int max;

        CacheMap(final int max) {
            super(64, 0.6f, true);
            this.max = max;
        }

        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return size() > max;
        }
    }
}
