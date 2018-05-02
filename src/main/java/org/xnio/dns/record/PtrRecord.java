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
 * A record of type {@link RRType#PTR}.
 */
public class PtrRecord extends Record {

    private static final long serialVersionUID = 2883759267175514233L;

    private final Domain target;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public PtrRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
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
    public PtrRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        this(name, rrClass, ttlSpec, Domain.fromString(recordString));
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrclass the record class
     * @param ttlSpec the TTL spec
     * @param target the target domain name
     */
    public PtrRecord(final Domain name, final int rrclass, final TTLSpec ttlSpec, final Domain target) {
        super(name, rrclass, RRType.PTR, ttlSpec);
        this.target = target;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param target the target domain name
     */
    public PtrRecord(final Domain name, final TTLSpec ttlSpec, final Domain target) {
        this(name, RRClass.IN, ttlSpec, target);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param target the target domain name
     */
    public PtrRecord(final Domain name, final Domain target) {
        this(name, TTLSpec.ZERO, target);
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(target);
    }

    /**
     * Get the target domain name.
     *
     * @return the target
     */
    public Domain getTarget() {
        return target;
    }
}