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
 * A record of type {@link RRType#MX}.
 */
public class MxRecord extends Record {

    private static final long serialVersionUID = -8404288502462059611L;

    private final int preference;
    private final Domain exchanger;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public MxRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        this(name, rrClass, ttlSpec, recordBuffer.getShort() & 0xffff, Domain.fromBytes(recordBuffer));
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the string from which the record data should be built
     */
    public MxRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        super(name, rrClass, RRType.MX, ttlSpec);
        final int spc = recordString.indexOf(' ');
        if (spc == -1) {
            throw new IllegalArgumentException("Invalid record data format");
        }
        preference = Integer.parseInt(recordString.substring(0, spc));
        exchanger = Domain.fromString(recordString.substring(spc).trim());
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param ttlSpec the TTL spec
     * @param preference the exchanger preference value
     * @param exchanger the exchanger domain
     */
    public MxRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final int preference, final Domain exchanger) {
        super(name, rrClass, RRType.MX, ttlSpec);
        this.preference = preference;
        this.exchanger = exchanger;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param preference the exchanger preference value
     * @param exchanger the exchanger domain
     */
    public MxRecord(final Domain name, final TTLSpec ttlSpec, final int preference, final Domain exchanger) {
        this(name, RRClass.IN, ttlSpec, preference, exchanger);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param preference the exchanger preference value
     * @param exchanger the exchanger domain
     */
    public MxRecord(final Domain name, final int preference, final Domain exchanger) {
        this(name, TTLSpec.ZERO, preference, exchanger);
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(preference).append(' ').append(exchanger);
    }

    /**
     * Get the preference value.
     *
     * @return the preference value
     */
    public int getPreference() {
        return preference;
    }

    /**
     * Get the exchanger name.
     *
     * @return the exchanger name
     */
    public Domain getExchanger() {
        return exchanger;
    }
}
