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

import java.util.Map;

import org.xnio.dns.record.SoaRecord;

/**
 *
 */
public final class Zone {
    private final SoaRecord soaRecord;
    private final Map<RecordIdentifier, Record> recordMap;

    Zone(final SoaRecord soaRecord, final Map<RecordIdentifier, Record> recordMap) {
        this.soaRecord = soaRecord;
        this.recordMap = recordMap;
    }

    public SoaRecord getSoaRecord() {
        return soaRecord;
    }

    public Record getRecord(Domain name, int rrType, int rrClass) {

    }
}
