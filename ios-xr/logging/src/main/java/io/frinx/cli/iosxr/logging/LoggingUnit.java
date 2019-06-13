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

package io.frinx.cli.iosxr.logging;

import com.google.common.collect.Sets;
import io.fd.honeycomb.rpc.RpcService;
import io.fd.honeycomb.translate.impl.read.GenericConfigReader;
import io.fd.honeycomb.translate.impl.write.GenericListWriter;
import io.fd.honeycomb.translate.impl.write.GenericWriter;
import io.fd.honeycomb.translate.spi.builder.CustomizerAwareReadRegistryBuilder;
import io.fd.honeycomb.translate.spi.builder.CustomizerAwareWriteRegistryBuilder;
import io.fd.honeycomb.translate.util.RWUtils;
import io.frinx.cli.io.Cli;
import io.frinx.cli.iosxr.logging.handler.LoggingInterfaceConfigWriter;
import io.frinx.cli.iosxr.logging.handler.LoggingInterfacesReader;
import io.frinx.cli.registry.api.TranslationUnitCollector;
import io.frinx.cli.registry.spi.TranslateUnit;
import io.frinx.cli.unit.iosxr.init.IosXrDevices;
import io.frinx.cli.unit.utils.NoopCliListWriter;
import io.frinx.openconfig.openconfig.logging.IIDs;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.logging.rev171024.$YangModuleInfoImpl;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.logging.rev171024.logging.interfaces.structural.Interfaces;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.logging.rev171024.logging.interfaces.structural.interfaces._interface.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.logging.rev171024.logging.top.LoggingBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;

public final class LoggingUnit implements TranslateUnit {

    private static final InstanceIdentifier<Interfaces> IFCS_ID = InstanceIdentifier.create(Interfaces.class);
    private static final InstanceIdentifier<Config> IFC_CFG_ID = InstanceIdentifier.create(Config.class);

    private final TranslationUnitCollector registry;
    private TranslationUnitCollector.Registration reg;

    public LoggingUnit(@Nonnull final TranslationUnitCollector registry) {
        this.registry = registry;
    }

    public void init() {
        reg = registry.registerTranslateUnit(IosXrDevices.IOS_XR_ALL, this);
    }

    public void close() {
        if (reg != null) {
            reg.close();
        }
    }

    @Override
    public Set<YangModuleInfo> getYangSchemas() {
        return Sets.newHashSet($YangModuleInfoImpl.getInstance());
    }

    @Override
    public Set<RpcService<?, ?>> getRpcs(@Nonnull Context context) {
        return Collections.emptySet();
    }

    @Override
    public void provideHandlers(@Nonnull CustomizerAwareReadRegistryBuilder readRegistry,
                                @Nonnull CustomizerAwareWriteRegistryBuilder writeRegistry, @Nonnull Context context) {
        Cli cli = context.getTransport();
        provideReaders(readRegistry, cli);
        provideWriters(writeRegistry, cli);
    }

    private void provideWriters(CustomizerAwareWriteRegistryBuilder writeRegistry, Cli cli) {
        writeRegistry.add(new GenericListWriter<>(IIDs.LO_IN_INTERFACE, new NoopCliListWriter<>()));
        writeRegistry.subtreeAddAfter(Sets.newHashSet(
                RWUtils.cutIdFromStart(IIDs.LO_IN_IN_CO_ENABLEDLOGGINGFOREVENT, IFC_CFG_ID)),
                new GenericWriter<>(IIDs.LO_IN_IN_CONFIG, new LoggingInterfaceConfigWriter(cli)),
                /*handle after ifc configuration*/ io.frinx.openconfig.openconfig.interfaces.IIDs.IN_IN_CONFIG);
    }

    private void provideReaders(CustomizerAwareReadRegistryBuilder readRegistry, Cli cli) {
        readRegistry.addStructuralReader(IIDs.LOGGING, LoggingBuilder.class);
        readRegistry.subtreeAdd(Sets.newHashSet(
                RWUtils.cutIdFromStart(IIDs.LO_IN_INTERFACE, IFCS_ID),
                RWUtils.cutIdFromStart(IIDs.LO_IN_IN_CONFIG, IFCS_ID),
                RWUtils.cutIdFromStart(IIDs.LO_IN_IN_CO_ENABLEDLOGGINGFOREVENT, IFCS_ID)),
                new GenericConfigReader<>(IIDs.LO_INTERFACES, new LoggingInterfacesReader(cli)));
    }

    @Override
    public String toString() {
        return "IOS XR Logging (Openconfig) translate unit";
    }

}
