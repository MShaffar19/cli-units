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

package io.frinx.cli.unit.iosxr.bgp.handler.peergroup;

import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.openconfig.network.instance.NetworInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.peer.group.afi.safi.list.AfiSafi;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.peer.group.afi.safi.list.AfiSafiKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.peer.group.base.AfiSafis;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.peer.group.list.PeerGroup;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.peer.group.list.PeerGroupKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.top.Bgp;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.top.bgp.PeerGroups;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.types.rev170202.L3VPNIPV4UNICAST;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.NetworkInstances;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.Protocols;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.protocols.Protocol;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.protocols.ProtocolKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.policy.types.rev160512.BGP;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.routing.policy.rev170714.apply.policy.group.ApplyPolicy;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.routing.policy.rev170714.apply.policy.group.apply.policy.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.routing.policy.rev170714.apply.policy.group.apply.policy.ConfigBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;

public class PeerGroupAfiSafiApplyPolicyConfigReaderTest {

    @Mock
    private Cli cli;
    @Mock
    private ReadContext context;

    private PeerGroupAfiSafiApplyPolicyConfigReader target;
    private InstanceIdentifier<Config> iid = KeyedInstanceIdentifier.create(NetworkInstances.class)
            .child(NetworkInstance.class, new NetworkInstanceKey(NetworInstance.DEFAULT_NETWORK))
            .child(Protocols.class)
            .child(Protocol.class, new ProtocolKey(BGP.class, "default"))
            .child(Bgp.class)
            .child(PeerGroups.class)
            .child(PeerGroup.class, new PeerGroupKey(PeerGroupListReaderTest.PEER_GROUP_1))
            .child(AfiSafis.class)
            .child(AfiSafi.class, new AfiSafiKey(L3VPNIPV4UNICAST.class))
            .child(ApplyPolicy.class)
            .child(Config.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        target = Mockito.spy(new PeerGroupAfiSafiApplyPolicyConfigReader(cli));
        PeerGroupListReaderTest.mockAsNumber(context);
    }

    @Test
    public void testReadCurrentAttributes() throws ReadFailedException {
        String output = target.f("route-policy %s in\nroute-policy %s out\n",
                PeerGroupAfiSafiApplyPolicyConfigWriterTest.POLICY_IN,
                PeerGroupAfiSafiApplyPolicyConfigWriterTest.POLICY_OUT);
        String cmd = target.f(PeerGroupAfiSafiApplyPolicyConfigReader.SH_NEI,
                PeerGroupListReaderTest.AS_NUMBER,
                PeerGroupListReaderTest.PEER_GROUP_1,
                "vpnv4 unicast");
        Mockito.doReturn(output).when(target).blockingRead(cmd, cli, iid, context);
        ConfigBuilder builder = new ConfigBuilder();
        target.readCurrentAttributes(iid, builder, context);
        Assert.assertEquals(builder.getImportPolicy().get(0), PeerGroupAfiSafiApplyPolicyConfigWriterTest.POLICY_IN);
        Assert.assertEquals(builder.getExportPolicy().get(0), PeerGroupAfiSafiApplyPolicyConfigWriterTest.POLICY_OUT);
    }
}