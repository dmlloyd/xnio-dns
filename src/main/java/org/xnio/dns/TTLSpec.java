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

import static java.lang.Math.min;
import static java.lang.Math.max;
import java.io.Serializable;

/**
 * A resource record time-to-live specification.  Such a specification may be relative or absolute.
 */
public abstract class TTLSpec implements Serializable {

    private static final long serialVersionUID = 9017672320381434104L;

    private TTLSpec() {}

    /**
     * Get the timestamp (in milliseconds, ala {@link System#currentTimeMillis()}) at which this record will be expired.
     *
     * @return the timestamp
     */
    public abstract long getEol();

    /**
     * Get the remaining time-to-live in seconds.  A fixed TTL spec will always return the same value, whereas a variable TTL
     * spec will count down towards an absolute time.
     *
     * @return the remaining time-to-live in seconds
     */
    public abstract int getTtl();

    /**
     * Determine whether this specification has a fixed or variable (decreasing) TTL.
     *
     * @return {@code true} if this instance has a fixed TTL, {@code false} if it has a variable TTL
     */
    public abstract boolean isFixed();

    /**
     * Determine whether this TTL specification is expired.  A fixed TTL specification never expires.
     *
     * @return {@code true} if this TTL is expired
     */
    public abstract boolean isExpired();

    /**
     * Create a variable (decreasing) TTL with the given EOL.
     *
     * @param eol the EOL timestamp
     * @return the specification
     */
    public static TTLSpec createVariable(long eol) {
        return new VariableTtlSpec(eol);
    }

    /**
     * Create a fixed TTL with the given lifetime.
     *
     * @param ttl the TTL value, in seconds
     * @return the specification
     */
    public static TTLSpec createFixed(int ttl) {
        return new FixedTtlSpec(ttl);
    }

    /**
     * A fixed TTL with a time of zero.
     */
    public static final TTLSpec ZERO = createFixed(0);

    static final class FixedTtlSpec extends TTLSpec {

        private static final long serialVersionUID = -2365149842752581181L;
        private final int ttl;

        private FixedTtlSpec(final int ttl) {
            this.ttl = ttl;
        }

        public long getEol() {
            return System.currentTimeMillis() + ((long)ttl * 1000L);
        }

        public int getTtl() {
            return ttl;
        }

        public boolean isFixed() {
            return true;
        }

        public boolean isExpired() {
            return false;
        }
    }

    static final class VariableTtlSpec extends TTLSpec {

        private static final long serialVersionUID = -3843994141080180888L;
        private final long eol;

        private VariableTtlSpec(final long eol) {
            this.eol = eol;
        }

        public long getEol() {
            return eol;
        }

        public int getTtl() {
            return (int) min((long) Integer.MAX_VALUE, max(0L, (eol - System.currentTimeMillis()) / 1000L));
        }

        public boolean isFixed() {
            return false;
        }

        public boolean isExpired() {
            return eol <= System.currentTimeMillis();
        }
    }
}
