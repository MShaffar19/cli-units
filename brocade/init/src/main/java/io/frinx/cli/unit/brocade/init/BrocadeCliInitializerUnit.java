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

package io.frinx.cli.unit.brocade.init;

import com.google.common.collect.Sets;
import io.fd.honeycomb.translate.spi.builder.CustomizerAwareReadRegistryBuilder;
import io.fd.honeycomb.translate.spi.builder.CustomizerAwareWriteRegistryBuilder;
import io.fd.honeycomb.translate.spi.write.PostTransactionHook;
import io.frinx.cli.io.Command;
import io.frinx.cli.io.SessionInitializationStrategy;
import io.frinx.cli.registry.api.TranslationUnitCollector;
import io.frinx.cli.registry.spi.TranslateUnit;
import io.frinx.cli.topology.RemoteDeviceId;
import io.frinx.cli.unit.ios.init.IosCliInitializerUnit;
import io.frinx.cli.unit.utils.AbstractUnit;
import io.frinx.translate.unit.commons.handler.spi.ChecksMap;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.cli.topology.rev170520.CliNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.cli.translate.registry.rev170520.Device;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrocadeCliInitializerUnit extends AbstractUnit {

    private static final Logger LOG = LoggerFactory.getLogger(BrocadeCliInitializerUnit.class);

    private static final Command WRITE_MEMORY = Command.writeCommandNoChecks("write memory");

    private static final Pattern INVALID_COMMAND = Pattern.compile("^Invalid input.*");
    private static final Pattern ERROR_COMMAND = Pattern.compile("^Error.*");

    public BrocadeCliInitializerUnit(@Nonnull final TranslationUnitCollector registry) {
        super(registry);
    }

    @Override
    protected Set<Device> getSupportedVersions() {
        return Collections.singleton(BrocadeDevices.BROCADE_GENERIC);
    }

    @Override
    protected String getUnitName() {
        return "Ironware cli init (FRINX) translate unit";
    }

    @Override
    public Set<YangModuleInfo> getYangSchemas() {
        return Collections.emptySet();
    }

    @Override
    public SessionInitializationStrategy getInitializer(@Nonnull final RemoteDeviceId id,
                                                        @Nonnull final CliNode cliNodeConfiguration) {
        return new IosCliInitializerUnit.IosCliInitializer(cliNodeConfiguration, id);
    }

    @SuppressWarnings("IllegalCatch")
    @Override
    public PostTransactionHook getPostTransactionHook(Context ctx) {
        return () -> {
            try {
                LOG.trace("Running Post transaction hook");
//                String output = ctx.getTransport().executeAndRead(WRITE_MEMORY).toCompletableFuture().get();
//                LOG.debug("Post transaction hook invoked successfully with output: {}", output);
            } catch (Exception e) {
                LOG.warn("Unable to execute post transaction hook", e);
                throw new IllegalStateException("Post transaction hook failure", e);
            }
        };
    }

    @Override
    public Set<Pattern> getErrorPatterns() {
        return Sets.newHashSet(INVALID_COMMAND, ERROR_COMMAND);
    }

    @Override
    public void provideHandlers(@Nonnull final CustomizerAwareReadRegistryBuilder readRegistry,
                                @Nonnull final CustomizerAwareWriteRegistryBuilder writeRegistry,
                                @Nonnull final TranslateUnit.Context context) {
        readRegistry.addCheckRegistry(ChecksMap.OPENCONFIG_REGISTRY);
        writeRegistry.addCheckRegistry(ChecksMap.OPENCONFIG_REGISTRY);
    }
}

