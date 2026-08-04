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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.processing.affix.blazeComposer.AffixComposingRules;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry.*;
import plus.dragons.createenchantmentindustry.integration.apotheosis.config.CEIAXConfig;

public class CEIAXCommon {
    public CEIAXCommon() {
        if (!ModIntegration.APOTHEOSIS.enabledForRegistration())
            return;
        CEIAXConfig.register();
        CEIAXFluids.register();
        CEIAXBlocks.register();
        CEIAXBlockEntities.register();
        CEIAXItems.register();
        CEIAXStats.register();
    }

    public static void initialize() {
        CEIAXRecipes.register();
        CEIAXCreativeModeTabs.register();
        CEIAXItemAttributes.register();
        CEIAXFanProcessingTypes.register();
        CEIAXArmInteractionPoints.register();
        CEIAXFluids.initialize();
        CEIAXBlockEntities.registerStorageProviders();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(AffixComposingRules.INSTANCE);
    }
}
