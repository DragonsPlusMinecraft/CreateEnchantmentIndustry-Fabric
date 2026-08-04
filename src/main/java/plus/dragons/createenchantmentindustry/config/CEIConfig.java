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

package plus.dragons.createenchantmentindustry.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;
import plus.dragons.createenchantmentindustry.common.CEICommon;

public final class CEIConfig {
    private static final CEICommonConfig COMMON_CONFIG = new CEICommonConfig();
    private static final CEIClientConfig CLIENT_CONFIG = new CEIClientConfig();
    private static final CEIServerConfig SERVER_CONFIG = new CEIServerConfig();
    private static final ForgeConfigSpec COMMON_SPEC = createSpec(COMMON_CONFIG);
    private static final ForgeConfigSpec CLIENT_SPEC = createSpec(CLIENT_CONFIG);
    private static final ForgeConfigSpec SERVER_SPEC = createSpec(SERVER_CONFIG);
    private static boolean registered;

    private CEIConfig() {}

    private static ForgeConfigSpec createSpec(ConfigBase config) {
        return new ForgeConfigSpec.Builder().configure(builder -> {
            config.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue();
    }

    public static void register() {
        if (registered)
            return;
        registered = true;
        Util.make(COMMON_SPEC, spec -> ForgeConfigRegistry.INSTANCE.register(CEICommon.ID, Type.COMMON, spec));
        Util.make(CLIENT_SPEC, spec -> ForgeConfigRegistry.INSTANCE.register(CEICommon.ID, Type.CLIENT, spec));
        Util.make(SERVER_SPEC, spec -> ForgeConfigRegistry.INSTANCE.register(CEICommon.ID, Type.SERVER, spec));
    }

    public static CEICommonConfig common() {
        return COMMON_CONFIG;
    }

    public static CEIClientConfig client() {
        return CLIENT_CONFIG;
    }

    public static CEIServerConfig server() {
        return SERVER_CONFIG;
    }

    public static CEIKineticsConfig kinetics() {
        return SERVER_CONFIG.kinetics;
    }

    public static CEIStressConfig stress() {
        return SERVER_CONFIG.kinetics.stressValues;
    }

    public static CEIFluidsConfig fluids() {
        return SERVER_CONFIG.fluids;
    }

    public static CEIEnchantmentsConfig enchantments() {
        return SERVER_CONFIG.enchantments;
    }

    public static CEIProcessingConfig processing() {
        return SERVER_CONFIG.processing;
    }

    public static CEIFeaturesConfig features() {
        return COMMON_CONFIG.features;
    }
}
