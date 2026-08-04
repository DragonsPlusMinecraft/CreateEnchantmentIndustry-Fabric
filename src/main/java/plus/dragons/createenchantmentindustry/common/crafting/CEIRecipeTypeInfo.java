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

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** Loader-neutral recipe type holder whose namespace is supplied by the owning integration. */
public final class CEIRecipeTypeInfo<R extends Recipe<?>> implements IRecipeTypeInfo {
    private final ResourceLocation id;
    private final RecipeSerializer<R> serializer;
    private final RecipeType<R> type;
    private boolean registered;

    public CEIRecipeTypeInfo(ResourceLocation id, Supplier<? extends RecipeSerializer<R>> serializer) {
        this.id = id;
        this.serializer = serializer.get();
        this.type = new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }

    public void register() {
        if (registered)
            return;
        registered = true;
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, type);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeType<?>> T getType() {
        return (T) type;
    }
}
