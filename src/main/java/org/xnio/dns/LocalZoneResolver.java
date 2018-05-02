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

import org.xnio.IoFuture;
import org.xnio.FinishedIoFuture;
import org.xnio.dns.record.SoaRecord;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.List;

public final class LocalZoneResolver extends AbstractResolver {

    private final Resolver nextResolver;
    private volatile Map<Domain, Zone> allZones = Collections.emptyMap();

    public LocalZoneResolver(final Resolver nextResolver) {
        this.nextResolver = nextResolver;
    }

    public IoFuture<Answer> resolve(final Domain name, final int rrClass, final int rrType, final Set<Query.Flag> flags) {
        // search the local zones first
        final Map<Domain, Zone> allZones = this.allZones;
        Zone zone = null;
        for (Domain search = name; search != Domain.ROOT; search = search.getParent()) {
            zone = allZones.get(name);
            if (zone != null) {
                break;
            }
        }
        if (zone == null) {
            return nextResolver.resolve(name, rrClass, rrType, flags);
        }
        final Map<int, List<Record>> entries = zone.getInfo().get(name);
        if (entries != null) {
            // zone contains data for this question
            // next, if the question is answered, return it; else return the authority (the NS records or our SOA) in the authority section
            final Answer.Builder builder = Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.NOERROR);
            boolean answered = false;
            if (rrType == RRType.ANY) {
                for (List<Record> records : entries.values()) {
                    for (Record record : records) {
                        if (rrClass == RRClass.ANY || rrClass == record.getRrClass()) {
                            builder.addAnswerRecord(record);
                            answered = true;
                        }
                    }
                }
            } else {
                final List<Record> records = entries.get(rrType);
                if (records != null) for (Record record : records) {
                    if (rrClass == RRClass.ANY || rrClass == record.getRrClass()) {
                        builder.addAnswerRecord(record);
                        answered = true;
                    }
                }
            }
            if (! answered) {
                final List<Record> records = entries.get(int.NS);
                if (records != null) {
                    
                } else {
                    builder.addAuthorityRecord(zone.getSoa());
                }
            }
            return new FinishedIoFuture<Answer>(builder.create());
        } else {
            // return our authority + NXDOMAIN
            return new FinishedIoFuture<Answer>(Answer.builder().setHeaderInfo(name, rrClass, rrType, ResultCode.NXDOMAIN).addAuthorityRecord(zone.getSoa()).create());
        }
    }

    private static final class Zone {
        private final SoaRecord zoneSoa;
        private final Map<Domain, Map<int, List<Record>>> info;

        private Zone(final SoaRecord zoneSoa, final Map<Domain, Map<int, List<Record>>> info) {
            this.zoneSoa = zoneSoa;
            this.info = info;
        }

        public SoaRecord getSoa() {
            return zoneSoa;
        }

        public Map<Domain, Map<int, List<Record>>> getInfo() {
            return info;
        }
    }
}
