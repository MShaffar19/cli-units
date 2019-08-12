/*
 * Copyright © 2019 Frinx and others.
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

package io.frinx.cli.unit.brocade.ifc.handler.switchedvlan.def;

import io.frinx.cli.io.Cli;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.VlanRoutedConfig;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.vlan.routed.top.routed.vlan.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.vlan.routed.top.routed.vlan.ConfigBuilder;

public class RoutedVlanConfigWriterTest {

    @Test
    public void writeTest() {
        RoutedVlanConfigWriter writer = new RoutedVlanConfigWriter(Mockito.mock(Cli.class));
        Config dataAfter = new ConfigBuilder().setVlan(new VlanRoutedConfig.Vlan(4)).build();
        Config dataBefore = new ConfigBuilder().setVlan(new VlanRoutedConfig.Vlan(5)).build();

        Assert.assertEquals("configure terminal\n"
                + "vlan 4\n"
                + "router-interface ve 4\n"
                + "end", writer.getWriteCommand(null, dataAfter, "ve 4"));
        Assert.assertEquals("configure terminal\n"
                + "vlan 5\n"
                + "no router-interface ve 4\n"
                + "vlan 4\n"
                + "router-interface ve 4\n"
                + "end", writer.getWriteCommand(dataBefore, dataAfter, "ve 4"));
        Assert.assertEquals("configure terminal\n"
                + "vlan 5\n"
                + "no router-interface ve 4\n"
                + "end", writer.getWriteCommand(dataBefore, null, "ve 4"));
    }
}