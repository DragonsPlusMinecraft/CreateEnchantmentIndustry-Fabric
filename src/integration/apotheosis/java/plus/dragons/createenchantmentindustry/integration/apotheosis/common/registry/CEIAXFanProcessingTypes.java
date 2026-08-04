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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.kinetics.fan.salvaging.SalvagingFanProcessingType;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon;

public class CEIAXFanProcessingTypes {
    public static final Supplier<SalvagingFanProcessingType> SALVAGING = register(
            "salvaging", SalvagingFanProcessingType::new);

    private static <T extends FanProcessingType> Supplier<T> register(String name, Supplier<T> factory) {
        T type = Registry.register(
                CreateBuiltInRegistries.FAN_PROCESSING_TYPE,
                CEIACommon.REGISTRATE.asResource(name),
                factory.get());
        return () -> type;
    }

    public static void register() {}
}
