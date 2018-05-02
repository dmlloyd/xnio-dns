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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.wildfly.common.net.Inet;
import org.xnio.dns.Record;
import org.xnio.dns.RRClass;
import org.xnio.dns.RRType;
import org.xnio.dns.Domain;
import org.xnio.dns.TTLSpec;
import org.xnio.Buffers;

/**
 * A record of type {@link RRType#AAAA}.
 */
public class AaaaRecord extends Record {
    private static final long serialVersionUID = -8702941004736168982L;

    private final Inet6Address address;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public AaaaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        super(name, rrClass, RRType.AAAA, ttlSpec);
        byte[] bytes = Buffers.take(recordBuffer, 16);
        try {
            address = (Inet6Address) InetAddress.getByAddress(name.getHostName(), bytes);
        } catch (UnknownHostException e) {
            // not possible
            throw new IllegalStateException(e);
        }
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the string from which the record data should be built
     */
    public AaaaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        super(name, rrClass, RRType.AAAA, ttlSpec);
        address = Inet.parseInet6AddressOrFail(recordString, name.getHostName());
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param address the IPv6 address
     */
    public AaaaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final Inet6Address address) {
        super(name, rrClass, RRType.AAAA, ttlSpec);
        this.address = address;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param address the IPv6 address
     */
    public AaaaRecord(final Domain name, final TTLSpec ttlSpec, final Inet6Address address) {
        this(name, RRClass.IN, ttlSpec, address);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param address the IPv6 address
     */
    public AaaaRecord(final Domain name, final Inet6Address address) {
        this(name, TTLSpec.ZERO, address);
    }

    /**
     * Get the IP address.
     *
     * @return the IP address
     */
    public Inet6Address getAddress() {
        return address;
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(address.getHostAddress());
    }
}