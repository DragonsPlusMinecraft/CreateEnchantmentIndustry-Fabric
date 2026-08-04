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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.mixin.zenith;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.AnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;

/**
 * Supplies the method inherited by Zenith's ClassTinkerers enum subclasses when Fabric-ASM fails to
 * emit their delegate in a named development environment.
 *
 * <p>Vanilla categories and correctly generated Zenith subclasses override this method, so the
 * fallback only handles malformed dynamic subclasses.
 */
@Restriction(require = @Condition(ModIntegration.Constants.APOTHIC_ENCHANTING))
@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin {
    /**
     * @author DragonsPlus
     * @reason Keep Zenith's eight dynamically added categories usable when their generated
     *         subclasses omit the abstract method implementation.
     */
    @Overwrite
    public boolean canEnchant(Item item) {
        String category = ((EnchantmentCategory) (Object) this).name();
        return switch (category) {
            case "ANVIL" -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof AnvilBlock;
            case "AXE" -> item instanceof AxeItem;
            case "CORE_ARMOR" -> EnchantmentCategory.ARMOR.canEnchant(item)
                    || EnchantmentCategory.WEARABLE.canEnchant(item);
            case "HOE" -> item instanceof HoeItem;
            case "PICKAXE" -> item instanceof PickaxeItem;
            case "SHEARS" -> item instanceof ShearsItem;
            case "SHIELD" -> item instanceof ShieldItem;
            case "NULL" -> false;
            default -> false;
        };
    }
}
