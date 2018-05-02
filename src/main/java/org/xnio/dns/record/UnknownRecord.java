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

package org.xnio.dns.record;

import org.xnio.dns.Record;
import org.xnio.dns.Domain;
import org.xnio.dns.RRClass;
import org.xnio.dns.TTLSpec;
import org.xnio.Buffers;
import java.nio.ByteBuffer;

/**
 * A record of unknown type.
 */
public class UnknownRecord extends Record {

    private static final long serialVersionUID = -1357005801530421627L;

    private final byte[] data;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param rrType the record type
     * @param ttlSpec the TTL spec
     * @param data the raw data
     */
    public UnknownRecord(final Domain name, final int rrClass, final int rrType, final TTLSpec ttlSpec, final byte[] data) {
        super(name, rrClass, rrType, ttlSpec);
        this.data = data;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrType the record type
     * @param ttlSpec the TTL spec
     * @param data the raw data
     */
    public UnknownRecord(final Domain name, final int rrType, final TTLSpec ttlSpec, final byte[] data) {
        this(name, RRClass.IN, rrType, ttlSpec, data);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrType the record type
     * @param data the raw data
     */
    public UnknownRecord(final Domain name, final int rrType, final byte[] data) {
        this(name, rrType, TTLSpec.ZERO, data);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param rrType the record type
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which this record's RDATA should be built
     */
    public UnknownRecord(final Domain name, final int rrClass, final int rrType, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        super(name, rrClass, rrType, ttlSpec);
        data = Buffers.take(recordBuffer, recordBuffer.remaining());
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        for (byte b : data) {
            builder.append(' ').append(Integer.toHexString(b & 0xff));
        }
    }
}