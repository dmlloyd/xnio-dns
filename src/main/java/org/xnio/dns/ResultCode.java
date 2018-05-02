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

/**
 * The possible result code values.
 */
public final class ResultCode {
    public static final int NOERROR = 0;
    public static final int FORMAT_ERROR = 1;
    public static final int SERVER_FAILURE = 2;
    public static final int NXDOMAIN = 3;
    public static final int NOT_IMPLEMENTED = 4;
    public static final int REFUSED = 5;
    public static final int UNKNOWN = -1;

    public static String toString(int resultCode) {
        switch (resultCode) {
            case NOERROR: return "NOERROR";
            case FORMAT_ERROR: return "FORMAT_ERROR";
            case SERVER_FAILURE: return "SERVER_FAILURE";
            case NXDOMAIN: return "NXDOMAIN";
            case NOT_IMPLEMENTED: return "NOT_IMPLEMENTED";
            case REFUSED: return "REFUSED";
            case UNKNOWN: return "UNKNOWN";
            default: return Integer.toString(resultCode);
        }
    }
}
