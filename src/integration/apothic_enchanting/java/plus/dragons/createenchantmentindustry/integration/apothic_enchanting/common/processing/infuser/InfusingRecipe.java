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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.processing.infuser;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.CEIAFluids;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.CEIARecipes;
import plus.dragons.createenchantmentindustry.util.CEIFluidUnits;

/** Create processing wrapper for native CEI recipes and Zenith enchanting recipes. */
public class InfusingRecipe extends ProcessingRecipe<Container> {
    private InfusionStats stats = InfusionStats.EMPTY;

    public InfusingRecipe(ProcessingRecipeParams params) {
        super(CEIARecipes.INFUSING, params);
    }

    public InfusionStats getStats() {
        return stats;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    public static boolean match(InfuserBlockEntity infuser, BasinBlockEntity basin, Recipe<?> recipe) {
        return process(infuser, basin, recipe, true);
    }

    public static boolean apply(InfuserBlockEntity infuser, BasinBlockEntity basin, Recipe<?> recipe) {
        return process(infuser, basin, recipe, false);
    }

    private static boolean process(
            InfuserBlockEntity infuser, BasinBlockEntity basin, Recipe<?> recipe, boolean simulateOnly) {
        if (recipe instanceof InfusingRecipe infusingRecipe) {
            return processNative(infuser, basin, infusingRecipe, simulateOnly);
        }
        if (recipe instanceof EnchantingRecipe enchantingRecipe) {
            return processApotheosis(infuser, basin, enchantingRecipe, simulateOnly);
        }
        return false;
    }

    private static boolean processNative(
            InfuserBlockEntity infuser, BasinBlockEntity basin, InfusingRecipe recipe, boolean simulateOnly) {
        if (!recipe.stats.qualified(infuser.infusionStats)) {
            return false;
        }

        Storage<ItemVariant> availableItems = basin.getInputInventory();
        Storage<FluidVariant> availableFluids = basin.inputTank.getCapability();
        Storage<FluidVariant> reagentTank = infuser.getFluidStorage(null);
        if (reagentTank == null) {
            return false;
        }

        ItemVariant itemInput = ItemVariant.blank();
        FluidStack fluidInput = FluidStack.EMPTY;
        if (!recipe.ingredients.isEmpty()) {
            itemInput = findMatchingItem(availableItems, recipe.ingredients.get(0));
            if (itemInput.isBlank()) {
                return false;
            }
        } else if (!recipe.fluidIngredients.isEmpty()) {
            if (availableFluids == null) {
                return false;
            }
            fluidInput = findMatchingFluid(availableFluids, recipe.fluidIngredients.get(0));
            if (fluidInput.isEmpty()) {
                return false;
            }
        } else {
            return false;
        }

        List<ItemStack> outputItems = new ArrayList<>(recipe.rollResults());
        List<FluidStack> outputFluids = recipe.getFluidResults().stream()
                .filter(stack -> !stack.isEmpty())
                .map(FluidStack::copy)
                .toList();
        if (!matchesFilter(basin.getFilter(), outputItems, outputFluids)) {
            return false;
        }

        long requiredAmount = CEIFluidUnits.millibuckets(
                ApothMiscUtil.getExpCostForSlot((int) recipe.stats.eterna(), 0));
        FluidStack reagent = findInfusingIngredient(reagentTank, requiredAmount);
        if (reagent.isEmpty()) {
            return false;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            if (!itemInput.isBlank()) {
                if (availableItems.extract(itemInput, 1, transaction) != 1) {
                    return false;
                }
            } else if (availableFluids.extract(fluidInput.getType(), fluidInput.getAmount(), transaction) != fluidInput.getAmount()) {
                return false;
            }

            if (reagentTank.extract(reagent.getType(), reagent.getAmount(), transaction) != reagent.getAmount()) {
                return false;
            }
            if (!basin.acceptOutputs(outputItems, outputFluids, transaction)) {
                return false;
            }
            if (!simulateOnly)
                transaction.commit();
            return true;
        }
    }

    private static boolean processApotheosis(
            InfuserBlockEntity infuser, BasinBlockEntity basin, EnchantingRecipe recipe, boolean simulateOnly) {
        Storage<ItemVariant> availableItems = basin.getInputInventory();
        Storage<FluidVariant> reagentTank = infuser.getFluidStorage(null);
        if (reagentTank == null) {
            return false;
        }

        ItemVariant inputVariant = findMatchingItem(availableItems, recipe, infuser.infusionStats);
        if (inputVariant.isBlank()) {
            return false;
        }
        ItemStack input = inputVariant.toStack();
        ItemStack output = recipe.assemble(
                input,
                infuser.infusionStats.eterna(),
                infuser.infusionStats.quanta(),
                infuser.infusionStats.arcana());
        if (output.isEmpty()
                || !matchesFilter(basin.getFilter(), List.of(output), List.of())) {
            return false;
        }

        long requiredAmount = CEIFluidUnits.millibuckets(
                ApothMiscUtil.getExpCostForSlot((int) recipe.getRequirements().eterna(), 0));
        FluidStack reagent = findInfusingIngredient(reagentTank, requiredAmount);
        if (reagent.isEmpty()) {
            return false;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            if (availableItems.extract(inputVariant, 1, transaction) != 1
                    || reagentTank.extract(reagent.getType(), reagent.getAmount(), transaction) != reagent.getAmount()
                    || !basin.acceptOutputs(List.of(output), List.of(), transaction)) {
                return false;
            }
            if (!simulateOnly)
                transaction.commit();
            return true;
        }
    }

    private static ItemVariant findMatchingItem(Storage<ItemVariant> items, Ingredient ingredient) {
        for (StorageView<ItemVariant> view : items.nonEmptyViews()) {
            if (view.getAmount() > 0 && ingredient.test(view.getResource().toStack()))
                return view.getResource();
        }
        return ItemVariant.blank();
    }

    private static ItemVariant findMatchingItem(
            Storage<ItemVariant> items, EnchantingRecipe recipe, InfusionStats stats) {
        for (StorageView<ItemVariant> view : items.nonEmptyViews()) {
            ItemStack input = view.getResource().toStack();
            if (view.getAmount() > 0 && recipe.matches(input, stats.eterna(), stats.quanta(), stats.arcana()))
                return view.getResource();
        }
        return ItemVariant.blank();
    }

    private static FluidStack findMatchingFluid(Storage<FluidVariant> fluids, FluidIngredient ingredient) {
        long required = ingredient.getRequiredAmount();
        for (StorageView<FluidVariant> view : fluids.nonEmptyViews()) {
            FluidStack requested = new FluidStack(view.getResource(), required);
            if (view.getAmount() >= required && ingredient.test(requested))
                return requested;
        }
        return FluidStack.EMPTY;
    }

    private static FluidStack findInfusingIngredient(Storage<FluidVariant> fluids, long amount) {
        for (StorageView<FluidVariant> view : fluids.nonEmptyViews()) {
            if (view.getAmount() >= amount
                    && view.getResource().getFluid().is(CEIAFluids.MOD_TAGS.infusing_ingredients))
                return new FluidStack(view.getResource(), amount);
        }
        return FluidStack.EMPTY;
    }

    private static boolean matchesFilter(
            FilteringBehaviour filter, List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs) {
        if (filter == null) {
            return false;
        }
        if (!itemOutputs.isEmpty()) {
            return filter.test(itemOutputs.get(0));
        }
        if (!fluidOutputs.isEmpty()) {
            return filter.test(fluidOutputs.get(0));
        }
        return false;
    }

    public static boolean canProcessInput(Recipe<?> recipe, ItemStack stack) {
        if (recipe instanceof EnchantingRecipe enchantingRecipe) {
            return enchantingRecipe.getInput().test(stack);
        }
        return !recipe.getIngredients().isEmpty() && recipe.getIngredients().get(0).test(stack);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public void readAdditional(JsonObject json) {
        JsonObject statsJson = GsonHelper.getAsJsonObject(json, "stats");
        stats = new InfusionStats(
                GsonHelper.getAsFloat(statsJson, "eterna"),
                GsonHelper.getAsFloat(statsJson, "quanta"),
                GsonHelper.getAsFloat(statsJson, "arcana"));
    }

    @Override
    public void writeAdditional(JsonObject json) {
        JsonObject statsJson = new JsonObject();
        statsJson.addProperty("eterna", stats.eterna());
        statsJson.addProperty("quanta", stats.quanta());
        statsJson.addProperty("arcana", stats.arcana());
        json.add("stats", statsJson);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        stats = InfusionStats.read(buffer);
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        stats.write(buffer);
    }

    public static class Builder extends ProcessingRecipeBuilder<InfusingRecipe> {
        private final InfusionStats stats;

        public Builder(ResourceLocation recipeId, InfusionStats stats) {
            super(InfusingRecipe::new, recipeId);
            this.stats = stats;
        }

        @Override
        public InfusingRecipe build() {
            InfusingRecipe recipe = super.build();
            recipe.stats = stats;
            return recipe;
        }
    }

    public static class Serializer<R extends InfusingRecipe> extends ProcessingRecipeSerializer<R> {
        public Serializer(ProcessingRecipeBuilder.ProcessingRecipeFactory<R> factory) {
            super(factory);
        }
    }

    public static InfusingRecipe createDisplayRecipe(EnchantingRecipe recipe) {
        var requirements = recipe.getRequirements();
        InfusionStats stats = new InfusionStats(requirements.eterna(), requirements.quanta(), requirements.arcana());
        return new Builder(recipe.getId(), stats)
                .withItemIngredients(recipe.getInput())
                .withSingleItemOutput(recipe.getOutput())
                .build();
    }
}
