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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import plus.dragons.createdragonsplus.common.CDPRegistrate;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.processing.infuser.InfuserBlockEntity;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.*;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.config.CEIAConfig;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.integration.CEIMaxEnchantmentLevel;

public class CEIACommon {
    public static final String ID = CEICommon.ID;
    public static final CDPRegistrate REGISTRATE = CEICommon.REGISTRATE;

    public CEIACommon() {
        if (!ModIntegration.APOTHIC_ENCHANTING.enabledForRegistration())
            return;
        CEIAConfig.register();
        CEIAFluids.register();
        CEIABlocks.register();
        CEIABlockEntities.register();
        CEIAItems.register();
    }

    public static void initialize() {
        CEIARecipes.register();
        CEIACreativeModeTabs.register();
        CEIAItemAttributes.register();
        CEIAFluids.initialize();
        CEIABlockEntities.registerStorageProviders();
        CEIAPackets.register();
        if (Apotheosis.enableEnch)
            CEIMaxEnchantmentLevel.register();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public net.minecraft.resources.ResourceLocation getFabricId() {
                return CEICommon.asResource("infuser_recipe_cache");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                InfuserBlockEntity.RELOAD_LISTENER.onResourceManagerReload(resourceManager);
            }
        });
    }
}
