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

import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.dragons.createenchantmentindustry.client.model.CEIPartialModels;
import plus.dragons.createenchantmentindustry.client.ponder.CEIPonderPlugin;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlocks;
import plus.dragons.createenchantmentindustry.common.registry.CEIDataMaps;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;

public final class CEIClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CEIClient.class);

    @Override
    public void onInitializeClient() {
        CEIClientNetwork.register();
        CEIPartialModels.register();
        PonderIndex.addPlugin(new CEIPonderPlugin());
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.cutoutMipped(),
                CEIBlocks.BLAZE_ENCHANTER.get(),
                CEIBlocks.CLASSIC_BLAZE_ENCHANTER.get(),
                CEIBlocks.BLAZE_FORGER.get(),
                CEIBlocks.EXPERIENCE_LANTERN.get());
        initializeIntegrationClient(
                ModIntegration.APOTHEOSIS,
                "plus.dragons.createenchantmentindustry.integration.apotheosis.client.CEIAXClient");
        initializeIntegrationClient(
                ModIntegration.APOTHIC_ENCHANTING,
                "plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client.CEIAClient");
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CEIDataMaps.clearClientSnapshot());
    }

    private static void initializeIntegrationClient(ModIntegration integration, String className) {
        if (!integration.enabled())
            return;
        try {
            Class.forName(className, true, CEIClient.class.getClassLoader())
                    .getMethod("initialize")
                    .invoke(null);
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("{} client integration source set is not present", integration.id());
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Failed to initialize " + integration.id() + " client integration", exception);
        }
    }
}
