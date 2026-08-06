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

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createenchantmentindustry.client.model.ModelLightData;

@Mixin(FaceBakery.class)
public abstract class FaceBakeryLightMixin {
    @Unique
    private static final int CEI_LIGHTMAP_INDEX = 6;

    @Inject(method = "bakeQuad", at = @At("RETURN"))
    private void cei$applyModelLight(
            Vector3f from,
            Vector3f to,
            BlockElementFace face,
            TextureAtlasSprite sprite,
            Direction facing,
            ModelState transform,
            BlockElementRotation rotation,
            boolean shade,
            ResourceLocation modelLocation,
            CallbackInfoReturnable<BakedQuad> cir) {
        ModelLightData lightData = (ModelLightData) (Object) face;
        if (!lightData.cei$hasModelLight())
            return;

        int[] vertices = cir.getReturnValue().getVertices();
        for (int offset = CEI_LIGHTMAP_INDEX; offset < vertices.length; offset += FaceBakery.VERTEX_INT_SIZE) {
            int current = vertices[offset];
            int blockLight = Math.max(LightTexture.block(current), lightData.cei$getBlockLight());
            int skyLight = Math.max(LightTexture.sky(current), lightData.cei$getSkyLight());
            vertices[offset] = LightTexture.pack(blockLight, skyLight);
        }
    }
}
