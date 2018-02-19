/*
 * Copyright © 2018 Frinx and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package io.frinx.cli.iosxr.unit.acl.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.google.common.base.Optional;
import io.fd.honeycomb.translate.ModificationCache;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.openconfig.openconfig.acl.IIDs;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526.ACLIPV6;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526.ACLTYPE;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526._interface.ingress.acl.top.IngressAclSets;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526._interface.ingress.acl.top.ingress.acl.sets.IngressAclSet;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526._interface.ingress.acl.top.ingress.acl.sets.IngressAclSetBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526._interface.ingress.acl.top.ingress.acl.sets.IngressAclSetKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526._interface.ingress.acl.top.ingress.acl.sets.ingress.acl.set.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526.acl.interfaces.top.interfaces.Interface;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.acl.rev170526.acl.interfaces.top.interfaces.InterfaceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.InterfaceId;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.InterfaceBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.iana._if.type.rev140508.EthernetCsmacd;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.iana._if.type.rev140508.Ieee8023adLag;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.iana._if.type.rev140508.L2vlan;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class IngressAclSetReaderTest {

    private Cli cliMock = Mockito.mock(Cli.class);
    private ReadContext context = Mockito.mock(ReadContext.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        Mockito.when(cliMock.executeAndRead(Mockito.any()))
            .then(invocation -> CompletableFuture.completedFuture(TestData.READ_OUTPUT));

        final ModificationCache modificationCacheMock = Mockito.mock(ModificationCache.class);
        Mockito.when(context.getModificationCache())
            .then(invocation -> modificationCacheMock);
        Mockito.when(context.getModificationCache().containsKey(Mockito.any()))
            .then(invocation -> true);
    }

    @Test
    public void readAclSet_LAGInterface() throws ReadFailedException {
        Mockito.when(context.read(Mockito.any()))
            .then(invocation -> Optional.of(TestData.INTERFACE_CORRECT_TYPE));

        final IngressAclSetBuilder aclSetBuilder = new IngressAclSetBuilder();
        IngressAclSetReader reader = new IngressAclSetReader(cliMock);
        reader.readCurrentAttributes(TestData.ACL_SET_IID, aclSetBuilder, context);

        assertEquals(TestData.ACL_SET_NAME, aclSetBuilder.getSetName());
        assertEquals(TestData.ACL_TYPE, aclSetBuilder.getType());
    }

    @Test
    public void readAclSet_noLAGInterface() throws ReadFailedException {
        Mockito.when(context.read(Mockito.any()))
            .then(invocation -> Optional.of(TestData.INTERFACE_WRONG_TYPE));

        final IngressAclSetBuilder aclSetBuilder = new IngressAclSetBuilder();
        IngressAclSetReader reader = new IngressAclSetReader(cliMock);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(CoreMatchers.allOf(
            CoreMatchers.containsString("Parent interface should be"),
            CoreMatchers.containsString(EthernetCsmacd.class.getSimpleName()),
            CoreMatchers.containsString(Ieee8023adLag.class.getSimpleName())
        ));

        reader.readCurrentAttributes(TestData.ACL_SET_IID, aclSetBuilder, context);
    }

    @Test
    public void readAllAclIds() throws ReadFailedException {
        IngressAclSetReader reader = new IngressAclSetReader(cliMock);
        final List<IngressAclSetKey> aclIds = reader.getAllIds(TestData.ACL_SET_IID, context);

        assertThat(aclIds, CoreMatchers.allOf(
            CoreMatchers.hasItem(new IngressAclSetKey(TestData.ACL_SET_NAME, TestData.ACL_TYPE)),
            CoreMatchers.hasItem(new IngressAclSetKey(TestData.ACL_SET_NAME_OTHER, TestData.ACL_TYPE))
            )
        );
    }

    private static class TestData {

        private static final String INTERFACE_NAME = "GigabitInterface 0/0/0/0";
        private static final String ACL_SET_NAME = "test_acl_group";
        private static final String ACL_SET_NAME_OTHER = "bubu_group";
        private static final Class<? extends ACLTYPE> ACL_TYPE = ACLIPV6.class;
        private static final String READ_OUTPUT = String.format("interface GigabitEthernet0/0/0/0\n"
                + " ipv6 access-group %s ingress\n"
                + " ipv6 access-group %s ingress\n"
                + "!",
            ACL_SET_NAME, ACL_SET_NAME_OTHER);

        static final InstanceIdentifier<IngressAclSet> ACL_SET_IID = IIDs.AC_INTERFACES
            .child(Interface.class, new InterfaceKey(new InterfaceId(INTERFACE_NAME)))
            .child(IngressAclSets.class)
            .child(IngressAclSet.class, new IngressAclSetKey(ACL_SET_NAME, ACL_TYPE));
        static final org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.Interface
            INTERFACE_CORRECT_TYPE =
            new InterfaceBuilder()
                .setName(INTERFACE_NAME)
                .setConfig(
                    new org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces._interface.ConfigBuilder()
                        .setType(Ieee8023adLag.class)
                        .build())
                .build();
        static final org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.Interface
            INTERFACE_WRONG_TYPE =
            new InterfaceBuilder()
                .setName(INTERFACE_NAME)
                .setConfig(
                    new org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces._interface.ConfigBuilder()
                        .setType(L2vlan.class)
                        .build())
                .build();

        static final IngressAclSet ACL_SET = new IngressAclSetBuilder()
            .setSetName(ACL_SET_NAME)
            .setType(ACL_TYPE)
            .setConfig(new ConfigBuilder()
                .setSetName(ACL_SET_NAME)
                .setType(ACL_TYPE)
                .build())
            .build();
    }
}
