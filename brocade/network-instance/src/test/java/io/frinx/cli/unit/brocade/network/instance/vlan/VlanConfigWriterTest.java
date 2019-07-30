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

package io.frinx.cli.unit.brocade.network.instance.vlan;

import io.frinx.cli.unit.utils.CliFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.vlan.top.vlans.vlan.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.types.rev170714.VlanId;

public class VlanConfigWriterTest implements CliFormatter {

    @Test
    public void testWrite() {
        String config = fT(VlanConfigWriter.WRITE_TEMPLATE, "data", new ConfigBuilder()
                .setVlanId(new VlanId(14))
                .setName("abcd")
                .build());

        Assert.assertEquals("configure terminal\n"
                + "vlan 14 name abcd\n"
                + "end\n", config);
    }
}