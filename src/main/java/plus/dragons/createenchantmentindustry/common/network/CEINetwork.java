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

package plus.dragons.createenchantmentindustry.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import plus.dragons.createenchantmentindustry.common.CEICommon;

/** Fabric play channels used by CEI. */
public final class CEINetwork {
    public static final ResourceLocation DATA_MAP_SYNC = CEICommon.asResource("data_map_sync");
    public static final ResourceLocation ENDER_WOVEN_BAG_TRACKING = CEICommon.asResource("ender_woven_bag_tracking");

    private CEINetwork() {}

    public static void register() {
        // Core currently has no C2S receiver. Optional integration receivers register lazily.
    }

    public static void sendDataMapSnapshot(ServerPlayer player) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        CEIDataMapSyncPacket.encode(CEIDataMapSyncPacket.create(), buffer);
        ServerPlayNetworking.send(player, DATA_MAP_SYNC, buffer);
    }
}
