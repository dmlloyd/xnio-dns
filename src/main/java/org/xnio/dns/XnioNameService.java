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

import sun.net.spi.nameservice.NameService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An implementation of the Sun JDK name service.  Install by setting
 * one of the the {@code "sun.net.spi.nameservice.provider.X"} system properties, where X is a sequential
 * integer, to {@code "dns,xnio"}.
 * <p>
 * Be sure that the resolver is also set appropriately sometime in early boot.
 */
public final class XnioNameService implements NameService {
    private static AtomicReference<Resolver> resolverRef = new AtomicReference<Resolver>(new EmptyResolver());

    /**
     * Set the global resolver that the name service should use.
     *
     * @param resolver the resolver to use
     */
    public static void setGlobalResolver(Resolver resolver) {
        if (resolver == null) {
            throw new NullPointerException("resolver is null");
        }
        resolverRef.set(resolver);
    }

    /**
     * Look up all hosts by name.
     *
     * @param hostName the host name
     * @return an array of addresses for the host name
     * @throws UnknownHostException if there are no names for this host, or if resolution fails
     */
    public InetAddress[] lookupAllHostAddr(final String hostName) throws UnknownHostException {
        try {
            final Resolver resolver = resolverRef.get();
            final List<InetAddress> addressList = resolver.resolveAllInet(Domain.fromString(hostName)).get();
            final int len = addressList.size();
            if (len == 0) {
                throw new UnknownHostException(hostName + ": no valid DNS records");
            }
            return addressList.toArray(new InetAddress[len]);
        } catch (IOException e) {
            final UnknownHostException uhe = new UnknownHostException("Failed to resolve address");
            uhe.initCause(e);
            throw uhe;
        }
    }

    /**
     * Get the name of the host with the given IP address.
     *
     * @param bytes the address bytes
     * @return the host name
     * @throws UnknownHostException if there is no host name for this IP address
     */
    public String getHostByAddr(final byte[] bytes) throws UnknownHostException {
        final Resolver resolver = resolverRef.get();
        try {
            final Domain domain = resolver.resolveReverse(InetAddress.getByAddress("unresolved", bytes)).get();
            if (domain == null) {
                throw new UnknownHostException();
            }
            return domain.getHostName();
        } catch (IOException e) {
            final UnknownHostException uhe = new UnknownHostException("Failed to resolve address");
            uhe.initCause(e);
            throw uhe;
        }
    }
}
