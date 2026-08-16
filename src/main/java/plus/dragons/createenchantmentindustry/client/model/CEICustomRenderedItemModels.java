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

package plus.dragons.createenchantmentindustry.client.model;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import java.util.Set;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createenchantmentindustry.common.CEICommon;

public final class CEICustomRenderedItemModels {
    private static final Set<ResourceLocation> MODELS = Set.of(
            itemModel("blaze_enchanter"),
            itemModel("blaze_forger"),
            itemModel("classic_blaze_enchanter"),
            itemModel("blaze_composer"));

    private CEICustomRenderedItemModels() {}

    public static void register() {
        ModelLoadingPlugin.register(pluginContext -> pluginContext
                .modifyModelAfterBake()
                .register(ModelModifier.WRAP_PHASE, (model, context) -> {
                    if (!MODELS.contains(context.id())
                            || model == null
                            || model.getClass() != CustomRenderedItemModel.class)
                        return model;
                    CustomRenderedItemModel customModel = (CustomRenderedItemModel) model;
                    return new CEICustomRenderedItemModel(customModel.getOriginalModel());
                }));
    }

    private static ModelResourceLocation itemModel(String path) {
        return new ModelResourceLocation(CEICommon.asResource(path), "inventory");
    }

    /** Allows vanilla to reach the registered dynamic renderer before FRAPI handles the wrapped model. */
    private static final class CEICustomRenderedItemModel extends CustomRenderedItemModel {
        private CEICustomRenderedItemModel(BakedModel originalModel) {
            super(originalModel);
        }

        @Override
        public boolean isVanillaAdapter() {
            return true;
        }
    }
}
