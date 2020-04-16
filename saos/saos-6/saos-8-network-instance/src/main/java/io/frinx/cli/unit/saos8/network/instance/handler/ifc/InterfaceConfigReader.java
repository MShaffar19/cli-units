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

package io.frinx.cli.unit.saos8.network.instance.handler.ifc;

import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.saos8.network.instance.handler.l2vsi.ifc.L2VSICpuSubinterfaceConfigReader;
import io.frinx.cli.unit.saos8.network.instance.handler.l2vsi.ifc.L2VSISubPortConfigReader;
import io.frinx.cli.unit.utils.CliConfigReader;
import io.frinx.translate.unit.commons.handler.spi.CompositeReader;
import java.util.ArrayList;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.interfaces._interface.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.network.instance.interfaces._interface.ConfigBuilder;

public class InterfaceConfigReader extends CompositeReader<Config, ConfigBuilder>
        implements CliConfigReader<Config, ConfigBuilder> {

    public InterfaceConfigReader(Cli cli) {
        super(new ArrayList<Child<Config, ConfigBuilder>>() {
            {
                add(new L2VSISubPortConfigReader(cli));
                add(new L2VSICpuSubinterfaceConfigReader(cli));
            }
        });
    }
}
