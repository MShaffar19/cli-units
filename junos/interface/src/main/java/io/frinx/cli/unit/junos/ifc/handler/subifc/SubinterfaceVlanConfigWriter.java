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

package io.frinx.cli.unit.junos.ifc.handler.subifc;

import io.fd.honeycomb.translate.write.WriteContext;
import io.fd.honeycomb.translate.write.WriteFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.junos.ifc.Util;
import io.frinx.cli.unit.utils.CliWriter;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.vlan.logical.top.vlan.Config;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class SubinterfaceVlanConfigWriter implements CliWriter<Config> {

    private static final String WRITE_TEMPLATE = "set interfaces %s vlan-id %s";
    private static final String DELETE_TEMPLATE = "delete interfaces %s vlan-id";

    private final Cli cli;

    public SubinterfaceVlanConfigWriter(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void writeCurrentAttributes(@Nonnull InstanceIdentifier<Config> id, @Nonnull Config dataAfter,
        @Nonnull WriteContext writeContext) throws WriteFailedException {

        String sbif = Util.getSubinterfaceName(id);

        blockingWriteAndRead(cli, id, dataAfter,
            f(WRITE_TEMPLATE, sbif, dataAfter.getVlanId().getVlanId().getValue()));
    }

    @Override
    public void updateCurrentAttributes(@Nonnull InstanceIdentifier<Config> id, @Nonnull Config dataBefore,
        @Nonnull Config dataAfter, @Nonnull WriteContext writeContext) throws WriteFailedException {
        if (dataAfter.getVlanId() == null) {
            deleteCurrentAttributes(id, dataBefore, writeContext);
        } else {
            writeCurrentAttributes(id, dataAfter, writeContext);
        }
    }

    @Override
    public void deleteCurrentAttributes(@Nonnull InstanceIdentifier<Config> id, @Nonnull Config dataBefore,
        @Nonnull WriteContext writeContext) throws WriteFailedException {

        String sbif = Util.getSubinterfaceName(id);

        blockingDeleteAndRead(cli, id, f(DELETE_TEMPLATE, sbif));
    }
}
