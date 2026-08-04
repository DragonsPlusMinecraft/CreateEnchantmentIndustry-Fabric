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

package plus.dragons.createenchantmentindustry.common.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Field;
import java.util.Locale;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import plus.dragons.createenchantmentindustry.common.CEICommon;

/** Fabric resource condition for the two Zenith modules used by CEI integrations. */
public final class CEIApotheosisModuleCondition implements ConditionJsonProvider {
    private static final ResourceLocation ID = CEICommon.asResource("apotheosis_module");
    private static final String ZENITH_CLASS = "dev.shadowsoffire.apotheosis.Apotheosis";

    public static final CEIApotheosisModuleCondition ENCHANTMENT = new CEIApotheosisModuleCondition(Module.ENCHANTMENT);
    public static final CEIApotheosisModuleCondition ADVENTURE = new CEIApotheosisModuleCondition(Module.ADVENTURE);
    private static boolean registered;

    private final Module module;

    private CEIApotheosisModuleCondition(Module module) {
        this.module = module;
    }

    public static void register() {
        if (registered)
            return;
        registered = true;
        ResourceConditions.register(ID, json -> new CEIApotheosisModuleCondition(
                Module.parse(GsonHelper.getAsString(json, "module")))
                        .test());
    }

    private boolean test() {
        if (!FabricLoader.getInstance().isModLoaded("zenith"))
            return false;
        try {
            Class<?> zenith = Class.forName(ZENITH_CLASS, false, CEIApotheosisModuleCondition.class.getClassLoader());
            Field enabled = zenith.getField(module.fieldName);
            return enabled.getBoolean(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to read Zenith module flag " + module.fieldName, exception);
        }
    }

    @Override
    public ResourceLocation getConditionId() {
        return ID;
    }

    @Override
    public void writeParameters(JsonObject object) {
        object.addProperty("module", module.name().toLowerCase(Locale.ROOT));
    }

    private enum Module {
        ENCHANTMENT("enableEnch"),
        ADVENTURE("enableAdventure");

        private final String fieldName;

        Module(String fieldName) {
            this.fieldName = fieldName;
        }

        private static Module parse(String name) {
            try {
                return valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new JsonParseException("Unknown Zenith module '" + name + "'", exception);
            }
        }
    }
}
