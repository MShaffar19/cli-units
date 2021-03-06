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

package io.frinx.cli.unit.ni.base.handler.vrf;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.fd.honeycomb.translate.spi.builder.BasicCheck;
import io.fd.honeycomb.translate.spi.builder.Check;
import io.fd.honeycomb.translate.util.RWUtils;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliConfigListReader;
import io.frinx.cli.unit.utils.CliReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import io.frinx.translate.unit.commons.handler.spi.CompositeListReader;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.Config;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public abstract class AbstractL3VrfReader
        implements CliConfigListReader<NetworkInstance, NetworkInstanceKey, NetworkInstanceBuilder>,
        CompositeListReader.Child<NetworkInstance, NetworkInstanceKey, NetworkInstanceBuilder> {

    private final Cli cli;

    protected AbstractL3VrfReader(Cli cli) {
        this.cli = cli;
    }

    @Override
    public Check getCheck() {
        return BasicCheck.emptyCheck();
    }

    @Nonnull
    @Override
    public List<NetworkInstanceKey> getAllIds(
            @Nonnull InstanceIdentifier<NetworkInstance> instanceIdentifier,
            @Nonnull ReadContext readContext) throws ReadFailedException {
        InstanceIdentifier<?> id = !instanceIdentifier.getTargetType().equals(NetworkInstance.class)
                ? RWUtils.cutId(instanceIdentifier, NetworkInstance.class)
                : instanceIdentifier;
        return getAllIds(this, id, readContext);
    }

    public List<NetworkInstanceKey> getAllIds(CliReader cliReader, InstanceIdentifier<?> id, ReadContext ctx)
            throws ReadFailedException {
        String output = cliReader.blockingRead(getReadCommand(), cli, id, ctx);
        return ParsingUtils.parseFields(output, 0,
            getVrfLine()::matcher,
            matcher -> matcher.group("vrfName"),
            NetworkInstanceKey::new);
    }

    protected abstract String getReadCommand();

    protected abstract Pattern getVrfLine();

    @VisibleForTesting
    public boolean isL3Vrf(NetworkInstanceKey name, InstanceIdentifier<Config> instanceIdentifier, ReadContext ctx)
            throws ReadFailedException {
        return getAllIds(this, instanceIdentifier, ctx).contains(name);
    }

    @Override
    public void readCurrentAttributes(@Nonnull InstanceIdentifier<NetworkInstance> instanceIdentifier,
                                        @Nonnull NetworkInstanceBuilder networkInstanceBuilder,
                                        @Nonnull ReadContext readContext) {
        networkInstanceBuilder.setName(instanceIdentifier.firstKeyOf(NetworkInstance.class).getName());
    }
}
