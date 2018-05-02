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

/**
 * A resolver which returns no results.
 */
public final class EmptyResolver extends AbstractResolver {

    private final int resultCode;

    /**
     * Create a new instance.
     *
     * @param resultCode the result code to return
     */
    public EmptyResolver(final int resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Create a new instance with a result code of {@link ResultCode#NOERROR}.
     */
    public EmptyResolver() {
        this(ResultCode.NOERROR);
    }

    public IoFuture<Answer> resolve(final Query query) {
        return new FinishedIoFuture<>(Answer.builder().populateFromQuery(query).setResultCode(resultCode).create());
    }
}
