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
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.nio.ByteBuffer;

public class WksRecord extends Record {

    private static final long serialVersionUID = -7273167850367070122L;

    public enum Protocol {
        UNKNOWN,
        TCP,
        UDP,
    }

    private final Protocol protocol;
    private final int[] ports;

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordString the string from which the record data should be built
     */
    public WksRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final String recordString) {
        super(name, rrClass, RRType.WKS, ttlSpec);
        final StringTokenizer tok = new StringTokenizer(recordString, " \t\n\r\f", false);
        try {
            final String proto = tok.nextToken();
            protocol = Protocol.valueOf(proto);
            final ArrayList<String> portStrings = new ArrayList<String>();
            while (tok.hasMoreTokens()) {
                portStrings.add(tok.nextToken());
            }
            final int[] ports = new int[portStrings.size()];
            int i = 0;
            for (String portString : portStrings) {
                final int portNum = Integer.parseInt(portString, 10);
                if (portNum < 0 || portNum > 65535) {
                    throw new IllegalArgumentException("Invalid port number given");
                }
                ports[i++] = portNum;
            }
            this.ports = ports;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid data for WKS record", e);
        }
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param recordBuffer the buffer from which the record data should be built
     */
    public WksRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final ByteBuffer recordBuffer) {
        super(name, rrClass, RRType.WKS, ttlSpec);
        switch (recordBuffer.get() & 0xff) {
            case 6: protocol = Protocol.TCP; break;
            case 17: protocol = Protocol.UDP; break;
            default: protocol = Protocol.UNKNOWN; break;
        }
        int i = 0;
        final int start = recordBuffer.position();
        while (recordBuffer.hasRemaining()) {
            final int val = recordBuffer.get() & 0xff;
            i += Integer.bitCount(val);
        }
        recordBuffer.position(start);
        int j = 0, cnt = 0;
        final int[] ports = new int[i];
        while (recordBuffer.hasRemaining()) {
            final int val = recordBuffer.get();
            if ((val & 0x01) != 0) ports[j++] = cnt;
            if ((val & 0x02) != 0) ports[j++] = cnt + 1;
            if ((val & 0x04) != 0) ports[j++] = cnt + 2;
            if ((val & 0x08) != 0) ports[j++] = cnt + 3;
            if ((val & 0x10) != 0) ports[j++] = cnt + 4;
            if ((val & 0x20) != 0) ports[j++] = cnt + 5;
            if ((val & 0x40) != 0) ports[j++] = cnt + 6;
            if ((val & 0x80) != 0) ports[j++] = cnt + 7;
            cnt += 8;
        }
        this.ports = ports;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param ttlSpec the TTL spec
     * @param protocol the protocol
     * @param ports the list of port numbers
     */
    public WksRecord(final Domain name, final int rrClass, final TTLSpec ttlSpec, final Protocol protocol, final int... ports) {
        super(name, rrClass, RRType.WKS, ttlSpec);
        this.protocol = protocol;
        final int[] copiedPorts = ports.clone();
        Arrays.sort(copiedPorts);
        int prev = -1;
        for (int i = 0; i < copiedPorts.length; i++) {
            int port = copiedPorts[i];
            if (port < 0 || port > 65536) {
                throw new IllegalArgumentException("Invalid port number " + port + " specified at index " + i);
            }
            if (port == prev) {
                throw new IllegalArgumentException("Duplicate port number " + port + " specified at index " + i);
            }
            prev = port;
        }
        this.ports = copiedPorts;
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param ttlSpec the TTL spec
     * @param protocol the protocol
     * @param ports the set of port numbers
     */
    public WksRecord(final Domain name, final TTLSpec ttlSpec, final Protocol protocol, final int... ports) {
        this(name, RRClass.IN, ttlSpec, protocol, ports);
    }

    /**
     * Construct a new instance.
     *
     * @param name the domain name
     * @param protocol the protocol
     * @param ports the set of port numbers
     */
    public WksRecord(final Domain name, final Protocol protocol, final int... ports) {
        this(name, RRClass.IN, TTLSpec.ZERO, protocol, ports);
    }

    /**
     * Return a copy of the port numbers in sorted order.
     *
     * @return the sorted ports
     */
    public int[] getPorts() {
        return ports.clone();
    }

    /**
     * Get the protocol for this well-known service record.
     *
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /** {@inheritDoc} */
    protected void appendRData(final StringBuilder builder) {
        builder.append(' ').append(protocol);
        for (int port : ports) {
            builder.append(' ').append(port);
        }
    }
}
