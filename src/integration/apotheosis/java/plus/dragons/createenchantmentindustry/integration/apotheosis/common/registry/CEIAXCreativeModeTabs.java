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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import plus.dragons.createenchantmentindustry.common.CEICommon;

public class CEIAXCreativeModeTabs {
    private static boolean registered;

    public static void register() {
        if (registered)
            return;
        registered = true;
        var key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, CEICommon.asResource("apotheotic"));
        ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
            if (!Apotheosis.enableAdventure)
                return;
            entries.accept(CEIAXBlocks.GEM_CUTTER.get());
            entries.accept(CEIAXBlocks.AFFIX_AUGMENTOR.get());
            entries.accept(CEIAXBlocks.BLAZE_COMPOSER.get());
            entries.accept(CEIAXItems.BRASS_AFFIX_TEMPLATE.get());
            entries.accept(CEIAXItems.CRYSTAL_AFFIX_TEMPLATE.get());
            entries.accept(CEIAXItems.APOTHEOTIC_AFFIX_TEMPLATE.get());
            entries.accept(CEIAXFluids.APOTHEOTIC_ESSENCE.getBucket().get());
            entries.accept(CEIAXFluids.CRYSTAL_ESSENCE.getBucket().get());
        });
    }
}
