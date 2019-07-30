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

package io.frinx.cli.unit.iosxr.oam.handler.domain;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.fd.honeycomb.translate.write.WriteContext;
import io.fd.honeycomb.translate.write.WriteFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.io.Command;
import io.frinx.openconfig.openconfig.oam.IIDs;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm.domains.DomainKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm.domains.domain.mas.MaKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm.domains.domain.mas.ma.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm.domains.domain.mas.ma.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.types.rev190619.CcmInterval;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.types.rev190619.DomainLevel;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.utils.IidUtils;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class CfmMaConfigWriterTest {
    @Mock
    private Cli cli;
    @Mock
    private WriteContext writeContext;
    private CfmMaConfigWriter target;
    private ArgumentCaptor<Command> response = ArgumentCaptor.forClass(Command.class);

    private static final String WRITE_INPUT = "ethernet cfm\n"
        + "domain DML1 level 3\n"
        + "service MA-002 down-meps\n"
        + "continuity-check interval 1m loss-threshold 3\n"
        + "mep crosscheck mep-id 2\n"
        + "mep crosscheck mep-id 4\n"
        + "efd\n"
        + "root\n";

    private static final String UPDATE_INPUT = "ethernet cfm\n"
        + "domain DML1 level 3\n"
        + "service MA-002 down-meps\n"
        + "continuity-check interval 10m loss-threshold 4\n"
        + "no mep crosscheck mep-id 2\n"
        + "mep crosscheck mep-id 5\n"
        + "no efd\n"
        + "root\n";

    private static final String DELETE_INPUT = "ethernet cfm\n"
        + "domain DML1 level 3\n"
        + "no service MA-002\n"
        + "root\n";

    private static final String DOMAIN_NAME = "DML1";
    private static final String MA_NAME = "MA-002";
    private static final DomainKey DOMAIN_KEY = new DomainKey(DOMAIN_NAME);
    private static final MaKey MA_KEY = new MaKey(MA_NAME);

    private static final InstanceIdentifier<Config> IID =
        IidUtils.createIid(IIDs.OA_CF_DO_DO_MA_MA_CONFIG, DOMAIN_KEY, MA_KEY);

    private static final Config DATA = new ConfigBuilder()
        .setMaName(MA_NAME)
        .setContinuityCheckInterval(CcmInterval._1m)
        .setContinuityCheckLossThreshold(3L)
        .setEfd(Boolean.TRUE)
        .setMepCrosscheck(Lists.newArrayList(Integer.valueOf(2), Integer.valueOf(4)))
        .build();


    private static final Config DATA_UPDATE = new ConfigBuilder()
        .setMaName(MA_NAME)
        .setContinuityCheckInterval(CcmInterval._10m)
        .setContinuityCheckLossThreshold(4L)
        .setEfd(Boolean.FALSE)
        .setMepCrosscheck(Lists.newArrayList(Integer.valueOf(4), Integer.valueOf(5)))
        .build();

    private static final org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm
        .domains.domain.Config DOMAIN_CONFIG =
        new org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.oam.rev190619.oam.top.oam.cfm.domains.domain
        .ConfigBuilder()
            .setDomainName(DOMAIN_NAME)
            .setLevel(new DomainLevel((short) 3))
            .build();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cli.executeAndRead(Mockito.any())).then(invocation -> CompletableFuture.completedFuture(""));
        target = new CfmMaConfigWriter(cli);
    }

    @Test
    public void testWriteCurrentAttributes() throws WriteFailedException {
        Mockito.doReturn(Optional.of(DOMAIN_CONFIG)).when(writeContext).readAfter(Mockito.any());

        target.writeCurrentAttributes(IID, DATA, writeContext);
        Mockito.verify(cli).executeAndRead(response.capture());
        Assert.assertEquals(WRITE_INPUT, response.getValue().getContent());
    }

    @Test
    public void testUpdateCurrentAttributes() throws WriteFailedException {
        Mockito.doReturn(Optional.of(DOMAIN_CONFIG)).when(writeContext).readAfter(Mockito.any());

        target.updateCurrentAttributes(IID, DATA, DATA_UPDATE, writeContext);
        Mockito.verify(cli).executeAndRead(response.capture());
        Assert.assertEquals(UPDATE_INPUT, response.getValue().getContent());
    }

    @Test
    public void testDeleteCurrentAttributes() throws WriteFailedException {
        Mockito.doReturn(Optional.of(DOMAIN_CONFIG)).when(writeContext).readBefore(Mockito.any());

        target.deleteCurrentAttributes(IID, DATA, writeContext);
        Mockito.verify(cli).executeAndRead(response.capture());
        Assert.assertEquals(DELETE_INPUT, response.getValue().getContent());
    }
}
