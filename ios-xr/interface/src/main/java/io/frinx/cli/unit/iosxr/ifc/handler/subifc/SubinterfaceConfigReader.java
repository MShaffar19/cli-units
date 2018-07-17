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

package io.frinx.cli.unit.iosxr.ifc.handler.subifc;

import static io.frinx.cli.unit.iosxr.ifc.handler.subifc.SubinterfaceReader.ZERO_SUBINTERFACE_ID;
import static io.frinx.cli.unit.iosxr.ifc.handler.subifc.SubinterfaceReader.getSubinterfaceName;
import static io.frinx.cli.unit.utils.ParsingUtils.parseField;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.iosxr.ifc.handler.InterfaceConfigReader;
import io.frinx.cli.unit.utils.CliConfigReader;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.Subinterface;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.SubinterfaceBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.SubinterfaceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.subinterface.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.subinterfaces.top.subinterfaces.subinterface.ConfigBuilder;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class SubinterfaceConfigReader implements CliConfigReader<Config, ConfigBuilder> {

    private Cli cli;

    public SubinterfaceConfigReader(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void readCurrentAttributes(@Nonnull InstanceIdentifier<Config> id,
                                      @Nonnull ConfigBuilder builder,
                                      @Nonnull ReadContext ctx) throws ReadFailedException {
        SubinterfaceKey subKey = id.firstKeyOf(Subinterface.class);

        // Parse subifc's configuration from device only for non 0 subifc
        if (subKey.getIndex() == ZERO_SUBINTERFACE_ID) {
            builder.setIndex(subKey.getIndex());
            return;
        }

        String subIfcName = getSubinterfaceName(id);
        String cmd = String.format(InterfaceConfigReader.SH_SINGLE_INTERFACE_CFG, subIfcName);
        parseInterface(blockingRead(cmd, cli, id, ctx), builder, subKey.getIndex(), subIfcName);
    }

    @VisibleForTesting
    static void parseInterface(final String output, final ConfigBuilder builder, Long subKey, String name) {
        // Set enabled unless proven otherwise
        builder.setEnabled(true);
        builder.setIndex(subKey);
        builder.setName(name);

        // Actually check if disabled
        parseField(output, 0,
                InterfaceConfigReader.SHUTDOWN_LINE::matcher,
            matcher -> false,
                builder::setEnabled);

        parseField(output,
                InterfaceConfigReader.DESCR_LINE::matcher,
            matcher -> matcher.group("desc"),
                builder::setDescription);
    }

    @Override
    public void merge(@Nonnull Builder<? extends DataObject> parentBuilder, @Nonnull Config readValue) {
        ((SubinterfaceBuilder) parentBuilder).setConfig(readValue);
    }
}
