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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.wildfly.common.net.Inet;
import org.xnio.FinishedIoFuture;
import org.xnio.IoFuture;
import org.xnio.dns.record.AaaaRecord;
import org.xnio.dns.record.ARecord;

/**
 * A HOSTS file resolver.
 */
public final class HostsResolver extends AbstractResolver {
    private volatile Map<Domain, List<InetAddress>> hostsMap = Collections.emptyMap();
    private final Resolver next;

    public HostsResolver(final Resolver next) {
        this.next = next;
    }

    private List<InetAddress> newList(InetAddress first) {
        final ArrayList<InetAddress> list = new ArrayList<InetAddress>();
        list.add(first);
        return list;
    }

    private void doInitialize(BufferedReader source) throws IOException {
        Map<Domain, List<InetAddress>> hostsMap = new HashMap<Domain, List<InetAddress>>();
        String line;
        while ((line = source.readLine()) != null) {
            int hi = line.indexOf('#');
            if (hi != -1) {
                line = line.substring(0, hi);
            }
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            final String[] parts = line.split("\\s++");
            final int len = parts.length;
            if (len >= 1) {
                String address = parts[0];
                for (int i = 1; i < len; i ++) {
                    final String hostName = parts[i];
                    final Domain domain = Domain.fromString(hostName);
                    final List<InetAddress> list = hostsMap.get(domain);
                    final InetAddress parsed = Inet.parseInetAddressOrFail(address, domain.getHostName());
                    if (list == null) {
                        hostsMap.put(domain, newList(parsed));
                    } else {
                        list.add(parsed);
                    }
                }
            }
        }
        this.hostsMap = hostsMap;
    }

    /**
     * Replace the current mapping with the contents of a new HOSTS file.
     *
     * @param source the hosts file source
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if an IP address in the hosts file was invalid
     */
    public void initialize(Reader source) throws IOException {
        if (source instanceof BufferedReader) {
            doInitialize((BufferedReader) source);
        } else {
            doInitialize(new BufferedReader(source));
        }
    }

    /**
     * Replace the current mapping with the contents of a new HOSTS file.
     *
     * @param file the file
     * @param encoding the file encoding, or {@code null} to use the platform encoding
     * @throws IOException if an I/O error occurs
     */
    public void initialize(File file, String encoding) throws IOException {
        final FileInputStream is = new FileInputStream(file);
        final InputStreamReader reader = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
        initialize(reader);
    }

    /**
     * Replace the current mapping with the contents of a new HOSTS file.
     *
     * @param fileName the file name
     * @param encoding the file encoding, or {@code null} to use the platform encoding
     * @throws IOException if an I/O error occurs
     */
    public void initialize(String fileName, String encoding) throws IOException {
        initialize(new File(fileName), encoding);
    }

    /**
     * {@inheritDoc}  This instance queries the HOSTS cache, and if no records are found, the request is forwarded to
     * the next resolver in the chain.
     */
    public IoFuture<Answer> resolve(final Domain name, final RRClass rrClass, final RRType rrType, final Set<Query.Flag> flags) {
        if (rrClass == RRClass.IN || rrClass == RRClass.ANY) {
            final List<InetAddress> list = hostsMap.get(name);
            if (list != null) {
                final Answer.Builder builder = Answer.builder();
                builder.setQueryDomain(name).setQueryRRClass(rrClass).setQueryRRType(rrType).setResultCode(ResultCode.NOERROR);
                for (InetAddress address : list) {
                    if (address instanceof Inet4Address && (rrType == RRType.A || rrType == RRType.ANY)) {
                        builder.addAnswerRecord(new ARecord(name, TTLSpec.ZERO, (Inet4Address) address));
                    } else if (address instanceof Inet6Address && (rrType == RRType.AAAA || rrType == RRType.ANY)) {
                        builder.addAnswerRecord(new AaaaRecord(name, TTLSpec.ZERO, (Inet6Address) address));
                    }
                }
                return new FinishedIoFuture<Answer>(builder.create());
            }
        }
        return next.resolve(name, rrClass, rrType, flags);
    }
}
