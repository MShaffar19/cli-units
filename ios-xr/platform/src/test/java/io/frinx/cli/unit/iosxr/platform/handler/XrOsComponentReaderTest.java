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

package io.frinx.cli.unit.iosxr.platform.handler;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.http.frinx.openconfig.net.yang.platform.rev161222.platform.component.top.components.ComponentKey;

public class XrOsComponentReaderTest {

    public static final String OUTPUT_INVENTORY = "Fri Aug  3 09:23:03.519 MET_DST\n"
        +    "NAME: \"module 0/RP0/CPU0\", DESCR: \"ASR 99 Route Processor for Packet Transport\"\n"
        +    "PID: A99-RP2-TR, VID: V03, SN: FOC2044N5HC\n"
        +    "\n"
        +    "NAME: \"module 0/RP1/CPU0\", DESCR: \"ASR 99 Route Processor for Packet Transport\"\n"
        +    "PID: A99-RP2-TR, VID: V03, SN: FOC2036NSFF\n"
        +    "\n"
        +    "NAME: \"fantray 0/FT0/SP\", DESCR: \"ASR-9922 Fan Tray V2\"\n"
        +    "PID: ASR-9922-FAN-V2, VID: V01, SN: FOC2130NZRD\n"
        +    "\n"
        +    "NAME: \"fan0 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan1 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan2 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan3 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan4 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan5 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan6 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan7 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan8 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan9 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan10 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan11 0/FT0/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fantray 0/FT1/SP\", DESCR: \"ASR-9922 Fan Tray V2\"\n"
        +    "PID: ASR-9922-FAN-V2, VID: V01, SN: FOC2130NZQU\n"
        +    "\n"
        +    "NAME: \"fan0 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan1 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan2 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan3 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan4 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan5 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan6 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan7 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan8 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan9 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan10 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan11 0/FT1/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fantray 0/FT2/SP\", DESCR: \"ASR-9922 Fan Tray V2\"\n"
        +    "PID: ASR-9922-FAN-V2, VID: V01, SN: FOC2130NZRF\n"
        +    "\n"
        +    "NAME: \"fan0 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan1 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan2 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan3 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan4 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan5 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan6 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan7 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan8 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan9 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan10 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan11 0/FT2/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fantray 0/FT3/SP\", DESCR: \"ASR-9922 Fan Tray V2\"\n"
        +    "PID: ASR-9922-FAN-V2, VID: V01, SN: FOC2130NZRL\n"
        +    "\n"
        +    "NAME: \"fan0 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan1 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan2 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan3 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan4 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan5 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan6 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan7 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan8 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan9 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan10 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"fan11 0/FT3/SP\", DESCR: \"ASR9K Generic Fan\"\n"
        +    "PID:    , VID: N/A, SN: \n"
        +    "\n"
        +    "NAME: \"module 0/0/CPU0\", DESCR: \"400G Modular Linecard, Service Edge Optimized\"\n"
        +    "PID: A9K-MOD400-SE, VID: V06, SN: FOC2124NJ8B\n"
        +    "\n"
        +    "NAME: \"module 0/0/0\", DESCR: \"ASR 9000 20-port 10GE Modular Port Adapter\"\n"
        +    "PID: A9K-MPA-20X10GE, VID: V03, SN: FOC2130P305\n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/0\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270NF5     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/1\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270NFL     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/2\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MYF     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/3\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MYM     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/4\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY5     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/5\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MZ2     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/6\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY6     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/7\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY9     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/8\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MYE     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/9\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY4     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/10\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270N12     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/11\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270NFN     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/12\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270NFR     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/13\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MYA     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/14\", DESCR: \"10GBASE-ER 1550nm SMF 40KM\"\n"
        +    "PID: SFP-10G-ER          , VID: V02 , SN: ONT2106004V     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/15\", DESCR: \"10GBASE-ER 1550nm SMF 40KM\"\n"
        +    "PID: SFP-10G-ER          , VID: V02 , SN: ONT210700CW     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/16\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270N13     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/17\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY8     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/18\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270NG2     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/0/19\", DESCR: \"10GBASE-LR SFP+ Module for SMF\"\n"
        +    "PID: SFP-10G-LR          , VID: V02 , SN: OPM21270MY7     \n"
        +    "\n"
        +    "NAME: \"module 0/0/1\", DESCR: \"ASR 9000 2-port 100GE Modular Port Adapter\"\n"
        +    "PID: A9K-MPA-2X100GE, VID: V03, SN: FOC2132N1AG\n"
        +    "\n"
        +    "NAME: \"module mau 0/0/1/0\", DESCR: \"100GBASE-ER4 CFP2 Module for SMF (<40 km)\"\n"
        +    "PID: CFP2-100G-ER4   , VID: V01 , SN: FLJ2052H00E     \n"
        +    "\n"
        +    "NAME: \"module mau 0/0/1/1\", DESCR: \"CPAK 100G LR4\"\n"
        +    "PID: CPAK-100G-LR4   , VID: V06 , SN: FBN21311718     \n"
        +    "\n"
        +    "NAME: \"module 0/1/CPU0\", DESCR: \"400G Modular Linecard, Service Edge Optimized\"\n"
        +    "PID: A9K-MOD400-SE, VID: V06, SN: FOC2124NJ5X\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS0/M0/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM212900YT\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS0/M1/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM2129010L\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS0/M2/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM2131020D\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS1/M0/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM2131022G\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS1/M1/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM21310228\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS1/M2/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM2131021B\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS2/M0/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM212900X7\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS2/M1/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM212900YK\n"
        +    "\n"
        +    "NAME: \"power-module 0/PS2/M2/SP\", DESCR: \"6kW AC V3 Power Module\"\n"
        +    "PID: PWR-6KW-AC-V3, VID: V03, SN: DTM2131020F\n"
        +    "\n"
        +    "NAME: \"module 0/FC0/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2118NETW\n"
        +    "\n"
        +    "NAME: \"module 0/FC1/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2131NMQ4\n"
        +    "\n"
        +    "NAME: \"module 0/FC2/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2131NMSH\n"
        +    "\n"
        +    "NAME: \"module 0/FC3/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2123NTEV\n"
        +    "\n"
        +    "NAME: \"module 0/FC4/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2128NGDC\n"
        +    "\n"
        +    "NAME: \"module 0/FC5/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2131NMVJ\n"
        +    "\n"
        +    "NAME: \"module 0/FC6/SP\", DESCR: \"ASR 9900 Series Switch Fabric Card 2\"\n"
        +    "PID: A99-SFC2, VID: V02 , SN: FOC2131NMTA\n"
        +    "\n"
        +    "NAME: \"chassis ASR-9922\", DESCR: \"ASR 9922 20 Line Card Slot Chassis with V3 AC PEM\"\n"
        +    "PID: ASR-9922, VID: V01, SN: FOX2131PJDT\n";


    @Test
    public void testParseComponents() throws Exception {
        List<ComponentKey> componentKeys = XrOsComponentReader.parseComponents(OUTPUT_INVENTORY);

        Assert.assertEquals(97, componentKeys.size());
    }
}