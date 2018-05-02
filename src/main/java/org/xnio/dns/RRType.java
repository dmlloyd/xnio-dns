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
 *
 */
public final class RRType {
    private RRType() {}

    public static final int UNKNOWN = -1;
    
    /** {@rfc 1035} */
    public static final int A = 1;
    /** {@rfc 1035} */
    public static final int NS = 2;
    /**
     * @deprecated use {@link #MX}
     */
    @Deprecated
    public static final int MD = 3;
    /**
     * @deprecated use {@link #MX}
     */
    @Deprecated
    public static final int MF = 4;
    /** {@rfc 1035} */
    public static final int CNAME = 5;
    /** {@rfc 1035} */
    public static final int SOA = 6;
    /** {@rfc 1035} */
    public static final int MB = 7;
    /** {@rfc 1035} */
    public static final int MG = 8;
    /** {@rfc 1035} */
    public static final int MR = 9;
    /** {@rfc 1035} */
    public static final int NULL = 10;
    /** {@rfc 1035} */
    public static final int WKS = 11;
    /** {@rfc 1035} */
    public static final int PTR = 12;
    /** {@rfc 1035} */
    public static final int HINFO = 13;
    /** {@rfc 1035} */
    public static final int MINFO = 14;
    /** {@rfc 1035} */
    public static final int MX = 15;
    /** {@rfc 1035} */
    public static final int TXT = 16;
    /** {@rfc 1183} */
    public static final int RP = 17;
    /** {@rfc 1183} */
    public static final int AFSDB = 18;
    /** {@rfc 1183} */
    public static final int X25 = 19;
    /** {@rfc 1183} */
    public static final int ISDN = 20;
    /** {@rfc 1183} */
    public static final int RT = 21;
    /** {@rfc 1706} */
    public static final int NSAP = 22;
    /** {@rfc 1348} */
    public static final int NSAP_PTR = 23;
    /** {@rfc 4034}, {@rfc 3755}, {@rfc 2535} */
    public static final int SIG = 24;
    /** {@rfc 4034}, {@rfc 3755}, {@rfc 2535} */
    public static final int KEY = 25;
    /** {@rfc 2163} */
    public static final int PX = 26;
    /** {@rfc 1712} */
    public static final int GPOS = 27;
    /** {@rfc 3596} */
    public static final int AAAA = 28;
    /** {@rfc 1876} */
    public static final int LOC = 29;
    @Deprecated
    public static final int NXT = 30;
    public static final int EID = 31;
    public static final int NIMLOC = 32;
    /** {@rfc 2782} */
    public static final int SRV = 33;
    public static final int ATMA = 34;
    /** {@rfc 2915} {@rfc 2168} */
    public static final int NAPTR = 35;
    /** {@rfc 2230} */
    public static final int KX = 36;
    /** {@rfc 2538} */
    public static final int CERT = 37;
    /** {@rfc 3226} {@rfc 2874} */
    public static final int A6 = 38;
    /** {@rfc 2672} */
    public static final int DNAME = 39;
    public static final int SINK = 40;
    /** {@rfc 2671} */
    public static final int OPT = 41;
    /** {@rfc 3123} */
    public static final int APL = 42;
    /** {@rfc 3658} */
    public static final int DS = 43;
    /** {@rfc 4255} */
    public static final int SSHFP = 44;
    /** {@rfc 4025} */
    public static final int IPSECKEY = 45;
    /** {@rfc 4034} {@rfc 3755} */
    public static final int RRSIG = 46;
    /** {@rfc 4034} {@rfc 3755} */
    public static final int NSEC = 47;
    /** {@rfc 4034} {@rfc 3755} */
    public static final int DNSKEY = 48;
    /** {@rfc 4701} */
    public static final int DHCID = 49;
    /** {@rfc 5155} */
    public static final int NSEC3 = 50;
    /** {@rfc 5155} */
    public static final int NSEC3PARAM = 51;
    /** {@rfc 5205} */
    public static final int HIP = 55;
    public static final int NINFO = 56;
    public static final int RKEY = 57;
    /** {@rfc 4408} */
    public static final int SPF = 99;
    public static final int UINFO = 100;
    public static final int UID = 101;
    public static final int GID = 102;
    public static final int UNSPEC = 103;
    /** {@rfc 2930} */
    public static final int TKEY = 249;
    /** {@rfc 2845} */
    public static final int TSIG = 250;
    /** {@rfc 1995} */
    public static final int IXFR = 251;
    /** {@rfc 1035} */
    public static final int AXFR = 252;
    /** {@rfc 1035} */
    public static final int MAILB = 253;
    /**
     * @deprecated use {@link #MX}
     */
    @Deprecated
    public static final int MAILA = 254;
    /** {@rfc 1035} */
    public static final int ANY = 255;
    public static final int TA = 32768;
    public static final int DLV = 32769;

