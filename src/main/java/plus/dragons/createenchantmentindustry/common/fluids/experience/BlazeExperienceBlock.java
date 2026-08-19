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

package plus.dragons.createenchantmentindustry.common.fluids.experience;

import com.simibubi.create.AllItems;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlock;

public abstract class BlazeExperienceBlock<T extends BlazeExperienceBlockEntity> extends BlazeBlock<T> {
    public BlazeExperienceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        T blockEntity = getBlockEntity(level, pos);
        if (blockEntity == null)
            return InteractionResult.PASS;
        boolean notConsume = player.getAbilities().instabuild;
        boolean forceOverflow = !(player instanceof FakePlayer);
        var resultHolder = applyFuel(state, level, pos, stack, forceOverflow, notConsume, false);
        var result = resultHolder.getResult();
        if (result == InteractionResult.PASS)
            return InteractionResult.PASS;
        if (result == InteractionResult.FAIL)
            return InteractionResult.FAIL;
        var remainder = resultHolder.getObject();
        if (!remainder.isEmpty()) {
            if (stack.isEmpty())
                player.setItemInHand(hand, remainder);
            else
                player.getInventory().placeItemBackInInventory(remainder);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResultHolder<ItemStack> applyFuel(BlockState state, Level level, BlockPos pos, ItemStack stack, boolean forceOverflow, boolean notConsume, boolean simulate) {
        if (!state.hasBlockEntity())
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlazeExperienceBlockEntity blaze))
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        if (stack.is(AllItems.CREATIVE_BLAZE_CAKE.get())) {
            if (!simulate)
                blaze.applyCreativeFuel();
            if (!notConsume)
                stack.shrink(1);
            return InteractionResultHolder.success(ItemStack.EMPTY);
        }
        var fuel = ExperienceFuel.get(level, stack);
        if (fuel != null) {
            boolean applied = blaze.applyExperienceFuel(fuel, forceOverflow, simulate);
            if (applied) {
                ItemStack remainder = ItemStack.EMPTY;
                if (!notConsume) {
                    remainder = fuel.usingConvertTo().map(ItemStack::copy).orElseGet(() -> {
                        var craftingRemainder = stack.getItem().getCraftingRemainingItem();
                        return craftingRemainder == null ? ItemStack.EMPTY : craftingRemainder.getDefaultInstance();
                    });
                    stack.shrink(1);
                }
                return InteractionResultHolder.success(remainder);
            }
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        }
        return InteractionResultHolder.pass(ItemStack.EMPTY);
    }

    /**
     * Tries to insert one fuel item from a mechanical arm without leaking side effects out of the arm transaction.
     * A {@code null} return means that the stack is not fuel and may be offered to the machine inventory instead.
     */
    public static @Nullable ItemStack applyFuel(
            BlockState state,
            Level level,
            BlockPos pos,
            ItemStack stack,
            TransactionContext transaction) {
        ItemStack simulatedInput = stack.copy();
        InteractionResultHolder<ItemStack> simulated = applyFuel(
                state, level, pos, simulatedInput, false, false, true);
        if (simulated.getResult() == InteractionResult.PASS)
            return null;
        if (!simulated.getResult().consumesAction())
            return stack;

        ItemStack fuel = stack.copy();
        boolean dropContainerOnCommit = !simulatedInput.isEmpty();
        new SnapshotParticipant<Boolean>() {
            @Override
            protected Boolean createSnapshot() {
                return Boolean.FALSE;
            }

            @Override
            protected void readSnapshot(Boolean snapshot) {}

            @Override
            protected void onFinalCommit() {
                InteractionResultHolder<ItemStack> applied = applyFuel(
                        level.getBlockState(pos), level, pos, fuel, false, false, false);
                if (dropContainerOnCommit && applied.getResult().consumesAction()) {
                    ItemStack container = applied.getObject();
                    if (!container.isEmpty())
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), container);
                }
            }
        }.updateSnapshots(transaction);

        return simulatedInput.isEmpty() ? simulated.getObject() : simulatedInput;
    }
}
