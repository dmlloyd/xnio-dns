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

/**
 * A domain parse exception.  Thrown when a domain name contains invalid characters.
 */
public class DomainParseException extends IllegalArgumentException {

    private static final long serialVersionUID = -8829167161458971075L;

    /**
     * Constructs a <tt>DomainParseException</tt> with no detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause(Throwable) initCause}.
     */
    public DomainParseException() {
    }

    /**
     * Constructs a <tt>DomainParseException</tt> with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause(Throwable) initCause}.
     *
     * @param msg the detail message
     */
    public DomainParseException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <tt>DomainParseException</tt> with the specified cause. The detail message is set to:
     * <pre>
     *  (cause == null ? null : cause.toString())</pre>
     * (which typically contains the class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public DomainParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <tt>DomainParseException</tt> with the specified detail message and cause.
     *
     * @param msg the detail message
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public DomainParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a <tt>DomainParseException</tt> with the specified detail message and position.
     *
     * @param msg the detail message
     * @param position the character position, starting at 0
     */
    public DomainParseException(String msg, int position) {
        super(msg);
        setPosition(position);
    }

    private int position;

    /**
     * Get the position at which the error occurred.
     *
     * @return the character position, starting at 0
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the position at which the error occurred.
     *
     * @param position the character position, starting at 0
     */
    public void setPosition(final int position) {
        this.position = position;
    }

    /** {@inheritDoc} */
    public String getMessage() {
        final String msg = super.getMessage();
        if (msg != null && msg.length() > 0) {
            return msg + " at position " + getPosition();
        } else {
            return "Parse exception at position " + getPosition();
        }
    }
}
