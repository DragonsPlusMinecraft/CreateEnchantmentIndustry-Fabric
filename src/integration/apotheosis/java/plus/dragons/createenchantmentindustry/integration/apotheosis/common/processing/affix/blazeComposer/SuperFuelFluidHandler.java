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

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry.CEIAXFluids;

/** Routes normal fuel first on insertion and super fuel first on extraction. */
final class SuperFuelFluidHandler implements Storage<FluidVariant> {
    private final Supplier<SmartFluidTank> normalTank;
    private final Supplier<SmartFluidTank> superTank;
    private final BooleanSupplier canFillSuperTank;

    SuperFuelFluidHandler(
            Supplier<SmartFluidTank> normalTank,
            Supplier<SmartFluidTank> superTank,
            BooleanSupplier canFillSuperTank) {
        this.normalTank = normalTank;
        this.superTank = superTank;
        this.canFillSuperTank = canFillSuperTank;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank()
                || resource.getFluid() != CEIAXFluids.APOTHEOTIC_ESSENCE.getSource()
                || maxAmount <= 0)
            return 0;
        long inserted = normalTank.get().insert(resource, maxAmount, transaction);
        long remaining = maxAmount - inserted;
        if (remaining > 0 && canFillSuperTank.getAsBoolean())
            inserted += superTank.get().insert(resource, remaining, transaction);
        return inserted;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank()
                || resource.getFluid() != CEIAXFluids.APOTHEOTIC_ESSENCE.getSource()
                || maxAmount <= 0)
            return 0;
        long extracted = superTank.get().extract(resource, maxAmount, transaction);
        long remaining = maxAmount - extracted;
        if (remaining > 0)
            extracted += normalTank.get().extract(resource, remaining, transaction);
        return extracted;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        List<StorageView<FluidVariant>> views = new ArrayList<>(2);
        normalTank.get().forEach(views::add);
        superTank.get().forEach(views::add);
        return views.iterator();
    }
}
