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

package plus.dragons.createenchantmentindustry.common;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import plus.dragons.createdragonsplus.common.CDPRegistrate;
import plus.dragons.createenchantmentindustry.common.crafting.CEIApotheosisModuleCondition;
import plus.dragons.createenchantmentindustry.common.fluids.printer.behaviour.CEIPrintingBehaviours;
import plus.dragons.createenchantmentindustry.common.processing.EnchantmentProcessingRules;
import plus.dragons.createenchantmentindustry.common.registry.CEIAdvancements;
import plus.dragons.createenchantmentindustry.common.registry.CEIArmInterationPoints;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlockEntities;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlocks;
import plus.dragons.createenchantmentindustry.common.registry.CEICreativeModeTabs;
import plus.dragons.createenchantmentindustry.common.registry.CEIDataMaps;
import plus.dragons.createenchantmentindustry.common.registry.CEIEnchantments;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;
import plus.dragons.createenchantmentindustry.common.registry.CEIItemAttributes;
import plus.dragons.createenchantmentindustry.common.registry.CEIItems;
import plus.dragons.createenchantmentindustry.common.registry.CEIMountedStorageTypes;
import plus.dragons.createenchantmentindustry.common.registry.CEIRecipes;
import plus.dragons.createenchantmentindustry.common.registry.CEIStats;
import plus.dragons.createenchantmentindustry.config.CEIConfig;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;

public final class CEICommon implements ModInitializer {
    public static final String ID = "create_enchantment_industry";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    @Override
    public void onInitialize() {
        CEIConfig.register();
        CEIApotheosisModuleCondition.register();

        // Force every Registrate declaration to load before the single Fabric registration pass.
        CEIFluids.register();
        CEIBlocks.register();
        CEIBlockEntities.register();
        CEIItems.register();
        CEIEnchantments.register();
        CEIArmInterationPoints.register();
        CEIMountedStorageTypes.register();
        CEIStats.register();
        initializeIntegration(
                ModIntegration.APOTHIC_ENCHANTING,
                "plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon");
        initializeIntegration(
                ModIntegration.APOTHEOSIS,
                "plus.dragons.createenchantmentindustry.integration.apotheosis.common.CEIAXCommon");
        REGISTRATE.register();

        finishIntegration(
                ModIntegration.APOTHIC_ENCHANTING,
                "plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon");
        finishIntegration(
                ModIntegration.APOTHEOSIS,
                "plus.dragons.createenchantmentindustry.integration.apotheosis.common.CEIAXCommon");

        // These registries and callbacks refer to entries created by Registrate.
        CEIRecipes.register();
        CEICreativeModeTabs.register();
        CEIItemAttributes.register();
        CEIPrintingBehaviours.register();
        CEIDataMaps.register();
        CEIFluids.initialize();
        CEIBlockEntities.registerStorageProviders();
        CEIAdvancements.register();
        CEIAdvancements.BuiltinTriggersQuickDeploy.register();
        ServerLifecycleEvents.SERVER_STARTED.register(EnchantmentProcessingRules::warnLegacyDataMaps);
    }

    private static void initializeIntegration(ModIntegration integration, String className) {
        if (!integration.enabledForRegistration())
            return;
        try {
            Class.forName(className, true, CEICommon.class.getClassLoader()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("{} integration source set is not present", integration.id());
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Failed to initialize " + integration.id() + " integration", exception);
        }
    }

    private static void finishIntegration(ModIntegration integration, String className) {
        if (!integration.enabledForRegistration())
            return;
        try {
            Class.forName(className, true, CEICommon.class.getClassLoader())
                    .getMethod("initialize")
                    .invoke(null);
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("{} integration source set is not present", integration.id());
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Failed to finish " + integration.id() + " integration", exception);
        }
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(ID, name);
    }

    public static String asLocalization(String key) {
        return ID + "." + key;
    }
}
