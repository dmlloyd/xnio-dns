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
import java.nio.ByteBuffer;

/**
 * A record of type {@link org.xnio.dns.RRType#NS}.
 */
public class NsRecord extends Record {

    private static final long serialVersionUID = 6806325778477267585L;

    private final Domain server;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public NsRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        this(name, rrClass, ttlSpec, Domain.fromBytes(recordBuffer));
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the string from which the record data should be built
     */
    public NsRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        this(name, rrClass, ttlSpec, Domain.fromString(recordString));
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrclass the record class
     * @param ttlSpec the TTL spec
     * @param server the name server domain
     */
    public NsRecord(final Domain name, final int rrclass, final TTLSpec ttlSpec, final Domain server) {
        super(name, rrclass, RRType.NS, ttlSpec);
        this.server = server;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param server the name server domain
     */
    public NsRecord(final Domain name, final TTLSpec ttlSpec, final Domain server) {
        this(name, RRClass.IN, ttlSpec, server);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param server the name server domain
     */
    public NsRecord(final Domain name, final Domain server) {
        this(name, TTLSpec.ZERO, server);
    }

    /**
     * Get the name server domain of this NS record.
     *
     * @return the name server domain
     */
    public Domain getServer() {
        return server;
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(server);
    }
}