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
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import org.xnio.AbstractConvertingIoFuture;
import org.xnio.IoFuture;
import org.xnio.dns.record.TxtRecord;
import org.xnio.dns.record.PtrRecord;
import org.xnio.dns.record.ARecord;
import org.xnio.dns.record.AaaaRecord;

/**
 * An abstract convenience base class for resolvers which implements the majority of the resolver methods.
 */
public abstract class AbstractResolver implements Resolver {

    /** {@inheritDoc} */
    public IoFuture<List<InetAddress>> resolveAllInet(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInetAddressList(resolve(name, RRClass.IN, RRType.ANY));
    }

    /** {@inheritDoc} */
    public IoFuture<InetAddress> resolveInet(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInetAddress(resolve(name, RRClass.IN, RRType.ANY));
    }

    /** {@inheritDoc} */
    public IoFuture<List<Inet4Address>> resolveAllInet4(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInet4AddressList(resolve(name, RRClass.IN, RRType.A));
    }

    /** {@inheritDoc} */
    public IoFuture<Inet4Address> resolveInet4(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInet4Address(resolve(name, RRClass.IN, RRType.A));
    }

    /** {@inheritDoc} */
    public IoFuture<List<Inet6Address>> resolveAllInet6(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInet6AddressList(resolve(name, RRClass.IN, RRType.AAAA));
    }

    /** {@inheritDoc} */
    public IoFuture<Inet6Address> resolveInet6(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureInet6Address(resolve(name, RRClass.IN, RRType.AAAA));
    }

    /** {@inheritDoc} */
    public IoFuture<Domain> resolveReverse(final InetAddress address) {
        if (address == null) {
            throw new NullPointerException("address is null");
        }
        return new FuturePtrDomain(resolve(Domain.reverseArpa(address), RRClass.IN, RRType.PTR));
    }

    /** {@inheritDoc} */
    public IoFuture<List<String>> resolveText(final Domain name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return new FutureText(resolve(name, RRClass.IN, RRType.TXT));
    }

    private static final class FutureText extends AbstractConvertingIoFuture<List<String>, Answer> {

        protected FutureText(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected List<String> convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            final List<String> strings = new ArrayList<String>(answers.size());
            for (Record answer : answers) {
                if (answer instanceof TxtRecord) {
                    strings.add(((TxtRecord)answer).getText());
                }
            }
            return strings;
        }
    }

    private static final class FuturePtrDomain extends AbstractConvertingIoFuture<Domain, Answer> {

        protected FuturePtrDomain(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected Domain convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            for (Record answer : answers) {
                if (answer instanceof PtrRecord) {
                    return ((PtrRecord)answer).getTarget();
                }
            }
            // not found...
            return null;
        }
    }

    static final class FutureInetAddressList extends AbstractConvertingIoFuture<List<InetAddress>, Answer> {

        protected FutureInetAddressList(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected List<InetAddress> convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            final List<InetAddress> list = new ArrayList<InetAddress>(answers.size());
            for (Record record : answers) {
                if (record instanceof ARecord) {
                    final ARecord aRecord = (ARecord) record;
                    list.add(aRecord.getAddress());
                } else if (record instanceof AaaaRecord) {
                    final AaaaRecord aaaaRecord = (AaaaRecord) record;
                    list.add(aaaaRecord.getAddress());
                }
            }
            return list;
        }
    }

    private static final class FutureInetAddress extends AbstractConvertingIoFuture<InetAddress, Answer> {

        protected FutureInetAddress(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected InetAddress convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            for (Record record : answers) {
                if (record instanceof ARecord) {
                    final ARecord aRecord = (ARecord) record;
                    return aRecord.getAddress();
                } else if (record instanceof AaaaRecord) {
                    final AaaaRecord aaaaRecord = (AaaaRecord) record;
                    return aaaaRecord.getAddress();
                }
            }
            return null;
        }
    }

    private static final class FutureInet4AddressList extends AbstractConvertingIoFuture<List<Inet4Address>, Answer> {

        protected FutureInet4AddressList(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected List<Inet4Address> convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            List<Inet4Address> list = new ArrayList<Inet4Address>(answers.size());
            for (Record record : answers) {
                if (record instanceof ARecord) {
                    final ARecord aRecord = (ARecord) record;
                    list.add(aRecord.getAddress());
                }
            }
            return list;
        }
    }

    private static final class FutureInet4Address extends AbstractConvertingIoFuture<Inet4Address, Answer> {

        protected FutureInet4Address(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected Inet4Address convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            for (Record record : answers) {
                if (record instanceof ARecord) {
                    final ARecord aRecord = (ARecord) record;
                    return aRecord.getAddress();
                }
            }
            return null;
        }
    }

    private static final class FutureInet6AddressList extends AbstractConvertingIoFuture<List<Inet6Address>, Answer> {

        protected FutureInet6AddressList(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected List<Inet6Address> convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            List<Inet6Address> list = new ArrayList<Inet6Address>(answers.size());
            for (Record record : answers) {
                if (record instanceof AaaaRecord) {
                    final AaaaRecord aaaaRecord = (AaaaRecord) record;
                    list.add(aaaaRecord.getAddress());
                }
            }
            return list;
        }
    }

    private static final class FutureInet6Address extends AbstractConvertingIoFuture<Inet6Address, Answer> {

        protected FutureInet6Address(final IoFuture<? extends Answer> delegate) {
            super(delegate);
        }

        protected Inet6Address convert(final Answer arg) throws IOException {
            final ResultCode resultCode = arg.getResultCode();
            if (resultCode != ResultCode.NOERROR) {
                throw new DNSException(resultCode);
            }
            final List<Record> answers = arg.getAnswerRecords();
            for (Record record : answers) {
                if (record instanceof AaaaRecord) {
                    final AaaaRecord aRecord = (AaaaRecord) record;
                    return aRecord.getAddress();
                }
            }
            return null;
        }
    }
}
