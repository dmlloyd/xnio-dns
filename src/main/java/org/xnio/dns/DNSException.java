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

import java.io.IOException;

/**
 * An exception thrown by a DNS server.
 */
public class DNSException extends IOException {

    private static final long serialVersionUID = 3313733955232311955L;
    private final ResultCode code;

    /**
     * Constructs a <tt>DNSException</tt> with no detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause(Throwable) initCause}.
     *
     * @param code the reason code
     */
    public DNSException(final ResultCode code) {
        this.code = code;
    }

    /**
     * Constructs a <tt>DNSException</tt> with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause(Throwable) initCause}.
     *
     * @param code the reason code
     * @param msg the detail message
     */
    public DNSException(final ResultCode code, final String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * Constructs a <tt>DNSException</tt> with the specified cause. The detail message is set to:
     * <pre>
     *  (cause == null ? null : cause.toString())</pre>
     * (which typically contains the class and detail message of <tt>cause</tt>).
     *
     * @param code the reason code
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public DNSException(final ResultCode code, final Throwable cause) {
        initCause(cause);
        this.code = code;
    }

    /**
     * Constructs a <tt>DNSException</tt> with the specified detail message and cause.
     *
     * @param code the reason code
     * @param msg the detail message
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public DNSException(final ResultCode code, final String msg, final Throwable cause) {
        super(msg);
        initCause(cause);
        this.code = code;
    }

    /**
     * Get the reason code.
     *
     * @return the reason code
     */
    public ResultCode getCode() {
        return code;
    }
}
