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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/** Lightmap data attached to a face while a JSON element model is being baked. */
public interface ModelLightData {
    boolean cei$hasModelLight();

    int cei$getBlockLight();

    int cei$getSkyLight();

    void cei$setModelLight(int blockLight, int skyLight);

    static @Nullable Values read(JsonObject owner) {
        JsonObject data = findData(owner, "forge_data");
        if (data == null)
            data = findData(owner, "neoforge_data");
        if (data == null)
            return null;

        int blockLight = GsonHelper.getAsInt(data, "block_light", 0);
        int skyLight = GsonHelper.getAsInt(data, "sky_light", 0);
        validate("block_light", blockLight);
        validate("sky_light", skyLight);
        return new Values(blockLight, skyLight);
    }

    private static @Nullable JsonObject findData(JsonObject owner, String key) {
        JsonElement element = owner.get(key);
        if (element == null || element.isJsonNull())
            return null;
        if (!element.isJsonObject())
            throw new JsonParseException("Expected " + key + " to be an object");

        JsonObject data = element.getAsJsonObject();
        return data.has("block_light") || data.has("sky_light") ? data : null;
    }

    private static void validate(String key, int value) {
        if (value < 0 || value > 15)
            throw new JsonParseException(key + " must be between 0 and 15, found " + value);
    }

    record Values(int blockLight, int skyLight) {}
}
