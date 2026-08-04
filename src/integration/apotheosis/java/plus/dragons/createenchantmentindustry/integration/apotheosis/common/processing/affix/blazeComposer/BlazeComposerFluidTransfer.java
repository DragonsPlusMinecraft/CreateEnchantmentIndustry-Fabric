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

import io.github.fabricators_of_create.porting_lib.transfer.MutableContainerItemContext;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Transactional item-container interaction for the Blaze Composer's fuel tanks. */
final class BlazeComposerFluidTransfer {
    static boolean hasFluidStorage(ItemStack stack) {
        return getItemStorage(stack) != null;
    }

    static TransferResult transfer(ItemStack stack, Storage<FluidVariant> tank, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            TransferResult result = transfer(stack, tank, transaction);
            if (!simulate && !result.isEmpty())
                transaction.commit();
            return result;
        }
    }

    static TransferResult transfer(
            ItemStack stack, Storage<FluidVariant> tank, TransactionContext transaction) {
        ItemFluidStorage item = getItemStorage(stack);
        if (item == null)
            return TransferResult.EMPTY;

        TransferResult drained = move(item, tank, true, transaction);
        if (!drained.isEmpty())
            return drained;
        return move(item, tank, false, transaction);
    }

    private static TransferResult move(
            ItemFluidStorage item,
            Storage<FluidVariant> tank,
            boolean itemToTank,
            TransactionContext transaction) {
        Storage<FluidVariant> source = itemToTank ? item.storage() : tank;
        Storage<FluidVariant> target = itemToTank ? tank : item.storage();
        for (var view : source.nonEmptyViews()) {
            FluidVariant variant = view.getResource();
            if (variant.isBlank())
                continue;
            long moved = StorageUtil.move(source, target, variant::equals, view.getAmount(), transaction);
            if (moved <= 0)
                continue;
            return new TransferResult(getResult(item.context()), moved);
        }
        return TransferResult.EMPTY;
    }

    private static @Nullable ItemFluidStorage getItemStorage(ItemStack stack) {
        if (stack.isEmpty())
            return null;
        ItemStack single = stack.copy();
        single.setCount(1);
        MutableContainerItemContext context = new MutableContainerItemContext(single);
        Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
        return storage == null ? null : new ItemFluidStorage(context, storage);
    }

    private static ItemStack getResult(MutableContainerItemContext context) {
        return context.getItemVariant().toStack(TransferUtil.truncateLong(context.getAmount()));
    }

    private record ItemFluidStorage(
            MutableContainerItemContext context, Storage<FluidVariant> storage) {}

    record TransferResult(ItemStack container, long moved) {
        static final TransferResult EMPTY = new TransferResult(ItemStack.EMPTY, 0);

        boolean isEmpty() {
            return moved <= 0;
        }
    }

    private BlazeComposerFluidTransfer() {}
}
