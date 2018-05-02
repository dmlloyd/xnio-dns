/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.xnio.Bits;

/**
 *
 */
public final class Query {
    private final Domain domain;
    private final int rrClass;
    private final int rrType;
    private final int queryFlags;

    public Query(final Domain domain, final int rrClass, final int rrType, final int queryFlags) {
        this.domain = domain;
        this.rrClass = rrClass;
        this.rrType = rrType;
        this.queryFlags = queryFlags;
    }

    public Domain getDomain() {
        return domain;
    }

    public int getRRClass() {
        return rrClass;
    }

    public int getRRType() {
        return rrType;
    }

    public int getQueryFlags() {
        return queryFlags;
    }

    public int hashCode() {
        return ((domain.hashCode() * 19 + rrClass) * 19 + rrType) * 19 + queryFlags;
    }

    public boolean equals(final Object obj) {
        return obj instanceof Query && equals((Query) obj);
    }

    public boolean equals(final Query other) {
        return this == other || other != null && domain.equals(other.domain) && rrClass == other.rrClass && rrType == other.rrType && queryFlags == other.queryFlags;
    }

    public String toString() {
        return String.format("Query for %s %s %s (%s)", domain, RRClass.toString(rrClass), RRType.toString(rrType), Flag.toString(queryFlags));
    }

    public Query withFlags(int newFlags) {
        if (queryFlags == newFlags) return this;
        return new Query(domain, rrClass, rrType, newFlags);
    }

    /**
     * Flags which control how a query is executed.
     */
    public static final class Flag {
        private Flag() {}
        /**
         * Bypass the cache (if any).
         */
        public static final int BYPASS_CACHE = 1 << 0;
        /**
         * Bypass recursion.
         */
        public static final int NO_RECURSION = 1 << 1;
        /**
         * Use TCP.
         */
        public static final int USE_TCP = 1 << 2;

        public static String toString(final int queryFlags) {
            if (Bits.allAreSet(queryFlags, BYPASS_CACHE)) {

            }

        }
    }
}
