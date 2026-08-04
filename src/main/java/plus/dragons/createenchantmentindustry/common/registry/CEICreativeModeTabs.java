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

package plus.dragons.createenchantmentindustry.common.registry;

import static com.simibubi.create.AllBlocks.EXPERIENCE_BLOCK;
import static com.simibubi.create.AllItems.EXP_NUGGET;
import static plus.dragons.createdragonsplus.common.registry.CDPItems.BLAZE_UPGRADE_SMITHING_TEMPLATE;
import static plus.dragons.createenchantmentindustry.common.registry.CEIBlocks.*;
import static plus.dragons.createenchantmentindustry.common.registry.CEIItems.*;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.config.CEIConfig;
import plus.dragons.createenchantmentindustry.util.CEILang;

public class CEICreativeModeTabs {
    public static CreativeModeTab BASE;

    public static void register() {
        if (BASE != null)
            return;
        ResourceLocation id = CEICommon.asResource("base");
        BASE = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, base(id));
    }

    private static CreativeModeTab base(ResourceLocation id) {
        return FabricItemGroup.builder()
                .title(CEILang.description("itemGroup", id).component())
                .icon(BLAZE_ENCHANTER::asStack)
                .displayItems(CEICreativeModeTabs::buildBaseContents)
                .build();
    }

    private static void buildBaseContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(MECHANICAL_GRINDSTONE);
        output.accept(EXPERIENCE_HATCH);
        output.accept(EXPERIENCE_LANTERN);
        output.accept(PRINTER);
        output.accept(BLAZE_ENCHANTER);
        output.accept(BLAZE_FORGER);
        if (CEIConfig.features().classicBlazeEnchanter.get())
            output.accept(CLASSIC_BLAZE_ENCHANTER);
        output.accept(EXPERIENCE_BLOCK);
        output.accept(SUPER_EXPERIENCE_BLOCK);
        output.accept(EXP_NUGGET);
        output.accept(SUPER_EXPERIENCE_NUGGET);
        output.accept(ENCHANTING_TEMPLATE);
        output.accept(SUPER_ENCHANTING_TEMPLATE);
        output.accept(BLAZE_UPGRADE_SMITHING_TEMPLATE);
        if (CEIConfig.features().classicBlazeEnchanter.get())
            output.accept(BLAZES_ENCHANTING_HANDBOOK);
        output.accept(EXPERIENCE_CAKE_BASE, TabVisibility.SEARCH_TAB_ONLY);
        output.accept(EXPERIENCE_CAKE);
        output.accept(EXPERIENCE_CAKE_SLICE);
        output.accept(EXPERIENCE_BUCKET.get());
    }
}
