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

import org.wildfly.common.Assert;

/**
 *
 */
public final class RRClass {
    public static final int UNKNOWN = -1;
    public static final int IN = 1;
    public static final int CH = 3;
    public static final int HS = 4;
    public static final int NONE = 254;
    public static final int ANY = 255;

    public static int fromString(final String str) {
        Assert.checkNotNullParam("str", str);
        switch (str) {
            case "IN": return IN;
            case "CH": return CH;
            case "HS": return HS;
            case "NONE": return NONE;
            case "ANY": return ANY;
            default: try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return UNKNOWN;
            }
        }
    }

    public static String toString(final int rrClass) {
        switch (rrClass) {
            case IN: return "IN";
            case CH: return "CH";
            case HS: return "HS";
            case NONE: return "NONE";
            case ANY: return "ANY";
            case UNKNOWN: return "UNKNOWN";
            default: return Integer.toString(rrClass);
        }
    }
}
