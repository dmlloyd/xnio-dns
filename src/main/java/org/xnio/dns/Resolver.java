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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

import org.xnio.IoFuture;

/**
 * A DNS resolver.
 */
public interface Resolver {
    /**
     * Execute a DNS query.
     *
     * @param query the DNS query (must not be {@code null})
     * @return the future answer (must not be {@code null})
     */
    IoFuture<Answer> resolve(Query query);

    /**
     * Execute a DNS query.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param rrType the resource record type
     * @param queryFlags the query flags
     * @return the future answer
     */
    default IoFuture<Answer> resolve(Domain name, int rrClass, int rrType, int queryFlags) {
        return resolve(new Query(name, rrClass, rrType, queryFlags));
    }

    /**
     * Execute a DNS query.
     *
     * @param name the domain name
     * @param rrClass the resource record class
     * @param rrType the resource record type
     * @return the future answer
     */
    default IoFuture<Answer> resolve(Domain name, int rrClass, int rrType) {
        return resolve(new Query(name, rrClass, rrType, 0));
    }

    /**
     * Execute a DNS query.  A resource record class of {@link org.xnio.dns.RRClass#IN} is assumed.
     *
     * @param name the domain name
     * @param rrType the resource record type
     * @return the future answer
     */
    default IoFuture<Answer> resolve(Domain name, int rrType) {
        return resolve(new Query(name, RRClass.IN, rrType, 0));
    }

    /**
     * Get all the IP addresses (IPv4 or IPv6) for the given domain name.
     *
     * @param name the domain name
     * @return the future list of IP addresses
     */
    IoFuture<List<InetAddress>> resolveAllInet(Domain name);

    /**
     * Get an IP address (IPv4 or IPv6) for the given domain name.
     *
     * @param name the domain name
     * @return the future IP address
     */
    IoFuture<InetAddress> resolveInet(Domain name);

    /**
     * Get all the IPv4 addresses for the given domain name.
     *
     * @param name the domain name
     * @return the future list of IP addresses
     */
    IoFuture<List<Inet4Address>> resolveAllInet4(Domain name);

    /**
     * Get all the IPv4 addresses for the given domain name.
     *
     * @param name the domain name
     * @return the future IP address
     */
    IoFuture<Inet4Address> resolveInet4(Domain name);

    /**
     * Get all the IPv6 addresses for the given domain name.
     *
     * @param name the domain name
     * @return the future list of IP addresses
     */
    IoFuture<List<Inet6Address>> resolveAllInet6(Domain name);

    /**
     * Get all the IPv6 addresses for the given domain name.
     *
     * @param name the domain name
     * @return the future IP address
     */
    IoFuture<Inet6Address> resolveInet6(Domain name);

    /**
     * Perform a reverse lookup of an IP address.
     *
     * @param address the IP address (IPv4 or IPv6)
     * @return the future domain name
     */
    IoFuture<Domain> resolveReverse(InetAddress address);

    /**
     * Perform a text-record lookup of a domain name.
     *
     * @param name the domain name
     * @return the future list of text record data
     */
    IoFuture<List<String>> resolveText(Domain name);
}
