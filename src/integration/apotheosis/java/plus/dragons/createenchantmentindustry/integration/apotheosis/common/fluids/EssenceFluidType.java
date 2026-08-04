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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common.fluids;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.createmod.catnip.theme.Color;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.joml.Vector3f;
import plus.dragons.createdragonsplus.common.fluids.SolidRenderFluidType;

/** Fabric fluid attributes and rendering data for the two Zenith essence fluids. */
public final class EssenceFluidType extends SolidRenderFluidType {
    private EssenceFluidType(
            Properties properties,
            ResourceLocation stillTexture,
            ResourceLocation flowingTexture,
            int tintColor,
            Vector3f fogColor) {
        super(properties, stillTexture, flowingTexture, tintColor, fogColor, () -> 1.0F);
    }

    public static EssenceFluidType create(
            ResourceLocation id,
            ResourceLocation stillTexture,
            ResourceLocation flowingTexture,
            int color,
            Rarity rarity,
            int lightLevel) {
        FluidType.Properties properties = FluidType.Properties.create()
                .descriptionId(Util.makeDescriptionId("fluid", id))
                .rarity(rarity)
                .density(3000)
                .viscosity(6000)
                .lightLevel(lightLevel)
                .canSwim(false)
                .canDrown(false)
                .pathType(BlockPathTypes.BLOCKED)
                .adjacentPathType(BlockPathTypes.BLOCKED)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA);
        return new EssenceFluidType(
                properties,
                stillTexture,
                flowingTexture,
                0xFFFFFFFF,
                new Color(color, false).asVectorF());
    }
}
