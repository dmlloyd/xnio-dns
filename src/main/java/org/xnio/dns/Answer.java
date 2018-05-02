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

import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;

/**
 * A query answer.
 */
public final class Answer {
    private final int resultCode;
    private final List<Record> answerRecords;
    private final List<Record> authorityRecords;
    private final List<Record> additionalRecords;
    private final int flags;

    private Answer(final Domain queryDomain, final int queryRRClass, final int queryRRType, final int resultCode, final List<Record> answerRecords, final List<Record> authorityRecords, final List<Record> additionalRecords, final int flags) {
        this.queryDomain = queryDomain;
        this.queryRRClass = queryRRClass;
        this.queryRRType = queryRRType;
        this.resultCode = resultCode;
        this.answerRecords = answerRecords;
        this.authorityRecords = authorityRecords;
        this.additionalRecords = additionalRecords;
        this.flags = flags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Domain queryDomain;
        private int queryRRClass;
        private int queryRRType;
        private int resultCode;
        private List<Record> answerRecords;
        private List<Record> authorityRecords;
        private List<Record> additionalRecords;
        private int flags;

        public Builder setAnswerRecords(List<Record> list) {
            answerRecords = copy(list);
            return this;
        }

        public Builder setAuthorityRecords(List<Record> list) {
            authorityRecords = copy(list);
            return this;
        }

        public Builder setAdditionalRecords(List<Record> list) {
            additionalRecords = copy(list);
            return this;
        }

        public Builder setHeaderInfo(Answer original) {
            queryDomain = original.queryDomain;
            queryRRClass = original.queryRRClass;
            queryRRType = original.queryRRType;
            resultCode = original.resultCode;
            return this;
        }

        public Builder setHeaderInfo(Domain queryDomain, int queryRRClass, int queryRRType, int resultCode) {
            this.queryDomain = queryDomain;
            this.queryRRClass = queryRRClass;
            this.queryRRType = queryRRType;
            this.resultCode = resultCode;
            return this;
        }

        public Builder setAll(Answer original) {
            setHeaderInfo(original);
            setAnswerRecords(original.answerRecords);
            setAuthorityRecords(original.authorityRecords);
            setAdditionalRecords(original.additionalRecords);
            return this;
        }

        private static List<Record> copy(List<Record> orig) {
            if (orig.isEmpty()) {
                return null;
            }
            final ArrayList<Record> list = new ArrayList<Record>(orig.size());
            for (Record record : orig) {
                if (record == null) {
                    throw new IllegalArgumentException("Null record in original list");
                }
                list.add(record);
            }
            return list;
        }

        public Builder setQueryDomain(Domain queryDomain) {
            this.queryDomain = queryDomain;
            return this;
        }

        public Builder setQueryRRClass(int rrClass) {
            queryRRClass = rrClass;
            return this;
        }

        public Builder setQueryRRType(int rrType) {
            queryRRType = rrType;
            return this;
        }

        public Builder setResultCode(int resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder addAnswerRecord(Record record) {
            if (answerRecords == null) {
                answerRecords = new ArrayList<Record>();
            }
            answerRecords.add(record);
            return this;
        }

        public Builder addAuthorityRecord(Record record) {
            if (authorityRecords == null) {
                authorityRecords = new ArrayList<Record>();
            }
            authorityRecords.add(record);
            return this;
        }

        public Builder addAdditionalRecord(Record record) {
            if (additionalRecords == null) {
                additionalRecords = new ArrayList<Record>();
            }
            additionalRecords.add(record);
            return this;
        }

        public Builder addFlag(int flag) {
            flags |= flag;
            return this;
        }

        public Answer create() {
            return new Answer(
                    queryDomain,
                    queryRRClass,
                    queryRRType, 
                    resultCode,
                answerRecords == null ? Collections.emptyList() : Collections.unmodifiableList(answerRecords),
                authorityRecords == null ? Collections.emptyList() : Collections.unmodifiableList(authorityRecords),
                additionalRecords == null ? Collections.emptyList() : Collections.unmodifiableList(additionalRecords),
                    flags
            );
        }

        public Builder populateFromQuery(final Query query) {
            setQueryDomain(query.getDomain());
            setQueryRRClass(query.getRRClass());
            setQueryRRType(query.getRRType());
            return this;
        }
    }

    /**
     * Get the query domain.
     *
     * @return the query domain
     */
    public Domain getQueryDomain() {
        return queryDomain;
    }

    /**
     * Get the query class.
     *
     * @return the query class
     */
    public int getQueryRRClass() {
        return queryRRClass;
    }

    /**
     * Get the query type.
     *
     * @return the query type
     */
    public int getQueryRRType() {
        return queryRRType;
    }

    /**
     * Get the result code.
     *
     * @return the result code
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Get the answer records.
     *
     * @return the answer records
     */
    public List<Record> getAnswerRecords() {
        return answerRecords;
    }

    /**
     * Get the authority records.
     *
     * @return the authority records
     */
    public List<Record> getAuthorityRecords() {
        return authorityRecords;
    }

    /**
     * Get the additional records.
     *
     * @return the additional records
     */
    public List<Record> getAdditionalRecords() {
        return additionalRecords;
    }

    /**
     * Get the answer flags.
     *
     * @return the answer flag set
     */
    public int getFlags() {
        return flags;
    }

    public final class Flag {
        private Flag() {}

        public static final int AUTHORITATIVE = 1 << 0;
        public static final int TRUNCATED = 1 << 1;
        public static final int RECURSION_DESIRED = 1 << 2;
        public static final int RECURSION_AVAILABLE = 1 << 3;
    }
}
