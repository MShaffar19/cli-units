/*
 * Copyright © 2020 Frinx and others.
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

package io.frinx.cli.unit.cubro.ifc.handler;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliConfigListReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.Interface;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.InterfaceBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.interfaces.rev161222.interfaces.top.interfaces.InterfaceKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class InterfaceReader implements CliConfigListReader<Interface, InterfaceKey, InterfaceBuilder> {


    private static final String SH_INTERFACE_CFG = "show running-config";

    private static final Pattern INTERFACE_ID = Pattern.compile("(interface )(?<id>\\d+)");
    private Cli cli;

    public InterfaceReader(Cli cli) {
        this.cli = cli;
    }

    @Nonnull
    @Override
    public List<InterfaceKey> getAllIds(@Nonnull InstanceIdentifier<Interface> instanceIdentifier,
                                        @Nonnull ReadContext readContext) throws ReadFailedException {
        return parseInterfaceIds(blockingRead(SH_INTERFACE_CFG, cli, instanceIdentifier, readContext));
    }

    @VisibleForTesting
    public List<InterfaceKey> parseInterfaceIds(String output) {
        return ParsingUtils.parseFields(output, 0,
            INTERFACE_ID::matcher,
            matcher -> matcher.group("id").trim(),
            InterfaceKey::new);
    }

    @Override
    public void readCurrentAttributes(@Nonnull InstanceIdentifier<Interface> instanceIdentifier,
                                      @Nonnull InterfaceBuilder builder, @Nonnull ReadContext readContext) {
        builder.setName(instanceIdentifier.firstKeyOf(Interface.class).getName());
    }
}