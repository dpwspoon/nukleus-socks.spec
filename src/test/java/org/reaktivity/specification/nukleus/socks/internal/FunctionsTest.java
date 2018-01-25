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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.reaktivity.specification.nukleus.socks.internal.Functions.socksAddress;
import static org.reaktivity.specification.socks.internal.types.SocksAddressTypeFW.KIND_DOMAIN_NAME;
import static org.reaktivity.specification.socks.internal.types.SocksAddressTypeFW.KIND_IPV4_ADDRESS;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.reaktivity.specification.socks.internal.types.OctetsFW;
import org.reaktivity.specification.socks.internal.types.SocksAddressFW;
import org.reaktivity.specification.socks.internal.types.SocksAddressTypeFW;

public class FunctionsTest
{

    @Test
    public void shouldGenerateSocksIpv4LocalhostAddress()
    {
        byte[] build = socksAddress()
                       .localhostIpv4()
                       .port(8080)
                       .build();

        DirectBuffer buffer = new UnsafeBuffer(build);
        SocksAddressFW socks = new SocksAddressFW().wrap(buffer, 0, buffer.capacity());

        assertEquals(KIND_IPV4_ADDRESS, socks.addressType().kind());

        OctetsFW ipv4Address = socks.addressType().ipv4Address();
        byte[] actual = new byte[ipv4Address.sizeof()];
        ipv4Address.buffer().getBytes(ipv4Address.offset(), actual);
        Assert.assertArrayEquals(new byte[] {0x7f, 0x00, 0x00, 0x01}, actual);

        assertEquals(8080, socks.port());
    }

    @Test
    public void shouldGenerateSocksIpv6LocalhostAddress()
    {
        byte[] build = socksAddress()
                       .localhostIpv6()
                       .port(8080)
                       .build();

        DirectBuffer buffer = new UnsafeBuffer(build);
        SocksAddressFW socks = new SocksAddressFW().wrap(buffer, 0, buffer.capacity());

        assertEquals(SocksAddressTypeFW.KIND_IPV6_ADDRESS, socks.addressType().kind());

        OctetsFW ipv6Address = socks.addressType().ipv6Address();
        byte[] actual = new byte[ipv6Address.sizeof()];
        ipv6Address.buffer().getBytes(ipv6Address.offset(), actual);
        assertArrayEquals(new byte[] {0x00, 0x00, 0x00, 0x00,
                                      0x00, 0x00, 0x00, 0x00,
                                      0x00, 0x00, 0x00, 0x00,
                                      0x00, 0x00, 0x00, 0x01}, actual);

        assertEquals(8080, socks.port());
    }

    @Test
    public void shouldGenerateSocksLocalhostDomainName()
    {
        byte[] build = socksAddress()
                .localhostDomainName()
                .port(8080)
                .build();

        DirectBuffer buffer = new UnsafeBuffer(build);
        SocksAddressFW socks = new SocksAddressFW().wrap(buffer, 0, buffer.capacity());

        assertEquals(KIND_DOMAIN_NAME, socks.addressType().kind());

        assertEquals("localhost", socks.addressType().domainName().asString());

        assertEquals(8080, socks.port());
    }
}
