/**
 * Copyright 2016-2017 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.nukleus.socks.internal;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.reaktivity.specification.socks.internal.types.SocksAddressFW;
import org.reaktivity.specification.socks.internal.types.SocksAddressFW.Builder;

public final class Functions
{

    public static class SocksAddressBuilder
    {
        private static final int MAX_ADDRESS_SIZE = 1024;
        private Builder socksAddressRW;

        public SocksAddressBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_ADDRESS_SIZE]);
            this.socksAddressRW = new SocksAddressFW.Builder();
            socksAddressRW.wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public SocksAddressBuilder port(
            int port)
        {
            socksAddressRW.port(port);
            return this;
        }

        public SocksAddressBuilder domainName(
            String domainName)
        {
            socksAddressRW.addressType(b -> b.domainName(domainName));
            return this;
        }

        public SocksAddressBuilder address(
            byte[] addr)
        {
            if (addr.length == 4)
            {
                socksAddressRW.addressType(b -> b.ipv4Address(o -> o.set(addr)));
            }
            else
            {
                socksAddressRW.addressType(b -> b.ipv6Address(o -> o.set(addr)));
            }
            return this;
        }

        public SocksAddressBuilder localhostDomainName()
        {
            return domainName("localhost");
        }

        public SocksAddressBuilder localhostIpv4()
        {
            return address(new byte[] {0x7f, 0x00, 0x00, 0x01});
        }

        public SocksAddressBuilder localhostIpv6()
        {
            return address(new byte[] {0x00, 0x00, 0x00, 0x00,
                                       0x00, 0x00, 0x00, 0x00,
                                       0x00, 0x00, 0x00, 0x00,
                                       0x00, 0x00, 0x00, 0x01});
        }

        public byte[] build()
        {
            final SocksAddressFW socksAddress = socksAddressRW.build();
            final byte[] result = new byte[socksAddress.sizeof()];
            socksAddress.buffer().getBytes(0, result);
            return result;
        }
    }

    @Function
    public static SocksAddressBuilder socksAddress()
    {
        return new SocksAddressBuilder();
    }

    public static class Mapper extends FunctionMapperSpi.Reflective
    {

        public Mapper()
        {
            super(Functions.class);
        }

        @Override
        public String getPrefixName()
        {
            return "socks";
        }
    }

    private Functions()
    {
        // utility
    }
}
