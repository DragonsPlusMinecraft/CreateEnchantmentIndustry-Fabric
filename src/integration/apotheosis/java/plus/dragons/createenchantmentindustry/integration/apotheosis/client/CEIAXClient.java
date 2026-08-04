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

package plus.dragons.createenchantmentindustry.integration.apotheosis.client;

import com.simibubi.create.foundation.item.render.CustomRenderedItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import plus.dragons.createdragonsplus.common.fluids.SolidRenderFluidType;
import plus.dragons.createenchantmentindustry.integration.apotheosis.client.ponder.CEIAXPonderPlugin;
import plus.dragons.createenchantmentindustry.integration.apotheosis.client.registry.CEIAXPartialModels;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.processing.affix.blazeComposer.BlazeComposerItemRenderer;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry.CEIAXBlocks;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry.CEIAXFluids;

/** Physical-client bridge for renderers referenced by common-side item classes. */
public final class CEIAXClient {
    private CEIAXClient() {}

    public static void initialize() {
        CEIAXPartialModels.register();
        CEIAXPonderPlugin.register();
        var item = CEIAXBlocks.BLAZE_COMPOSER.asItem();
        BuiltinItemRendererRegistry.INSTANCE.register(item, new BlazeComposerItemRenderer());
        CustomRenderedItems.register(item);
        BlockRenderLayerMap.INSTANCE.putBlock(CEIAXBlocks.BLAZE_COMPOSER.get(), RenderType.cutoutMipped());
        registerFluid(CEIAXFluids.APOTHEOTIC_ESSENCE, CEIAXFluids.APOTHEOTIC_ESSENCE_TYPE);
        registerFluid(CEIAXFluids.CRYSTAL_ESSENCE, CEIAXFluids.CRYSTAL_ESSENCE_TYPE);
    }

    private static void registerFluid(
            com.tterrag.registrate.util.entry.FluidEntry<?> entry, SolidRenderFluidType type) {
        var handler = new SimpleFluidRenderHandler(
                type.getStillTexture(), type.getFlowingTexture(), type.getTintColor());
        FluidRenderHandlerRegistry.INSTANCE.register(entry.getSource(), entry.get(), handler);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), entry.getSource(), entry.get());
    }
}
