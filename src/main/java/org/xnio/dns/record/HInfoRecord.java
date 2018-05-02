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
import org.xnio.dns.RRClass;
import org.xnio.dns.RRType;
import org.xnio.dns.Domain;
import org.xnio.dns.TTLSpec;
import org.xnio.Buffers;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * A record of type {@link RRType#HINFO}.
 */
public class HInfoRecord extends Record {

    private static final long serialVersionUID = 8123650143005076515L;

    private final String cpu;
    private final String os;
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private static String readCharString(ByteBuffer buffer) {
        return new String(Buffers.take(buffer, buffer.get() & 0xff), LATIN_1);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public HInfoRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        this(name, rrClass, ttlSpec, readCharString(recordBuffer), readCharString(recordBuffer));
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the string from which the record data should be built
     */
    public HInfoRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        super(name, rrClass, RRType.HINFO, ttlSpec);
        final int spc = recordString.indexOf(' ');
        if (spc == -1) {
            throw new IllegalArgumentException("Invalid record data format");
        }
        cpu = recordString.substring(0, spc);
        os = recordString.substring(spc).trim();
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param ttlSpec the TTL spec
     * @param cpu the CPU type
     * @param os the OS type
     */
    public HInfoRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String cpu, final String os) {
        super(name, rrClass, RRType.HINFO, ttlSpec);
        this.cpu = cpu;
        this.os = os;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param cpu the CPU type
     * @param os the OS type
     */
    public HInfoRecord(final Domain name, final TTLSpec ttlSpec, final String cpu, final String os) {
        this(name, RRClass.IN, ttlSpec, cpu, os);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param cpu the CPU type
     * @param os the OS type
     */
    public HInfoRecord(final Domain name, final String cpu, final String os) {
        this(name, TTLSpec.ZERO, cpu, os);
    }

    /**
     * Get the CPU type.
     *
     * @return the CPU type
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * Get the OS type.
     *
     * @return the OS type
     */
    public String getOs() {
        return os;
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(" \"").append(cpu).append("\" \"").append(os).append('"');
    }
}
