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

package plus.dragons.createenchantmentindustry.integration.apotheosis.common.registry;

import static plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon.REGISTRATE;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import plus.dragons.createdragonsplus.common.fluids.TypedFlowableFluid;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.integration.apotheosis.common.fluids.EssenceFluidType;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon;

public class CEIAXFluids {
    public static final ModTags MOD_TAGS = new ModTags();
    private static final ResourceLocation APOTHEOTIC_ESSENCE_ID = CEICommon.asResource("apotheotic_essence");
    private static final ResourceLocation APOTHEOTIC_ESSENCE_STILL = CEICommon.asResource(
            "fluid/apotheotic_essence_still");
    private static final ResourceLocation APOTHEOTIC_ESSENCE_FLOW = CEICommon.asResource(
            "fluid/apotheotic_essence_flow");
    private static final ResourceLocation CRYSTAL_ESSENCE_ID = CEICommon.asResource("crystal_essence");
    private static final ResourceLocation CRYSTAL_ESSENCE_STILL = CEICommon.asResource("fluid/crystal_essence_still");
    private static final ResourceLocation CRYSTAL_ESSENCE_FLOW = CEICommon.asResource("fluid/crystal_essence_flow");
    public static final EssenceFluidType APOTHEOTIC_ESSENCE_TYPE = EssenceFluidType.create(
            APOTHEOTIC_ESSENCE_ID,
            APOTHEOTIC_ESSENCE_STILL,
            APOTHEOTIC_ESSENCE_FLOW,
            0xF56F22,
            Rarity.EPIC,
            15);
    public static final EssenceFluidType CRYSTAL_ESSENCE_TYPE = EssenceFluidType.create(
            CRYSTAL_ESSENCE_ID,
            CRYSTAL_ESSENCE_STILL,
            CRYSTAL_ESSENCE_FLOW,
            0x8778FA,
            Rarity.RARE,
            8);

    public static final FluidEntry<TypedFlowableFluid.Flowing> APOTHEOTIC_ESSENCE = REGISTRATE
            .fluid(
                    "apotheotic_essence",
                    APOTHEOTIC_ESSENCE_STILL,
                    APOTHEOTIC_ESSENCE_FLOW,
                    properties -> new TypedFlowableFluid.Flowing(properties, APOTHEOTIC_ESSENCE_TYPE))
            .fluidAttributes(() -> APOTHEOTIC_ESSENCE_TYPE)
            .fluidProperties(p -> p.levelDecreasePerBlock(2).blastResistance(100f))
            .source(properties -> new TypedFlowableFluid.Source(properties, APOTHEOTIC_ESSENCE_TYPE))
            .block()
            .properties(properties -> properties
                    .lightLevel((b) -> 15))
            .build()
            .bucket()
            .properties(properties -> properties
                    .rarity(Rarity.EPIC))
            .build()
            .register();
    public static final FluidEntry<TypedFlowableFluid.Flowing> APOTHEOTIC_ESSENCE_FLOWING = APOTHEOTIC_ESSENCE;

    public static final FluidEntry<TypedFlowableFluid.Flowing> CRYSTAL_ESSENCE = REGISTRATE
            .fluid(
                    "crystal_essence",
                    CRYSTAL_ESSENCE_STILL,
                    CRYSTAL_ESSENCE_FLOW,
                    properties -> new TypedFlowableFluid.Flowing(properties, CRYSTAL_ESSENCE_TYPE))
            .fluidAttributes(() -> CRYSTAL_ESSENCE_TYPE)
            .fluidProperties(p -> p.levelDecreasePerBlock(2).blastResistance(100f))
            .source(properties -> new TypedFlowableFluid.Source(properties, CRYSTAL_ESSENCE_TYPE))
            .block()
            .properties(properties -> properties
                    .lightLevel((b) -> 8))
            .build()
            .bucket()
            .properties(properties -> properties
                    .rarity(Rarity.RARE))
            .build()
            .register();
    public static final FluidEntry<TypedFlowableFluid.Flowing> CRYSTAL_ESSENCE_FLOWING = CRYSTAL_ESSENCE;

    public static void register() {
        REGISTRATE.registerFluidTags(MOD_TAGS);
        Registry.register(PortingLibFluids.FLUID_TYPES, APOTHEOTIC_ESSENCE_ID, APOTHEOTIC_ESSENCE_TYPE);
        Registry.register(PortingLibFluids.FLUID_TYPES, CRYSTAL_ESSENCE_ID, CRYSTAL_ESSENCE_TYPE);
    }

    public static void initialize() {}

    public static class ModTags extends IntrinsicTagRegistry<Fluid, RegistrateTagsProvider.IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> fanSalvagingCatalysts = tag("fan_processing_catalysts/salvaging", "Bulk Salvaging Catalysts");

        public ModTags() {
            super(CEIACommon.ID, Registries.FLUID);
        }

        @Override
        public void generate(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
            super.generate(provider);
            provider.addTag(fanSalvagingCatalysts)
                    .addOptional(CEICommon.asResource("infused_dragon_breath"));
            provider.addTag(AllTags.AllFluidTags.BOTTOMLESS_DENY.tag)
                    .addOptional(CEICommon.asResource("apotheotic_essence"))
                    .addOptional(CEICommon.asResource("flowing_apotheotic_essence"))
                    .addOptional(CEICommon.asResource("crystal_essence"))
                    .addOptional(CEICommon.asResource("flowing_crystal_essence"));
        }
    }
}
