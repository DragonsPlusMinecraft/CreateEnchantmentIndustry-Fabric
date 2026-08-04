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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;
import plus.dragons.createenchantmentindustry.common.CEICommon;

public class CEIAConfig {
    private static final CEIAClientConfig CLIENT_CONFIG = new CEIAClientConfig();
    private static final CEIAServerConfig SERVER_CONFIG = new CEIAServerConfig();
    private static final ForgeConfigSpec CLIENT_SPEC = createSpec(CLIENT_CONFIG);
    private static final ForgeConfigSpec SERVER_SPEC = createSpec(SERVER_CONFIG);
    private static boolean registered;

    private CEIAConfig() {}

    private static ForgeConfigSpec createSpec(net.createmod.catnip.config.ConfigBase config) {
        return new ForgeConfigSpec.Builder().configure(builder -> {
            config.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue();
    }

    public static void register() {
        if (registered)
            return;
        registered = true;
        Util.make(CLIENT_SPEC, spec -> ForgeConfigRegistry.INSTANCE.register(
                CEICommon.ID, Type.CLIENT, spec, "create_enchantment_industry-apothic_enchanting-client.toml"));
        Util.make(SERVER_SPEC, spec -> ForgeConfigRegistry.INSTANCE.register(
                CEICommon.ID, Type.SERVER, spec, "create_enchantment_industry-apothic_enchanting-server.toml"));
    }

    public static CEIAClientConfig client() {
        return CLIENT_CONFIG;
    }

    public static CEIAServerConfig server() {
        return SERVER_CONFIG;
    }
}
