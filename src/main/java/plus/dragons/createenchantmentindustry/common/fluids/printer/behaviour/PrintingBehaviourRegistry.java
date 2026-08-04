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

package plus.dragons.createenchantmentindustry.common.fluids.printer.behaviour;

import com.mojang.serialization.DataResult;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import plus.dragons.createenchantmentindustry.api.registry.CEIRegistries;
import plus.dragons.createenchantmentindustry.common.CEICommon;

/** Fabric registry and priority dispatcher for Printer behaviour providers. */
public final class PrintingBehaviourRegistry {
    public static final Registry<PrintingBehaviourProvider> REGISTRY = FabricRegistryBuilder
            .createSimple(CEIRegistries.PRINTING_BEHAVIOUR_PROVIDER)
            .buildAndRegister();

    private PrintingBehaviourRegistry() {}

    static void registerBuiltin(String name, Supplier<PrintingBehaviourProvider> provider) {
        Registry.register(REGISTRY, CEICommon.asResource(name), provider.get());
    }

    static DataResult<PrintingBehaviour> create(Level level, SmartFluidTankBehaviour tank, ItemStack stack) {
        return REGISTRY.stream()
                .sorted(Comparator.comparingInt(PrintingBehaviourProvider::priority).reversed())
                .map(entry -> Objects.requireNonNull(
                        entry.provider().create(level, tank, stack),
                        () -> "Printing behaviour provider " + REGISTRY.getKey(entry) + " returned null"))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .findFirst()
                .orElseGet(() -> DataResult.success(new RecipePrintingBehaviour(stack)));
    }
}
