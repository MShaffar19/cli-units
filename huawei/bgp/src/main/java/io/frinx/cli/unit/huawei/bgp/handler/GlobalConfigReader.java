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

package io.frinx.cli.unit.huawei.bgp.handler;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliConfigReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import io.frinx.openconfig.network.instance.NetworInstance;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.global.base.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.global.base.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.bgp.rev170202.bgp.top.bgp.GlobalBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstanceKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.types.inet.rev170403.AsNumber;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.types.yang.rev170403.DottedQuad;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class GlobalConfigReader implements CliConfigReader<Config, ConfigBuilder> {

    private static final String DISPLAY_BGP_CONFIG =
            "display current-configuration configuration bgp | include ^bgp|^ *router-id|^ ipv4-family vpn-instance";
    private static final Pattern AS_PATTERN = Pattern.compile("bgp (?<as>\\S*).*");
    private static final Pattern ROUTER_ID_PATTERN_GLOBAL = Pattern.compile("\\s*router-id (?<routerId>\\S*).*");
    private static final Pattern ROUTER_ID_PATTERN =
            Pattern.compile("\\s*ipv4-family vpn-instance (?<vrf>\\S*)\\s+router-id (?<routerId>\\S*).*");

    private Cli cli;

    public GlobalConfigReader(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void merge(@Nonnull Builder<? extends DataObject> builder, @Nonnull Config config) {
        ((GlobalBuilder) builder).setConfig(config);
    }

    @Override
    public void readCurrentAttributes(@Nonnull InstanceIdentifier<Config> id,
                                             @Nonnull ConfigBuilder configBuilder,
                                             @Nonnull ReadContext readContext) throws ReadFailedException {
        String output = blockingRead(DISPLAY_BGP_CONFIG, cli, id, readContext);
        NetworkInstanceKey vrfKey = id.firstKeyOf(NetworkInstance.class);

        parseConfigAttributes(output, configBuilder, vrfKey);
    }

    @VisibleForTesting
    public static void parseConfigAttributes(String output, ConfigBuilder builder, NetworkInstanceKey vrfKey) {
        parseGlobalAs(output, builder);
        if (vrfKey.equals(NetworInstance.DEFAULT_NETWORK)) {
            setGlobalRouterId(builder, output);
        } else {
            setVrfRouterId(builder, output, vrfKey.getName());
        }
    }

    private static void setGlobalRouterId(ConfigBuilder configBuilder, String output) {
        output = realignOutput(output);

        ParsingUtils.parseField(output, 0,
            ROUTER_ID_PATTERN_GLOBAL::matcher,
            matcher -> matcher.group("routerId"),
            (String value) -> configBuilder.setRouterId(new DottedQuad(value)));
    }

    private static String realignOutput(String output) {
        output = output.replaceAll(ParsingUtils.NEWLINE.pattern(), "");
        output = output.replaceAll("ipv4-family", "\nipv4-family");
        return output;
    }

    private static void setVrfRouterId(ConfigBuilder configBuilder, String output, String vrf) {
        output = realignOutput(output);

        ParsingUtils.NEWLINE.splitAsStream(output)
                .map(String::trim)
                .map(ROUTER_ID_PATTERN::matcher)
                .filter(Matcher::matches)
                .filter(m -> m.group("vrf").equals(vrf))
                .map(m -> m.group("routerId"))
                .findFirst()
                .map(DottedQuad::new)
                .ifPresent(configBuilder::setRouterId);
    }

    @VisibleForTesting
    public static void parseGlobalAs(String output, ConfigBuilder configBuilder) {
        ParsingUtils.parseField(output, 0,
            AS_PATTERN::matcher,
            matcher -> matcher.group("as"),
            (String value) -> configBuilder.setAs(new AsNumber(Long.valueOf(value))));
    }

}