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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createenchantmentindustry.client.model.ModelLightData;

@Mixin(BlockElement.Deserializer.class)
public abstract class BlockElementLightDeserializerMixin {
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElement;", at = @At("RETURN"))
    private void cei$readElementLight(
            JsonElement json,
            Type type,
            JsonDeserializationContext context,
            CallbackInfoReturnable<BlockElement> cir) {
        ModelLightData.Values values = ModelLightData.read(json.getAsJsonObject());
        if (values == null)
            return;

        for (var face : cir.getReturnValue().faces.values()) {
            ModelLightData lightData = (ModelLightData) (Object) face;
            if (!lightData.cei$hasModelLight())
                lightData.cei$setModelLight(values.blockLight(), values.skyLight());
        }
    }
}
