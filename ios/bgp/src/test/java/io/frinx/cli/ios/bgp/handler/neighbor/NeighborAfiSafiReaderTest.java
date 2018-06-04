/*
 * Copyright © 2018 Frinx and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.frinx.cli.ios.bgp.handler.neighbor;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import io.frinx.openconfig.network.instance.NetworInstance;
import java.util.List;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.neighbor.afi.safi.list.AfiSafiKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.types.rev170202.IPV4UNICAST;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceKey;

public class NeighborAfiSafiReaderTest {

    private static final String OUTPUT = " address-family ipv4\n" +
            " address-family ipv6\n" +
            " address-family vpnv4\n" +
            " address-family vpnv6\n" +
            "router bgp 65002\n" +
            " neighbor 1.2.3.4 remote-as 65000\n" +
            " address-family ipv4\n" +
            "  neighbor 1.2.3.4 activate\n" +
            " address-family ipv6\n" +
            " address-family vpnv6\n" +
            " address-family ipv4 vrf abcd\n" +
            "  neighbor 1.2.3.4 remote-as 65000\n" +
            "  neighbor 1.2.3.4 activate\n";

    private static final String OUTPUT2 = " address-family ipv4\n" +
            " address-family ipv6\n" +
            " address-family vpnv4\n" +
            " address-family vpnv6\n" +
            "router bgp 65002\r\n" +
            " neighbor 1.2.3.4 remote-as 65000\r\n" +
            " neighbor 1.2.3.4 update-source GigabitEthernet1\r\n" +
            " address-family ipv4\r\n" +
            "  neighbor 1.2.3.4 activate\r\n" +
            "  neighbor 1.2.3.4 route-map policy1 in\r\n" +
            " address-family ipv6";

    private static final String OUTPUT3 = " address-family ipv4\n" +
            " address-family ipv6\n" +
            " address-family vpnv4\n" +
            " address-family vpnv6\n" +
            "router bgp 65002\r\n" +
            " neighbor 1.2.3.4 remote-as 65000\r\n" +
            " neighbor 1.2.3.4 update-source GigabitEthernet1\r\n" +
            " address-family ipv4 vrf abcd\r\n" +
            "  neighbor 1.2.3.4 activate\r\n" +
            "  neighbor 1.2.3.4 route-map policy1 in\r\n" +
            " address-family NONEXISTING vrf abcd";

    @Test
    public void testAllIds() throws Exception {
        List<AfiSafiKey> defaults = NeighborAfiSafiReader.getAfiKeys(OUTPUT, NetworInstance.DEFAULT_NETWORK, line -> line.contains("activate"));
        assertEquals(defaults.size(), 1);
        assertThat(defaults, hasItem(new AfiSafiKey(IPV4UNICAST.class)));

        defaults = NeighborAfiSafiReader.getAfiKeys(OUTPUT2, NetworInstance.DEFAULT_NETWORK, line -> line.contains("activate"));
        assertEquals(defaults.size(), 1);
        assertThat(defaults, hasItem(new AfiSafiKey(IPV4UNICAST.class)));

        List<AfiSafiKey> abcds = NeighborAfiSafiReader.getAfiKeys(OUTPUT, new NetworkInstanceKey("abcd"), line -> line.contains("activate"));
        assertEquals(abcds.size(), 1);
        assertThat(abcds, hasItem(new AfiSafiKey(IPV4UNICAST.class)));

        abcds = NeighborAfiSafiReader.getAfiKeys(OUTPUT3, new NetworkInstanceKey("abcd"), line -> line.contains("activate"));
        assertEquals(abcds.size(), 1);
        assertThat(abcds, hasItem(new AfiSafiKey(IPV4UNICAST.class)));

        List<AfiSafiKey> abcds2 = NeighborAfiSafiReader.getAfiKeys(OUTPUT, new NetworkInstanceKey("abcd2"), line -> line.contains("activate"));
        assertEquals(abcds2.size(), 0);
    }
}