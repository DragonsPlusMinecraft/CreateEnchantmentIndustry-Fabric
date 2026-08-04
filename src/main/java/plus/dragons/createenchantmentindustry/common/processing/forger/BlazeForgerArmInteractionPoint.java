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

package plus.dragons.createenchantmentindustry.common.processing.forger;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createenchantmentindustry.common.fluids.experience.BlazeExperienceBlock;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlocks;

public class BlazeForgerArmInteractionPoint extends ArmInteractionPoint {
    public BlazeForgerArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ItemStack stack, TransactionContext transaction) {
        if (!(level.getBlockEntity(pos) instanceof BlazeForgerBlockEntity forger))
            return stack;
        ItemStack input = stack.copy();
        ItemStack fuelRemainder = BlazeExperienceBlock.applyFuel(cachedState, level, pos, input, transaction);
        return fuelRemainder != null
                ? fuelRemainder
                : forger.insertAutomationItem(input, transaction);
    }

    @Override
    public ItemStack extract(int amount, TransactionContext transaction) {
        if (level.getBlockEntity(pos) instanceof BlazeForgerBlockEntity forger) {
            return forger.extractAutomationItem(amount, transaction);
        }
        return ItemStack.EMPTY;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CEIBlocks.BLAZE_FORGER.has(state);
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BlazeForgerArmInteractionPoint(this, level, pos, state);
        }
    }
}
