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

package plus.dragons.createenchantmentindustry.common.registry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import java.util.function.Supplier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.common.crafting.CEIRecipeTypeInfo;
import plus.dragons.createenchantmentindustry.common.fluids.printer.PrintingRecipe;
import plus.dragons.createenchantmentindustry.common.kinetics.grindstone.GrindingRecipe;

public final class CEIRecipes {
    public static final CEIRecipeTypeInfo<PrintingRecipe> PRINTING = create(
            "printing", () -> new PrintingRecipe.Serializer<>(PrintingRecipe::new));
    public static final CEIRecipeTypeInfo<GrindingRecipe> GRINDING = create(
            "grinding", () -> new ProcessingRecipeSerializer<>(GrindingRecipe::new));
    private static boolean registered;

    private CEIRecipes() {}

    public static void register() {
        if (registered)
            return;
        registered = true;
        PRINTING.register();
        GRINDING.register();
    }

    private static <R extends Recipe<?>> CEIRecipeTypeInfo<R> create(
            String name, Supplier<? extends RecipeSerializer<R>> serializer) {
        return new CEIRecipeTypeInfo<>(CEICommon.asResource(name), serializer);
    }
}
