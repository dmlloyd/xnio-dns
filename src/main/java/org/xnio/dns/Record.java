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

import static org.xnio.dns.RRType.A;
import static org.xnio.dns.RRType.AAAA;
import static org.xnio.dns.RRType.CNAME;
import static org.xnio.dns.RRType.HINFO;
import static org.xnio.dns.RRType.MX;
import static org.xnio.dns.RRType.NS;
import static org.xnio.dns.RRType.PTR;
import static org.xnio.dns.RRType.SOA;
import static org.xnio.dns.RRType.TXT;
import static org.xnio.dns.RRType.WKS;

import java.io.Serializable;
import java.nio.ByteBuffer;
import org.xnio.dns.record.AaaaRecord;
import org.xnio.dns.record.ARecord;
import org.xnio.dns.record.CNameRecord;
import org.xnio.dns.record.HInfoRecord;
import org.xnio.dns.record.MxRecord;
import org.xnio.dns.record.NsRecord;
import org.xnio.dns.record.PtrRecord;
import org.xnio.dns.record.SoaRecord;
import org.xnio.dns.record.TxtRecord;
import org.xnio.dns.record.UnknownRecord;
import org.xnio.dns.record.WksRecord;
import org.xnio.Buffers;

/**
 * A resource record.
 */
public abstract class Record implements Serializable {

    private static final long serialVersionUID = 132819048908309214L;

    private final Domain name;
    private final int rrClass;
    private final int rrType;
    private final TTLSpec ttlSpec;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param rrType the record type
     * @param ttlSpec the TTL of this record
     */
    protected Record(final Domain name, final int rrClass, final int rrType, final TTLSpec ttlSpec) {
        this.name = name;
        this.rrType = rrType;
        this.rrClass = rrClass;
        this.ttlSpec = ttlSpec;
    }

    /**
     * Get the domain name for this record.
     *
     * @return the domain name
     */
    public Domain getName() {
        return name;
    }

    /**
     * Get the class of this record.
     *
     * @return the resource record class
     */
    public int getRrClass() {
        return rrClass;
    }

    /**
     * Get the type of this record.
     *
     * @return the resource record type
     */
    public int getRrType() {
        return rrType;
    }

    /**
     * Get the TTL of this record.
     *
     * @return the TTL
     */
    public TTLSpec getTtlSpec() {
        return ttlSpec;
    }

    /**
     * Get a clone of this record with the given TTL specification.
     *
     * @param ttlSpec the TTL specification (must not be {@code null})
     * @return the cloned record (not {@code null})
     */
    public abstract Record withTTLSpec(TTLSpec ttlSpec);

    /**
     * Append any record-specific RR data to the string builder.
     *
     * @param builder the builder
     */
    protected void appendRData(StringBuilder builder) {}

    /**
     * Get the string representation of this record.
     *
     * @return the string representation
     */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(name).append(' ').append(getTtlSpec().getTtl()).append(' ').append(RRClass.toString(rrClass)).append(' ').append(RRType.toString(rrType));
        appendRData(builder);
        return builder.toString();
    }

    /**
     * Construct an instance from bytes in a byte buffer.
     *
     * @param buffer the source buffer
     * @return the resource record
     */
    public static Record fromBytes(final ByteBuffer buffer, final long queryTimeStamp) {
        final Domain name = Domain.fromBytes(buffer);
        final int rrType = buffer.getShort() & 0xffff;
        final int rrClass = buffer.getShort() & 0xffff;
        final TTLSpec ttlSpec = TTLSpec.createVariable(queryTimeStamp + (buffer.getInt() & 0xffff_ffffL));
        final ByteBuffer recordBuffer = Buffers.slice(buffer, buffer.getShort() & 0xffff);
        switch (rrType) {
            case AAAA:  return new AaaaRecord (name, rrClass, ttlSpec, recordBuffer);
            case A:     return new ARecord    (name, rrClass, ttlSpec, recordBuffer);
            case CNAME: return new CNameRecord(name, rrClass, ttlSpec, recordBuffer);
            case HINFO: return new HInfoRecord(name, rrClass, ttlSpec, recordBuffer);
            case MX:    return new MxRecord   (name, rrClass, ttlSpec, recordBuffer);
            case NS:    return new NsRecord   (name, rrClass, ttlSpec, recordBuffer);
            case PTR:   return new PtrRecord  (name, rrClass, ttlSpec, recordBuffer);
            case SOA:   return new SoaRecord  (name, rrClass, ttlSpec, recordBuffer);
            case TXT:   return new TxtRecord  (name, rrClass, ttlSpec, recordBuffer);
            case WKS:   return new WksRecord  (name, rrClass, ttlSpec, recordBuffer);

            default:    return new UnknownRecord(name, rrClass, rrType, ttlSpec, recordBuffer);
        }
    }
}
