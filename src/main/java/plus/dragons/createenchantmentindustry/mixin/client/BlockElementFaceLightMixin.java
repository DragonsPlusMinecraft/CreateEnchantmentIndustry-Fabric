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

package plus.dragons.createenchantmentindustry.mixin.client;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import plus.dragons.createenchantmentindustry.client.model.ModelLightData;

@Mixin(BlockElementFace.class)
public class BlockElementFaceLightMixin implements ModelLightData {
    @Unique
    private boolean cei$hasModelLight;

    @Unique
    private int cei$blockLight;

    @Unique
    private int cei$skyLight;

    @Override
    public boolean cei$hasModelLight() {
        return cei$hasModelLight;
    }

    @Override
    public int cei$getBlockLight() {
        return cei$blockLight;
    }

    @Override
    public int cei$getSkyLight() {
        return cei$skyLight;
    }

    @Override
    public void cei$setModelLight(int blockLight, int skyLight) {
        cei$hasModelLight = true;
        cei$blockLight = blockLight;
        cei$skyLight = skyLight;
    }
}
