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

package io.frinx.cli.unit.ios.ifc.handler;

import com.google.common.collect.Lists;
import io.frinx.cli.io.Cli;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.InterfaceKey;

public class InterfaceReaderTest {

    private static final String SH_INTERFACE = "interface Loopback0\n"
            + "interface FastEthernet0/0\n"
            + "interface GigabitEthernet1/0\n"
            + "interface GigabitEthernet2/0\n"
            + "interface GigabitEthernet3/0\n"
            + "interface FastEthernet4/0\n"
            + "interface FastEthernet4/0.56\n"
            + "interface FastEthernet4/0.57\n";

    private static final List<InterfaceKey> IDS_EXPECTED = Lists.newArrayList("Loopback0", "FastEthernet0/0",
            "GigabitEthernet1/0", "GigabitEthernet2/0", "GigabitEthernet3/0", "FastEthernet4/0")
            .stream()
            .map(InterfaceKey::new)
            .collect(Collectors.toList());

    private static final List<InterfaceKey> IDS_ALL_EXPECTED = Lists.newArrayList("Loopback0", "FastEthernet0/0",
            "GigabitEthernet1/0", "GigabitEthernet2/0", "GigabitEthernet3/0", "FastEthernet4/0",
            "FastEthernet4/0.56", "FastEthernet4/0.57")
            .stream()
            .map(InterfaceKey::new)
            .collect(Collectors.toList());

    @Test
    public void testParseInterfaceIds() {
        Assert.assertEquals(IDS_EXPECTED,
                new InterfaceReader(Mockito.mock(Cli.class)).parseInterfaceIds(SH_INTERFACE));
    }

    @Test
    public void testParseAllInterfaceIds() {
        Assert.assertEquals(IDS_ALL_EXPECTED,
                new InterfaceReader(Mockito.mock(Cli.class)).parseAllInterfaceIds(SH_INTERFACE));
    }
}
