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

package io.frinx.cli.unit.saos.qos.handler.scheduler;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.qos.rev161216.qos.scheduler.top.scheduler.policies.SchedulerPolicyKey;

public class SchedulerPolicyReaderTest {

    private static final String OUTPUT = "traffic-profiling set meter-provisioning eir\n"
           + "traffic-profiling set port 1 mode advanced\n"
           + "traffic-profiling set port 2 mode advanced\n"
           + "traffic-profiling set port 6 mode hierarchical-vlan\n"
           + "traffic-profiling standard-profile create port 1 profile 1 name CIA_CoS0 cir 50048 eir 0 cbs 8 ebs 0\n"
           + "traffic-profiling standard-profile set port 1 profile CIA_CoS0\n"
           + "traffic-profiling standard-profile create port 1 profile 2 name V4096 cir 10048 eir 0 cbs 256 ebs\n"
           + "traffic-profiling standard-profile create port 5 profile 1 name Test1 cir 10048 eir 0 cbs 128 ebs 0\n"
           + "traffic-profiling standard-profile create port 5 profile 2 name Test2 cir 10048 eir 0 cbs 128 ebs 0\n"
           + "traffic-profiling enable port 2\n"
           + "traffic-profiling enable\n"
           + "traffic-services queuing queue-map create rcos-map NNI-NNI\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 1 queue 1\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 2 queue 2\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 3 queue 3\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 4 queue 4\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 5 queue 5\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 6 queue 6\n"
           + "traffic-services queuing queue-map set rcos-map NNI-NNI rcos 7 queue 7\n"
           + "log flash add filter default traffic-profiling-mgr info\n"
           + "traffic-services queuing egress-port-queue-group set queue 3 port 4 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set port 7 cir 101056\n"
           + "traffic-services queuing egress-port-queue-group set queue 0 port 7 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set queue 1 port 7 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set queue 2 port 7 scheduler-weight 67\n"
           + "traffic-services queuing egress-port-queue-group set queue 3 port 7 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set queue 4 port 5 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set queue 6 port 6 scheduler-weight 6\n"
           + "traffic-services queuing egress-port-queue-group set queue 7 port 7 scheduler-weight 3\n"
           + "traffic-profiling set port 1 mode advanced\n";

    @Test
    public void getAllIdsTest() {
        List<SchedulerPolicyKey> ids = Arrays.asList(
                new SchedulerPolicyKey("Test2"),
                new SchedulerPolicyKey("Test1"),
                new SchedulerPolicyKey("4"),
                new SchedulerPolicyKey("5"),
                new SchedulerPolicyKey("6"),
                new SchedulerPolicyKey("7"),
                new SchedulerPolicyKey("CIA_CoS0"),
                new SchedulerPolicyKey("V4096"));

        Assert.assertEquals(ids, SchedulerPolicyReader.getAllIds(OUTPUT));
    }
}