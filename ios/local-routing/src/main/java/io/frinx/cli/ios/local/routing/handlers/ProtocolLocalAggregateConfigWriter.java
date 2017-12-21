/*
 * Copyright © 2017 Frinx and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package io.frinx.cli.ios.local.routing.handlers;

import com.google.common.collect.Lists;

import io.frinx.cli.io.Cli;
import io.frinx.cli.ios.bgp.handler.local.aggregates.BgpLocalAggregateConfigWriter;
import io.frinx.cli.registry.common.CompositeWriter;

public class ProtocolLocalAggregateConfigWriter extends CompositeWriter {

    public ProtocolLocalAggregateConfigWriter(final Cli cli) {
        super(Lists.newArrayList(
            new BgpLocalAggregateConfigWriter(cli)
        ));
    }
}