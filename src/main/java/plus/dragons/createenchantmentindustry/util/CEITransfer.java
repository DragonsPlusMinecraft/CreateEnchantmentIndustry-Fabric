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

package plus.dragons.createenchantmentindustry.util;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.level.material.Fluid;

/** Transaction-safe helpers for CEI fluid operations. */
public final class CEITransfer {
    private CEITransfer() {}

    public static long insert(Storage<FluidVariant> storage, FluidStack stack, boolean simulate) {
        if (storage == null || stack.isEmpty())
            return 0;
        return insert(storage, stack.getType(), stack.getAmount(), simulate);
    }

    static <T> long insert(Storage<T> storage, T resource, long amount, boolean simulate) {
        if (storage == null || resource == null || amount <= 0)
            return 0;
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(resource, amount, transaction);
            if (!simulate)
                transaction.commit();
            return inserted;
        }
    }

    public static boolean insertExact(Storage<FluidVariant> storage, FluidStack stack, boolean simulate) {
        if (storage == null || stack.isEmpty())
            return false;
        return insertExact(storage, stack.getType(), stack.getAmount(), simulate);
    }

    static <T> boolean insertExact(Storage<T> storage, T resource, long amount, boolean simulate) {
        if (storage == null || resource == null || amount <= 0)
            return false;
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(resource, amount, transaction);
            if (inserted != amount)
                return false;
            if (!simulate)
                transaction.commit();
            return true;
        }
    }

    public static long extract(Storage<FluidVariant> storage, FluidStack stack, boolean simulate) {
        if (storage == null || stack.isEmpty())
            return 0;
        return extract(storage, stack.getType(), stack.getAmount(), simulate);
    }

    static <T> long extract(Storage<T> storage, T resource, long amount, boolean simulate) {
        if (storage == null || resource == null || amount <= 0)
            return 0;
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(resource, amount, transaction);
            if (!simulate)
                transaction.commit();
            return extracted;
        }
    }

    public static boolean extractExact(Storage<FluidVariant> storage, FluidStack stack, boolean simulate) {
        if (storage == null || stack.isEmpty())
            return false;
        return extractExact(storage, stack.getType(), stack.getAmount(), simulate);
    }

    static <T> boolean extractExact(Storage<T> storage, T resource, long amount, boolean simulate) {
        if (storage == null || resource == null || amount <= 0)
            return false;
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(resource, amount, transaction);
            if (extracted != amount)
                return false;
            if (!simulate)
                transaction.commit();
            return true;
        }
    }

    public static long insertMillibuckets(
            Storage<FluidVariant> storage,
            Fluid fluid,
            long amount,
            boolean simulate) {
        return insert(storage, CEIFluidUnits.stack(fluid, amount), simulate);
    }

    public static long extractMillibuckets(
            Storage<FluidVariant> storage,
            Fluid fluid,
            long amount,
            boolean simulate) {
        return extract(storage, CEIFluidUnits.stack(fluid, amount), simulate);
    }
}
