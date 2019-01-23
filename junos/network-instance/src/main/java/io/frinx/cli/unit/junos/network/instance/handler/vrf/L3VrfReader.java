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

package io.frinx.cli.unit.junos.network.instance.handler.vrf;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliConfigListReader;
import io.frinx.cli.unit.utils.CliReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import io.frinx.translate.unit.commons.handler.spi.CompositeListReader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceKey;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class L3VrfReader
        implements CliConfigListReader<NetworkInstance, NetworkInstanceKey, NetworkInstanceBuilder>,
        CompositeListReader.Child<NetworkInstance, NetworkInstanceKey, NetworkInstanceBuilder> {

    private final Cli cli;

    @VisibleForTesting
    static final String DISPLAY_CONF_VRF =
        "show configuration routing-instances | display set | match \"instance-type virtual-router$\"";

    private static final Pattern VRF_CONFIGURATION_LINE =
        Pattern.compile("set routing-instances (?<vrfName>\\S+) instance-type virtual-router");

    public L3VrfReader(Cli cli) {
        this.cli = cli;
    }

    @Nonnull
    @Override
    public List<NetworkInstanceKey> getAllIds(
        @Nonnull InstanceIdentifier<NetworkInstance> instanceIdentifier,
        @Nonnull ReadContext readContext) throws ReadFailedException {

        return getL3VrfNames(this, cli, instanceIdentifier, readContext).stream()
            .map(NetworkInstanceKey::new)
            .collect(Collectors.toList());
    }

    @Override
    public void readCurrentAttributes(
        @Nonnull InstanceIdentifier<NetworkInstance> instanceIdentifier,
        @Nonnull NetworkInstanceBuilder networkInstanceBuilder,
        @Nonnull ReadContext readContext) throws ReadFailedException {

        String name = instanceIdentifier.firstKeyOf(NetworkInstance.class).getName();
        networkInstanceBuilder.setName(name);
    }

    public static <O extends DataObject, B extends Builder<O>> List<String> getL3VrfNames(
        CliReader<O, B> cliReader,
        Cli cli,
        InstanceIdentifier<O> instanceIdentifier,
        ReadContext readContext) throws ReadFailedException {

        String output = cliReader.blockingRead(DISPLAY_CONF_VRF, cli, instanceIdentifier, readContext);
        return ParsingUtils.parseFields(output, 0,
            VRF_CONFIGURATION_LINE::matcher,
            matcher -> matcher.group("vrfName"),
            s -> s);
    }
}