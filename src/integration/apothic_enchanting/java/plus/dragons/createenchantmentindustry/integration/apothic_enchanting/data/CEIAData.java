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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.data;

import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.client.ponder.CEIAPonderPlugin;

public final class CEIAData {
    private CEIAData() {}

    public static void initialize() {
        if (PonderIndex.streamPlugins().noneMatch(plugin -> plugin instanceof CEIAPonderPlugin))
            PonderIndex.addPlugin(new CEIAPonderPlugin());
    }

    public static void registerProviders(FabricDataGenerator.Pack pack) {
        pack.addProvider(CEIARecipeProvider::new);
        pack.addProvider(CEIAConditionalLootTableProvider::new);
    }
}
