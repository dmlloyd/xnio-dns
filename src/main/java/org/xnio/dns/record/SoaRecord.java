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
import org.xnio.dns.RRType;
import org.xnio.dns.TTLSpec;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

/**
 * A record of type {@link RRType#SOA}.
 */
public class SoaRecord extends Record {

    private static final long serialVersionUID = 4582740248500266324L;

    private final Domain mName;
    private final Domain rName;
    private final int serial;
    private final int refresh;
    private final int retry;
    private final int expire;
    private final TTLSpec minimum;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public SoaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        super(name, rrClass, RRType.SOA, ttlSpec);
        mName = Domain.fromBytes(recordBuffer);
        rName = Domain.fromBytes(recordBuffer);
        serial = recordBuffer.getInt();
        refresh = recordBuffer.getInt();
        retry = recordBuffer.getInt();
        expire = recordBuffer.getInt();
        minimum = TTLSpec.createFixed(recordBuffer.getInt());
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the buffer from which the record data should be built
     */
    public SoaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        super(name, rrClass, RRType.SOA, ttlSpec);
        final StringTokenizer tok = new StringTokenizer(recordString, " \t\n\r\f", false);
        try {
            mName = Domain.fromString(tok.nextToken());
            rName = Domain.fromString(tok.nextToken());
            serial = Integer.parseInt(tok.nextToken());
            refresh = Integer.parseInt(tok.nextToken());
            retry = Integer.parseInt(tok.nextToken());
            expire = Integer.parseInt(tok.nextToken());
            minimum = TTLSpec.createFixed(Integer.parseInt(tok.nextToken()));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Malformed record data string");
        }
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the record class
     * @param ttlSpec the TTL of the record
     * @param mName the domain name of the name server that was the primary source of data for this domain
     * @param rName the mailbox of the person responsible for this domain
     * @param serial the serial number (always treat as unsigned)
     * @param refresh the zone refresh time
     * @param retry the zone refresh failure retry time
     * @param expire the zone expiration time
     * @param minimum the minimum TTL
     */
    public SoaRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final Domain mName, final Domain rName, final int serial, final int refresh, final int retry, final int expire, final TTLSpec minimum) {
        super(name, rrClass, RRType.SOA, ttlSpec);
        this.mName = mName;
        this.rName = rName;
        this.serial = serial;
        this.refresh = refresh;
        this.retry = retry;
        this.expire = expire;
        this.minimum = minimum;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL of the record
     * @param mName the domain name of the name server that was the primary source of data for this domain
     * @param rName the mailbox of the person responsible for this domain
     * @param serial the serial number (always treat as unsigned)
     * @param refresh the zone refresh time
     * @param retry the zone refresh failure retry time
     * @param expire the zone expiration time
     * @param minimum the minimum TTL
     */
    public SoaRecord(final Domain name, final TTLSpec ttlSpec, final Domain mName, final Domain rName, final int serial, final int refresh, final int retry, final int expire, final TTLSpec minimum) {
        this(name, RRClass.IN, ttlSpec, mName, rName, serial, refresh, retry, expire, minimum);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param mName the domain name of the name server that was the primary source of data for this domain
     * @param rName the mailbox of the person responsible for this domain
     * @param serial the serial number (always treat as unsigned)
     * @param refresh the zone refresh time
     * @param retry the zone refresh failure retry time
     * @param expire the zone expiration time
     * @param minimum the minimum TTL
     */
    public SoaRecord(final Domain name, final Domain mName, final Domain rName, final int serial, final int refresh, final int retry, final int expire, final TTLSpec minimum) {
        this(name, RRClass.IN, TTLSpec.ZERO, mName, rName, serial, refresh, retry, expire, minimum);
    }

    /**
     * Get the MNAME, which is the domain name of the name server that was the primary source of data for this domain.
     *
     * @return the MNAME
     */
    public Domain getMName() {
        return mName;
    }

    /**
     * Get the RNAME, which is the mailbox of the person responsible for this domain (but in domain format).
     *
     * @return the RNAME
     */
    public Domain getRName() {
        return rName;
    }

    /**
     * Get the serial number of this domain.  This value is an unsigned 32-bit integer.
     *
     * @return the serial number
     */
    public long getSerial() {
        return serial & 0xffffffffL;
    }

    /**
     * Get the zone refresh time (in seconds).
     *
     * @return the zone refresh time
     */
    public int getRefresh() {
        return refresh;
    }

    /**
     * Get the zone refresh time failure retry time (in seconds).
     *
     * @return the zone refresh time
     */
    public int getRetry() {
        return retry;
    }

    /**
     * Get the zone expiration time (in seconds).
     *
     * @return the zone expiration time
     */
    public int getExpire() {
        return expire;
    }

    /**
     * Get the zone minimum TTL.
     *
     * @return the zone minimum TTL
     */
    public TTLSpec getMinimum() {
        return minimum;
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(mName).append(' ').append(rName).append(" ( ").append(serial & 0xffffffffL);
        builder.append(' ').append(refresh).append(' ').append(retry).append(' ').append(expire).append(' ').append(minimum);
        builder.append(" )");
    }
}
