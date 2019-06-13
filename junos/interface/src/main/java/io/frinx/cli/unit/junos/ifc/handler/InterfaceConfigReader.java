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

package io.frinx.cli.unit.junos.ifc.handler;

import io.frinx.cli.ifc.base.handler.AbstractInterfaceConfigReader;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.junos.ifc.Util;
import java.util.regex.Pattern;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.InterfaceType;

public final class InterfaceConfigReader extends AbstractInterfaceConfigReader {

    public static final String SH_SINGLE_INTERFACE_CFG = "show configuration interfaces %s | display set";

    private static final Pattern SHUTDOWN_LINE = Pattern.compile("set interfaces (?<id>\\S+) disable");

    private static final Pattern DESCRIPTION_LINE =
            Pattern.compile("set interfaces (?<id>\\S+) description (?<desc>.*)");

    public InterfaceConfigReader(Cli cli) {
        super(cli);
    }

    @Override
    protected String getReadCommand(String ifcName) {
        return f(SH_SINGLE_INTERFACE_CFG, ifcName);
    }

    @Override
    protected Pattern getShutdownLine() {
        return SHUTDOWN_LINE;
    }

    @Override
    protected Pattern getMtuLine() {
        // mtu is not parsed
        return Pattern.compile("");
    }

    @Override
    protected Pattern getDescriptionLine() {
        return DESCRIPTION_LINE;
    }

    @Override
    public Class<? extends InterfaceType> parseType(final String name) {
        return Util.parseType(name);
    }
}
