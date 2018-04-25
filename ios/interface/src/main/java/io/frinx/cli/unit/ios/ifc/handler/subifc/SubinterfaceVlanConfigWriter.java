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

package io.frinx.cli.unit.ios.ifc.handler.subifc;

import static io.frinx.cli.unit.ios.ifc.handler.subifc.SubinterfaceReader.ZERO_SUBINTERFACE_ID;
import static io.frinx.cli.unit.ios.ifc.handler.subifc.SubinterfaceReader.getSubinterfaceName;

import io.fd.honeycomb.translate.write.WriteContext;
import io.fd.honeycomb.translate.write.WriteFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliWriter;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.Subinterface;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.vlan.rev170714.vlan.logical.top.vlan.Config;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class SubinterfaceVlanConfigWriter implements CliWriter<Config> {

    private final Cli cli;

    public SubinterfaceVlanConfigWriter(Cli cli) {
        this.cli = cli;
    }

    private static final String WRITE_TEMPLATE = "configure terminal\n" +
            "interface %s\n" +
            "encapsulation dot1Q %s\n" +
            "end";

    @Override
    public void writeCurrentAttributes(@Nonnull InstanceIdentifier<Config> id,
                                       @Nonnull Config dataAfter,
                                       @Nonnull WriteContext writeContext) throws WriteFailedException {
        Long subId = id.firstKeyOf(Subinterface.class).getIndex();

        if (subId == ZERO_SUBINTERFACE_ID) {
            throw new WriteFailedException.CreateFailedException(id, dataAfter,
                    new IllegalArgumentException("Unable to manage Vlan for subinterface: " + ZERO_SUBINTERFACE_ID));
        } else {
            blockingWriteAndRead(cli, id, dataAfter,
                    f(WRITE_TEMPLATE,
                            getSubinterfaceName(id),
                            dataAfter.getVlanId().getVlanId().getValue()));
        }
    }

    @Override
    public void updateCurrentAttributes(@Nonnull InstanceIdentifier<Config> id,
                                        @Nonnull Config dataBefore,
                                        @Nonnull Config dataAfter,
                                        @Nonnull WriteContext writeContext) throws WriteFailedException {
        deleteCurrentAttributes(id, dataBefore, writeContext);
        writeCurrentAttributes(id, dataAfter, writeContext);
    }

    private static final String DELETE_TEMPLATE = "configure terminal\n" +
            "interface %s\n" +
            "no encapsulation dot1Q %s\n" +
            "end";


    @Override
    public void deleteCurrentAttributes(@Nonnull InstanceIdentifier<Config> id,
                                        @Nonnull Config dataBefore,
                                        @Nonnull WriteContext writeContext) throws WriteFailedException {
        Long subId = id.firstKeyOf(Subinterface.class).getIndex();

        if (subId == ZERO_SUBINTERFACE_ID) {
            throw new WriteFailedException.CreateFailedException(id, dataBefore,
                    new IllegalArgumentException("Unable to manage Vlan for subinterface: " + ZERO_SUBINTERFACE_ID));
        } else {
            blockingDeleteAndRead(cli, id,
                    f(DELETE_TEMPLATE,
                            getSubinterfaceName(id),
                            dataBefore.getVlanId().getVlanId().getValue()));
        }
    }
}
