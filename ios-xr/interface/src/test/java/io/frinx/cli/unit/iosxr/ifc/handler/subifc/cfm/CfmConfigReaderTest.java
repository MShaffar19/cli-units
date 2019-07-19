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

package io.frinx.cli.unit.iosxr.ifc.handler.subifc.cfm;

import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.openconfig.openconfig.oam.IIDs;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.InterfaceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.SubinterfaceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.ethernet.cfm._interface.cfm.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.ethernet.cfm._interface.cfm.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.utils.IidUtils;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class CfmConfigReaderTest {
    private static final String SH_RUN = "show running-config interface Bundle-Ether1000.100";
    private static final String SH_RUN_OUTPUT = "interface Bundle-Ether100.100\n"
        + " encapsulation dot1q 100\n"
        + " service-policy output 100M-Policing-Kakuho\n"
        + " ipv4 address 218.45.246.205 255.255.255.252\n"
        + " ipv6 address 2403:7a00:6:1a::1/64\n"
        + " ethernet cfm\n"
        + "  mep domain DML1 service MA-001 mep-id 1\n"
        + "   cos 1\n"
        + "   loss-measurement counters aggregate\n"
        + "   sla operation profile PPP target mep-id 2\n"
        + " description D300220206\n"
        + " ipv4 access-group D300000000_out egress\n"
        + " ipv6 access-group D300000000_out egress\n";

    @Mock
    private Cli cli;
    @Mock
    private ReadContext ctx;
    private CfmConfigReader target;

    private static final String INTERFACE_NAME = "Bundle-Ether1000";
    private static final Long SUBIFC_INDEX = Long.valueOf(100L);

    private static final InterfaceKey INTERFACE_KEY = new InterfaceKey(INTERFACE_NAME);
    private static final SubinterfaceKey SUBIFC_KEY = new SubinterfaceKey(SUBIFC_INDEX);
    private static final InstanceIdentifier<Config> IID = IidUtils.createIid(
        IIDs.IN_IN_SU_SU_AUG_IFSUBIFCFMAUG_CF_CONFIG,
        INTERFACE_KEY, SUBIFC_KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        target = Mockito.spy(new CfmConfigReader(cli));
    }

    @Test
    public void testReadCurrentAttributes() throws ReadFailedException {
        Mockito.doReturn(SH_RUN_OUTPUT).when(target)
            .blockingRead(SH_RUN, cli, IID, ctx);

        final ConfigBuilder builder = new ConfigBuilder();

        target.readCurrentAttributes(IID, builder, ctx);

        Assert.assertThat(builder.isEnabled(), CoreMatchers.is(Boolean.TRUE));
    }
}
