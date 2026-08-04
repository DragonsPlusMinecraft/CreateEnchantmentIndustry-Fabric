/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createenchantmentindustry.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import plus.dragons.createenchantmentindustry.common.network.CEIDataMapSyncPacket;
import plus.dragons.createenchantmentindustry.common.network.CEINetwork;
import plus.dragons.createenchantmentindustry.common.registry.CEIDataMaps;

@Environment(EnvType.CLIENT)
public final class CEIClientNetwork {
    private static boolean registered;

    private CEIClientNetwork() {}

    public static synchronized void register() {
        if (registered)
            return;
        registered = true;
        ClientPlayNetworking.registerGlobalReceiver(CEINetwork.DATA_MAP_SYNC, (client, handler, buffer, sender) -> {
            CEIDataMapSyncPacket packet = CEIDataMapSyncPacket.decode(buffer);
            client.execute(() -> CEIDataMaps.applyClientSnapshot(packet.values()));
        });
    }
}
