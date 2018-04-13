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

package io.frinx.cli.iosxr.qos.handler.classifier;

import com.google.common.annotations.VisibleForTesting;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.frinx.cli.io.Cli;
import io.frinx.cli.unit.utils.CliConfigReader;
import io.frinx.cli.unit.utils.ParsingUtils;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.qos.rev161216.qos.classifier.terms.top.terms.term.ActionsBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.qos.rev161216.qos.classifier.terms.top.terms.term.actions.Config;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.qos.rev161216.qos.classifier.terms.top.terms.term.actions.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.qos.rev161216.qos.classifier.top.classifiers.Classifier;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class ActionConfigReader implements CliConfigReader<Config, ConfigBuilder> {

    private static final String SH_POLICY_MAPS = "do show run policy-map | utility egrep \"^policy-map| class %s\"";
    private static final String RAW_POLICY_LINE = "(.*)policy-map (?<name>.+) class %s(.*)";

    private Cli cli;

    public ActionConfigReader(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void readCurrentAttributes(@Nonnull InstanceIdentifier<Config> instanceIdentifier, @Nonnull ConfigBuilder configBuilder, @Nonnull ReadContext readContext) throws ReadFailedException {
        String className = instanceIdentifier.firstKeyOf(Classifier.class).getName();
        String output = blockingRead(f(SH_POLICY_MAPS, className), cli, instanceIdentifier, readContext);
        parsePolicyName(output, configBuilder, className);

    }

    @VisibleForTesting
    public static void parsePolicyName(String output, ConfigBuilder configBuilder, String className) {
        Pattern policyLine = Pattern.compile(String.format(RAW_POLICY_LINE, className));
        ParsingUtils.parseField(output.replaceAll(ParsingUtils.NEWLINE.pattern(), " "), policyLine::matcher,
            matcher -> matcher.group("name"),
            g -> configBuilder.setTargetGroup(g.trim()));
    }

    @Override
    public void merge(@Nonnull Builder<? extends DataObject> builder, @Nonnull Config config) {
        ((ActionsBuilder) builder).setConfig(config);
    }
}