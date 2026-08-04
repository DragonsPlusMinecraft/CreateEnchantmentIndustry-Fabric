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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import plus.dragons.createdragonsplus.common.fluids.SolidRenderFluidType;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.common.network.CEINetwork;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client.contraptions.actors.enderWovenBag.EnderWovenBagClientPacketHandler;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client.ponder.CEIAPonderPlugin;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client.registry.CEIAPartialModels;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.contraptions.actors.enderWovenBag.ContraptionEnderWovenBagPocketChangePacket;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.contraptions.actors.enderWovenBag.EnderWovenBagItem;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.CEIABlocks;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.CEIAFluids;

/** Physical-client initialization for the Zenith enchanting integration. */
public final class CEIAClient {
    private CEIAClient() {}

    public static void initialize() {
        CEIAPartialModels.register();
        if (Apotheosis.enableEnch || Apotheosis.enableAdventure)
            PonderIndex.addPlugin(new CEIAPonderPlugin());
        ItemProperties.register(
                CEIABlocks.ENDER_WOVEN_BAG.asItem(),
                CEICommon.asResource("open"),
                EnderWovenBagItem::override);
        registerFluidRendering();
        ClientPlayNetworking.registerGlobalReceiver(
                CEINetwork.ENDER_WOVEN_BAG_TRACKING,
                (client, handler, buffer, responseSender) -> {
                    var packet = ContraptionEnderWovenBagPocketChangePacket.decode(buffer);
                    client.execute(() -> EnderWovenBagClientPacketHandler.handle(packet));
                });
    }

    private static void registerFluidRendering() {
        var entry = CEIAFluids.INFUSED_DRAGON_BREATH;
        SolidRenderFluidType type = CEIAFluids.INFUSED_DRAGON_BREATH_TYPE;
        var handler = new SimpleFluidRenderHandler(
                type.getStillTexture(), type.getFlowingTexture(), type.getTintColor());
        FluidRenderHandlerRegistry.INSTANCE.register(entry.getSource(), entry.get(), handler);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), entry.getSource(), entry.get());
    }
}
