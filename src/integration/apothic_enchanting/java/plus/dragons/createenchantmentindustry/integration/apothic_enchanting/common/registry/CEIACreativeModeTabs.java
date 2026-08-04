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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry;

import static plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry.CEIABlocks.*;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.util.CEILang;

public class CEIACreativeModeTabs {
    public static CreativeModeTab APOTHEOTIC;

    public static void register() {
        if (APOTHEOTIC != null)
            return;
        ResourceLocation id = CEICommon.asResource("apotheotic");
        APOTHEOTIC = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, base(id));
    }

    private static CreativeModeTab base(ResourceLocation id) {
        return FabricItemGroup.builder()
                .title(CEILang.description("itemGroup", id).component())
                .icon(BRASS_BOOKSHELF::asStack)
                .displayItems(CEIACreativeModeTabs::buildBaseContents)
                .build();
    }

    private static void buildBaseContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        if (!Apotheosis.enableEnch)
            return;
        output.accept(INFUSER);
        output.accept(BRASS_BOOKSHELF);
        output.accept(CREATIVE_BOOKSHELF);
        output.accept(ENDER_WOVEN_BAG);
        output.accept(CEIAFluids.INFUSED_DRAGON_BREATH.getBucket().get());
    }
}
