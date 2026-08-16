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

package plus.dragons.createenchantmentindustry.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createenchantmentindustry.common.CEICommon;

public final class FabricPartialItemRenderer {
    private static final Map<BakedModel, BakedModel> MODELS_WITHOUT_TRANSFORMS = Collections.synchronizedMap(new IdentityHashMap<>());

    private FabricPartialItemRenderer() {}

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return CEICommon.asResource("partial_item_renderer");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        MODELS_WITHOUT_TRANSFORMS.clear();
                    }
                });
    }

    public static void render(
            ItemStack stack,
            BakedModel model,
            ItemDisplayContext transformType,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            int overlay) {
        Minecraft.getInstance()
                .getItemRenderer()
                .render(
                        stack,
                        transformType,
                        false,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        MODELS_WITHOUT_TRANSFORMS.computeIfAbsent(model, ModelWithoutTransforms::new));
    }

    private static final class ModelWithoutTransforms extends ForwardingBakedModel {
        private ModelWithoutTransforms(BakedModel model) {
            wrapped = model;
        }

        @Override
        public ItemTransforms getTransforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }
    }
}
