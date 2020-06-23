/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lcn.internal.subhandler;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.openhab.binding.lcn.internal.LcnModuleHandler;
import org.openhab.binding.lcn.internal.common.LcnChannelGroup;
import org.openhab.binding.lcn.internal.common.LcnDefs;
import org.openhab.binding.lcn.internal.common.LcnDefs.RelayStateModifier;
import org.openhab.binding.lcn.internal.common.LcnException;
import org.openhab.binding.lcn.internal.common.PckGenerator;
import org.openhab.binding.lcn.internal.connection.ModInfo;

/**
 * Handles Commands and State changes of roller shutters connected to relays of an LCN module.
 * 
 * @author Fabian Wolter - Initial contribution
 */
@NonNullByDefault
public class LcnModuleRollershutterRelaySubHandler extends AbstractLcnModuleSubHandler {
    public LcnModuleRollershutterRelaySubHandler(LcnModuleHandler handler, ModInfo info) {
        super(handler, info);
    }

    @Override
    public void handleRefresh(LcnChannelGroup channelGroup, int number) {
        info.refreshRelays();
    }

    @Override
    public void handleCommandUpDown(UpDownType command, LcnChannelGroup channelGroup, int number) throws LcnException {
        RelayStateModifier[] relayStateModifiers = createRelayStateModifierArray();
        // direction relay
        relayStateModifiers[number * 2 + 1] = command == UpDownType.DOWN ? LcnDefs.RelayStateModifier.ON
                : LcnDefs.RelayStateModifier.OFF;
        // power relay
        relayStateModifiers[number * 2] = LcnDefs.RelayStateModifier.ON;
        handler.sendPck(PckGenerator.controlRelays(relayStateModifiers));
    }

    @Override
    public void handleCommandStopMove(StopMoveType command, LcnChannelGroup channelGroup, int number)
            throws LcnException {
        RelayStateModifier[] relayStateModifiers = createRelayStateModifierArray();
        // power relay
        relayStateModifiers[number * 2] = command == StopMoveType.MOVE ? LcnDefs.RelayStateModifier.ON
                : LcnDefs.RelayStateModifier.OFF;
        handler.sendPck(PckGenerator.controlRelays(relayStateModifiers));
    }

    @Override
    public void handleStatusMessage(Matcher matcher) {
        // status messages of roller shutters on relays are handled in the relay sub handler
    }

    @Override
    public Collection<Pattern> getPckStatusMessagePatterns() {
        return Collections.emptyList();
    }
}