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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.contraptions.actors.enderWovenBag;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import plus.dragons.createenchantmentindustry.common.network.CEINetwork;

public record ContraptionEnderWovenBagPocketChangePacket(int entityId, BlockPos localPos, boolean open) {
    public static void encode(ContraptionEnderWovenBagPocketChangePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeBlockPos(packet.localPos);
        buffer.writeBoolean(packet.open);
    }

    public static ContraptionEnderWovenBagPocketChangePacket decode(FriendlyByteBuf buffer) {
        return new ContraptionEnderWovenBagPocketChangePacket(
                buffer.readVarInt(), buffer.readBlockPos(), buffer.readBoolean());
    }

    public static void sendToTracking(Entity entity, ContraptionEnderWovenBagPocketChangePacket packet) {
        for (var player : PlayerLookup.tracking(entity)) {
            FriendlyByteBuf buffer = PacketByteBufs.create();
            encode(packet, buffer);
            ServerPlayNetworking.send(player, CEINetwork.ENDER_WOVEN_BAG_TRACKING, buffer);
        }
    }
}
