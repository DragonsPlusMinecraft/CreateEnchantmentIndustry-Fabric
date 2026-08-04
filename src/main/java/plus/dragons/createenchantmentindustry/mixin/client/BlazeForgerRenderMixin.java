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

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import plus.dragons.createdragonsplus.client.renderer.blockentity.BlazeBlockEntityRenderExtension;
import plus.dragons.createenchantmentindustry.client.model.CEIPartialModels;
import plus.dragons.createenchantmentindustry.common.processing.forger.BlazeForgerBlockEntity;

@Mixin(BlazeForgerBlockEntity.class)
public abstract class BlazeForgerRenderMixin implements BlazeBlockEntityRenderExtension {
    @Override
    public @Nullable PartialModel getHatModel(HeatLevel heatLevel) {
        return heatLevel.isAtLeast(HeatLevel.FADING)
                ? CEIPartialModels.BLAZE_FORGER_HAT
                : CEIPartialModels.BLAZE_FORGER_HAT_SMALL;
    }
}
