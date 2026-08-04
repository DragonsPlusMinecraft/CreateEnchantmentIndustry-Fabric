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

package plus.dragons.createenchantmentindustry.common.registry;

import static plus.dragons.createenchantmentindustry.common.CEICommon.REGISTRATE;

import com.simibubi.create.AllTags.AllFluidTags;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.DispenserBlock;
import plus.dragons.createdragonsplus.common.fluids.StandardDispenserBehaviour;
import plus.dragons.createdragonsplus.common.fluids.TypedFlowableFluid;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.common.fluids.experience.ExperienceEffectHandler;
import plus.dragons.createenchantmentindustry.common.fluids.experience.ExperienceFluidType;
import plus.dragons.createenchantmentindustry.common.item.FoilBucketItem;

public final class CEIFluids {
    private static final TagKey<Item> COMMON_BUCKETS = TagKey.create(
            Registries.ITEM, new ResourceLocation("c", "buckets"));
    private static final ResourceLocation EXPERIENCE_STILL = CEICommon.asResource("fluid/experience_still");
    private static final ResourceLocation EXPERIENCE_FLOW = CEICommon.asResource("fluid/experience_flow");
    public static final ExperienceFluidType EXPERIENCE_TYPE = ExperienceFluidType.create(
            EXPERIENCE_STILL, EXPERIENCE_FLOW);

    public static final FluidEntry<TypedFlowableFluid.Flowing> EXPERIENCE = REGISTRATE
            .fluid(
                    "experience",
                    EXPERIENCE_STILL,
                    EXPERIENCE_FLOW,
                    properties -> new TypedFlowableFluid.Flowing(properties, EXPERIENCE_TYPE))
            .lang("Liquid Experience")
            .fluidAttributes(() -> EXPERIENCE_TYPE)
            .fluidProperties(properties -> properties.blastResistance(100F))
            .tag(AllFluidTags.BOTTOMLESS_DENY.tag)
            .source(properties -> new TypedFlowableFluid.Source(properties, EXPERIENCE_TYPE))
            .block()
            .properties(properties -> properties.lightLevel(state -> 15))
            .lang("Liquid Experience")
            .build()
            .bucket(FoilBucketItem::new)
            .lang("Bucket o' Enchanting")
            .properties(properties -> properties.rarity(Rarity.UNCOMMON))
            .tag(COMMON_BUCKETS)
            .build()
            .register();
    /** Kept as a source-compatible alias for integrations that need the flowing variant. */
    public static final FluidEntry<TypedFlowableFluid.Flowing> EXPERIENCE_FLOWING = EXPERIENCE;
    private static boolean registered;

    private CEIFluids() {}

    public static void register() {
        if (registered)
            return;
        registered = true;
        Registry.register(PortingLibFluids.FLUID_TYPES, CEICommon.asResource("experience"), EXPERIENCE_TYPE);
    }

    public static void initialize() {
        OpenPipeEffectHandler.REGISTRY.register(EXPERIENCE.getSource(), new ExperienceEffectHandler());
        DispenserBlock.registerBehavior(EXPERIENCE.getBucket().get(), StandardDispenserBehaviour.INSTANCE);
    }
}
