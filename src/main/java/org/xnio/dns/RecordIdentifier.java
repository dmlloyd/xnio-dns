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

final class RecordIdentifier {
    private final int rrClass;
    private final int rrType;
    private final Domain domain;
    private final int hashCode;

    RecordIdentifier(final Domain domain, final int rrClass, final int rrType) {
        this.rrClass = rrClass;
        this.rrType = rrType;
        this.domain = domain;
        int result = rrClass;
        result = 31 * result + rrType;
        result = 31 * result + domain.hashCode();
        hashCode = result;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (! (o instanceof RecordIdentifier)) return false;
        final RecordIdentifier cacheKey = (RecordIdentifier) o;
        if (hashCode != cacheKey.hashCode) return false;
        if (!domain.equals(cacheKey.domain)) return false;
        if (rrClass != cacheKey.rrClass) return false;
        if (rrType != cacheKey.rrType) return false;
        return true;
    }

    public int hashCode() {
        return hashCode;
    }
}
