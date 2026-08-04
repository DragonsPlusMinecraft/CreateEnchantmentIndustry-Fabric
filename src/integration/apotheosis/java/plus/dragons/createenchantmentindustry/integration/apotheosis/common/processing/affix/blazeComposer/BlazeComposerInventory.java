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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common.processing.affix.blazeComposer;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.processing.affix.blazeComposer.template.AffixTemplateOps;

/** Four-slot snapshot-aware inventory exposed through Fabric Transfer API. */
public class BlazeComposerInventory extends ItemStackHandler {
    private final BlazeComposerBlockEntity composer;
    private boolean suppressCallbacks;
    private AffixTemplateOps.Result result = AffixTemplateOps.Result.emptyInput();

    public BlazeComposerInventory(BlazeComposerBlockEntity composer) {
        super(4);
        this.composer = composer;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemVariant resource, int count) {
        return slot >= 0 && slot < 2 && !hasRemainingOutput();
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if (slot > 1 || stack.isEmpty() || hasRemainingOutput())
            return stack;
        try (Transaction transaction = Transaction.openOuter()) {
            ItemStack remainder = insertItem(slot, stack, transaction);
            if (!simulate)
                transaction.commit();
            return remainder;
        }
    }

    public ItemStack insertItem(int slot, ItemStack stack, TransactionContext transaction) {
        validateSlotIndex(slot);
        if (slot > 1 || stack.isEmpty() || hasRemainingOutput())
            return stack;
        long inserted = getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), transaction);
        ItemStack remainder = stack.copy();
        remainder.shrink(Math.toIntExact(inserted));
        return remainder;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        if (amount <= 0)
            return ItemStack.EMPTY;
        ItemStack stored = getStackInSlot(slot);
        if (stored.isEmpty())
            return ItemStack.EMPTY;
        try (Transaction transaction = Transaction.openOuter()) {
            ItemStack result = extractItem(slot, amount, transaction);
            if (!simulate)
                transaction.commit();
            return result;
        }
    }

    public ItemStack extractItem(int slot, int amount, TransactionContext transaction) {
        validateSlotIndex(slot);
        if (amount <= 0)
            return ItemStack.EMPTY;
        ItemStack stored = getStackInSlot(slot);
        if (stored.isEmpty())
            return ItemStack.EMPTY;
        long extracted = getSlot(slot).extract(ItemVariant.of(stored), amount, transaction);
        ItemStack result = stored.copy();
        result.setCount(Math.toIntExact(extracted));
        return result;
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlotCount())
            throw new IndexOutOfBoundsException(
                    "Slot " + slot + " not in valid range [0," + getSlotCount() + ")");
    }

    private void setInternal(int slot, ItemStack stack) {
        suppressCallbacks = true;
        try {
            setStackInSlot(slot, stack);
        } finally {
            suppressCallbacks = false;
        }
    }

    @Override
    protected void onLoad() {
        var level = composer.getLevel();
        if (level != null && !level.isClientSide)
            updateResult();
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (suppressCallbacks)
            return;
        if (slot == 0 || slot == 1) {
            composer.onInputChanged();
            updateResult();
        }
        composer.notifyUpdate();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        updateResult();
    }

    public int getEssenceCost() {
        return result.cost();
    }

    public AffixTemplateOps.Result getLastResult() {
        return result;
    }

    public boolean hasRemainingOutput() {
        return !getStackInSlot(2).isEmpty() || !getStackInSlot(3).isEmpty();
    }

    public void clearInput() {
        setInternal(0, ItemStack.EMPTY);
        setInternal(1, ItemStack.EMPTY);
        result = AffixTemplateOps.Result.emptyInput();
    }

    public void clear() {
        for (int i = 0; i < getSlotCount(); i++)
            setInternal(i, ItemStack.EMPTY);
        result = AffixTemplateOps.Result.emptyInput();
    }

    public AffixTemplateOps.Result getProcessingResult() {
        return AffixTemplateOps.compose(
                composer.getMode(),
                composer.isSuper(),
                composer.getBlockedSuperPenalty(),
                getStackInSlot(0),
                getStackInSlot(1));
    }

    public void applyResult(ItemStack primaryOutput, ItemStack secondaryOutput) {
        if (primaryOutput.isEmpty() && secondaryOutput.isEmpty())
            return;
        setInternal(2, primaryOutput.copy());
        setInternal(3, secondaryOutput.copy());
        clearInput();
        updateResult();
    }

    public void updateResult() {
        result = AffixTemplateOps.compose(
                composer.getMode(),
                composer.isSuper(),
                0,
                composer.getBlockedSuperPreviewMinPenalty(),
                composer.getBlockedSuperPreviewMaxPenalty(),
                getStackInSlot(0),
                getStackInSlot(1));
    }
}