    public static String toString(final int i) {
        switch (i) {
            case A: return "A";
            case NS: return "NS";
            case MD: return "MD";
            case MF: return "MF";
            case CNAME: return "CNAME";
            case SOA: return "SOA";
            case MB: return "MB";
            case MG: return "MG";
            case MR: return "MR";
            case NULL: return "NULL";
            case WKS: return "WKS";
            case PTR: return "PTR";
            case HINFO: return "HINFO";
            case MINFO: return "MINFO";
            case MX: return "MX";
            case TXT: return "TXT";
            case RP: return "RP";
            case AFSDB: return "AFSDB";
            case X25: return "X25";
            case ISDN: return "ISDN";
            case RT: return "RT";
            case NSAP: return "NSAP";
            case NSAP_PTR: return "NSAP_PTR";
            case SIG: return "SIG";
            case KEY: return "KEY";
            case PX: return "PX";
            case GPOS: return "GPOS";
            case AAAA: return "AAAA";
            case LOC: return "LOC";
            case NXT: return "NXT";
            case EID: return "EID";
            case NIMLOC: return "NIMLOC";
            case SRV: return "SRV";
            case ATMA: return "ATMA";
            case NAPTR: return "NAPTR";
            case KX: return "KX";
            case CERT: return "CERT";
            case A6: return "A6";
            case DNAME: return "DNAME";
            case SINK: return "SINK";
            case OPT: return "OPT";
            case APL: return "APL";
            case DS: return "DS";
            case SSHFP: return "SSHFP";
            case IPSECKEY: return "IPSECKEY";
            case RRSIG: return "RRSIG";
            case NSEC: return "NSEC";
            case DNSKEY: return "DNSKEY";
            case DHCID: return "DHCID";
            case NSEC3: return "NSEC3";
            case NSEC3PARAM: return "NSEC3PARAM";
            case HIP: return "HIP";
            case NINFO: return "NINFO";
            case RKEY: return "RKEY";
            case SPF: return "SPF";
            case UINFO: return "UINFO";
            case UID: return "UID";
            case GID: return "GID";
            case UNSPEC: return "UNSPEC";
            case TKEY: return "TKEY";
            case TSIG: return "TSIG";
            case IXFR: return "IXFR";
            case AXFR: return "AXFR";
            case MAILB: return "MAILB";
            case MAILA: return "MAILA";
            case ANY: return "ANY";
            case TA: return "TA";
            case DLV: return "DLV";
            default: return "UNKNOWN";
        }
    }

    public static int fromString(String rrTypeName) {
        switch (rrTypeName) {
            case "A": return A;
            case "NS": return NS;
            case "MD": return MD;
            case "MF": return MF;
            case "CNAME": return CNAME;
            case "SOA": return SOA;
            case "MB": return MB;
            case "MG": return MG;
            case "MR": return MR;
            case "NULL": return NULL;
            case "WKS": return WKS;
            case "PTR": return PTR;
            case "HINFO": return HINFO;
            case "MINFO": return MINFO;
            case "MX": return MX;
            case "TXT": return TXT;
            case "RP": return RP;
            case "AFSDB": return AFSDB;
            case "X25": return X25;
            case "ISDN": return ISDN;
            case "RT": return RT;
            case "NSAP": return NSAP;
            case "NSAP_PTR": return NSAP_PTR;
            case "SIG": return SIG;
            case "KEY": return KEY;
            case "PX": return PX;
            case "GPOS": return GPOS;
            case "AAAA": return AAAA;
            case "LOC": return LOC;
            case "NXT": return NXT;
            case "EID": return EID;
            case "NIMLOC": return NIMLOC;
            case "SRV": return SRV;
            case "ATMA": return ATMA;
            case "NAPTR": return NAPTR;
            case "KX": return KX;
            case "CERT": return CERT;
            case "A6": return A6;
            case "DNAME": return DNAME;
            case "SINK": return SINK;
            case "OPT": return OPT;
            case "APL": return APL;
            case "DS": return DS;
            case "SSHFP": return SSHFP;
            case "IPSECKEY": return IPSECKEY;
            case "RRSIG": return RRSIG;
            case "NSEC": return NSEC;
            case "DNSKEY": return DNSKEY;
            case "DHCID": return DHCID;
            case "NSEC3": return NSEC3;
            case "NSEC3PARAM": return NSEC3PARAM;
            case "HIP": return HIP;
            case "NINFO": return NINFO;
            case "RKEY": return RKEY;
            case "SPF": return SPF;
            case "UINFO": return UINFO;
            case "UID": return UID;
            case "GID": return GID;
            case "UNSPEC": return UNSPEC;
            case "TKEY": return TKEY;
            case "TSIG": return TSIG;
            case "IXFR": return IXFR;
            case "AXFR": return AXFR;
            case "MAILB": return MAILB;
            case "MAILA": return MAILA;
            case "ANY": return ANY;
            case "TA": return TA;
            case "DLV": return DLV;
            default: return UNKNOWN;
        }
    }
}
