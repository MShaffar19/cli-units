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

package io.frinx.cli.ios.bgp.handler.local.aggregates;

import static io.frinx.cli.ios.bgp.handler.BgpProtocolReader.DEFAULT_BGP_INSTANCE;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.handlers.bgp.BgpListReader;
import io.frinx.cli.io.Cli;
import io.frinx.cli.ios.bgp.handler.neighbor.NeighborReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.net.util.SubnetUtils;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.local.routing.rev170515.local.aggregate.top.LocalAggregatesBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.local.routing.rev170515.local.aggregate.top.local.aggregates.Aggregate;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.local.routing.rev170515.local.aggregate.top.local.aggregates.AggregateBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.local.routing.rev170515.local.aggregate.top.local.aggregates.AggregateKey;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.network.instance.rev170228.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.types.inet.rev170403.IpPrefix;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class BgpLocalAggregateReader implements BgpListReader.BgpConfigListReader<Aggregate, AggregateKey, AggregateBuilder> {

    private static final String GROUP_IP = "ip";
    private static final String GROUP_MASK = "mask";
    static final String SH_SUMM = "show running-config | include ^router bgp|^ *address-family|^ *network";
    private static final Pattern NEIGHBOR_LINE = Pattern.compile("network (?<ip>\\S*) mask (?<mask>\\S*).*");
    private final Cli cli;

    public BgpLocalAggregateReader(Cli cli) {
        this.cli = cli;
    }

    @VisibleForTesting
    static List<AggregateKey> getVrfAggregateKeys(String output, String vrfName) {
        Optional<String> optionalVrfOutput =
                Arrays.stream(NeighborReader.splitOutput(output))
                        .filter(value -> value.contains(vrfName))
                        .reduce((s1, s2) -> s1 + s2);

        if (optionalVrfOutput.isPresent()) {
            return ParsingUtils.parseFields(optionalVrfOutput.get().replaceAll("network", "\nnetwork"), 0,
                    NEIGHBOR_LINE::matcher,
                    BgpLocalAggregateReader::resolveGroups,
                    value -> new AggregateKey(parsePrefix(parseNetworkPrefix(value))));
        }
        return new ArrayList<>();
    }

    private static IpPrefix parsePrefix(String s) {
        return new IpPrefix(s.toCharArray());
    }

    @VisibleForTesting
    static List<AggregateKey> getDefaultAggregateKeys(String output) {
        Optional<String> optionalVrfOutput =
                Arrays.stream(NeighborReader.splitOutput(output))
                        .filter(value -> !value.contains("vrf"))
                        .reduce((s1, s2) -> s1 + s2);

        if (optionalVrfOutput.isPresent()) {
            return ParsingUtils.parseFields(optionalVrfOutput.get().replaceAll("network", "\nnetwork"), 0,
                    NEIGHBOR_LINE::matcher,
                    BgpLocalAggregateReader::resolveGroups,
                    value -> new AggregateKey(parsePrefix(parseNetworkPrefix(value))));
        }
        return new ArrayList<>();
    }

    private static String parseNetworkPrefix(HashMap<String, String> params) {
        SubnetUtils utils = new SubnetUtils(params.get(GROUP_IP), params.get(GROUP_MASK));
        return utils.getInfo().getCidrSignature();
    }

    private static HashMap<String, String> resolveGroups(Matcher m) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(GROUP_IP, m.group(GROUP_IP));
        hashMap.put(GROUP_MASK, m.group(GROUP_MASK));

        return hashMap;
    }

    @Nonnull
    @Override
    public List<AggregateKey> getAllIdsForType(@Nonnull InstanceIdentifier<Aggregate> instanceIdentifier,
                                               @Nonnull ReadContext readContext) throws ReadFailedException {
        String niName = instanceIdentifier.firstKeyOf(NetworkInstance.class).getName();

        if (DEFAULT_BGP_INSTANCE.equals(niName)) {
            return getDefaultAggregateKeys(blockingRead(SH_SUMM, cli, instanceIdentifier, readContext));
        } else {
            return getVrfAggregateKeys(blockingRead(SH_SUMM, cli, instanceIdentifier, readContext), niName);
        }
    }

    @Override
    public void merge(@Nonnull Builder<? extends DataObject> builder, @Nonnull List<Aggregate> list) {
        ((LocalAggregatesBuilder) builder).setAggregate(list);
    }

    @Override
    public void readCurrentAttributesForType(@Nonnull InstanceIdentifier<Aggregate> instanceIdentifier,
                                             @Nonnull AggregateBuilder aggregateBuilder, @Nonnull ReadContext readContext) throws ReadFailedException {
        aggregateBuilder.setKey(instanceIdentifier.firstKeyOf(Aggregate.class));
    }
}
