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

package plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.registry;

import static plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon.REGISTRATE;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.event.PipeCollisionEvent;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.entry.FluidEntry;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import io.github.fabricators_of_create.porting_lib.fluids.FluidInteractionRegistry;
import io.github.fabricators_of_create.porting_lib.fluids.FluidInteractionRegistry.InteractionInformation;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import plus.dragons.createdragonsplus.common.fluids.StandardDispenserBehaviour;
import plus.dragons.createdragonsplus.common.fluids.TypedFlowableFluid;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonBreathFluidType;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragondBreathLiquidBlock;
import plus.dragons.createdragonsplus.common.fluids.dragonBreath.DragonsBreathOpenPipeEffect;
import plus.dragons.createdragonsplus.data.tag.IntrinsicTagRegistry;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.common.crafting.CEIApotheosisModuleCondition;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;
import plus.dragons.createenchantmentindustry.integration.apothic_enchanting.common.CEIACommon;
import plus.dragons.createenchantmentindustry.util.CEIFluidUnits;

public class CEIAFluids {
    private static final ResourceLocation CDP_DRAGON_BREATH = new ResourceLocation(
            "create_dragons_plus", "dragon_breath");
    private static final TagKey<Fluid> COMMON_DRAGON_BREATH = TagKey.create(
            Registries.FLUID, new ResourceLocation("c", "dragon_breath"));
    private static boolean cdpInteractionsRegistered;
    public static final ModTags MOD_TAGS = new ModTags();
    private static final ResourceLocation INFUSED_DRAGON_BREATH_STILL = REGISTRATE.asResource(
            "fluid/infused_dragon_breath_still");
    private static final ResourceLocation INFUSED_DRAGON_BREATH_FLOW = REGISTRATE.asResource(
            "fluid/infused_dragon_breath_flow");
    public static final DragonBreathFluidType INFUSED_DRAGON_BREATH_TYPE = DragonBreathFluidType.create(
            INFUSED_DRAGON_BREATH_STILL, INFUSED_DRAGON_BREATH_FLOW);
    public static final FluidEntry<TypedFlowableFluid.Flowing> INFUSED_DRAGON_BREATH = REGISTRATE
            .fluid("infused_dragon_breath",
                    INFUSED_DRAGON_BREATH_STILL,
                    INFUSED_DRAGON_BREATH_FLOW,
                    properties -> new TypedFlowableFluid.Flowing(properties, INFUSED_DRAGON_BREATH_TYPE))
            .lang("Infused Dragon's Breath")
            .fluidAttributes(() -> INFUSED_DRAGON_BREATH_TYPE)
            .fluidProperties(properties -> properties
                    .blastResistance(200F)
                    .levelDecreasePerBlock(2)
                    .flowSpeed(2)
                    .tickRate(10))
            .source(properties -> new TypedFlowableFluid.Source(properties, INFUSED_DRAGON_BREATH_TYPE))
            .block(DragondBreathLiquidBlock::new)
            .lang("Infused Dragon's Breath")
            .build()
            .bucket()
            .properties(properties -> properties.rarity(Rarity.EPIC))
            .lang("Infused Dragon's Breath Bucket")
            .build()
            .setData(ProviderType.RECIPE, (ctx, prov) -> {
                new ProcessingRecipeBuilder<>(EmptyingRecipe::new, ctx.getId().withPath("infused_dragon_breath"))
                        .withCondition(ModIntegration.APOTHIC_ENCHANTING.condition())
                        .withCondition(CEIApotheosisModuleCondition.ENCHANTMENT)
                        .require(Ench.Items.INFUSED_BREATH)
                        .output(ctx.get(), CEIFluidUnits.millibuckets(250))
                        .output(Items.GLASS_BOTTLE)
                        .build(prov);
                new ProcessingRecipeBuilder<>(FillingRecipe::new, ctx.getId().withPath("infused_dragon_breath"))
                        .withCondition(ModIntegration.APOTHIC_ENCHANTING.condition())
                        .withCondition(CEIApotheosisModuleCondition.ENCHANTMENT)
                        .require(ctx.get(), CEIFluidUnits.millibuckets(250))
                        .require(Items.GLASS_BOTTLE)
                        .output(Ench.Items.INFUSED_BREATH)
                        .build(prov);
            })
            .register();
    public static final FluidEntry<TypedFlowableFluid.Flowing> INFUSED_DRAGON_BREATH_FLOWING = INFUSED_DRAGON_BREATH;

    public static class ModTags extends IntrinsicTagRegistry<Fluid, RegistrateTagsProvider.IntrinsicImpl<Fluid>> {
        public final TagKey<Fluid> infusing_ingredients = tag("infusing/ingredients", "Infusing Reagent");

        public ModTags() {
            super(CEIACommon.ID, Registries.FLUID);
        }

        @Override
        public void generate(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
            super.generate(provider);
            provider.addTag(COMMON_DRAGON_BREATH)
                    .addOptional(CEICommon.asResource("infused_dragon_breath"))
                    .addOptional(CEICommon.asResource("flowing_infused_dragon_breath"));
            provider.addTag(infusing_ingredients)
                    .add(CEIFluids.EXPERIENCE.getSource())
                    .add(CEIFluids.EXPERIENCE_FLOWING.get());
        }
    }

    public static void register() {
        REGISTRATE.registerFluidTags(MOD_TAGS);
        Registry.register(
                PortingLibFluids.FLUID_TYPES,
                CEIACommon.REGISTRATE.asResource("infused_dragon_breath"),
                INFUSED_DRAGON_BREATH_TYPE);
    }

    public static void initialize() {
        if (!Apotheosis.enableEnch)
            return;
        registerBaseFluidInteractions();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> registerCdpFluidInteractions());
        registerOpenPipeEffects();
        registerDispenserBehavior();
        PipeCollisionEvent.FLOW.register(Events::onPipeCollisionFlow);
        PipeCollisionEvent.SPILL.register(Events::onPipeCollisionSpill);
    }

    public static void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(
                INFUSED_DRAGON_BREATH.getBucket().get(), StandardDispenserBehaviour.INSTANCE);
    }

    public static class Events {
        public static void onPipeCollisionFlow(final PipeCollisionEvent.Flow event) {
            FluidType first = event.getFirstFluid().getFluidType();
            FluidType second = event.getSecondFluid().getFluidType();
            if (first == PortingLibFluids.LAVA_TYPE && second == INFUSED_DRAGON_BREATH_TYPE) {
                event.setState(Blocks.END_STONE.defaultBlockState());
            } else if (second == PortingLibFluids.LAVA_TYPE && first == INFUSED_DRAGON_BREATH_TYPE) {
                event.setState(Blocks.END_STONE.defaultBlockState());
            }
        }

        public static void onPipeCollisionSpill(final PipeCollisionEvent.Spill event) {
            Fluid world = event.getWorldFluid();
            Fluid pipe = event.getPipeFluid();
            FluidType worldType = world.getFluidType();
            FluidType pipeType = pipe.getFluidType();
            if (worldType == PortingLibFluids.LAVA_TYPE && pipeType == INFUSED_DRAGON_BREATH_TYPE) {
                if (world.isSource(world.defaultFluidState())) {
                    event.setState(Blocks.OBSIDIAN.defaultBlockState());
                } else {
                    event.setState(Blocks.END_STONE.defaultBlockState());
                }
            } else if (pipeType == PortingLibFluids.LAVA_TYPE && worldType == INFUSED_DRAGON_BREATH_TYPE) {
                if (pipe.isSource(pipe.defaultFluidState())) {
                    event.setState(Blocks.OBSIDIAN.defaultBlockState());
                } else {
                    event.setState(Blocks.END_STONE.defaultBlockState());
                }
            }
        }
    }

    static void registerBaseFluidInteractions() {
        FluidInteractionRegistry.addInteraction(PortingLibFluids.LAVA_TYPE, new InteractionInformation(
                INFUSED_DRAGON_BREATH_TYPE,
                fluidState -> fluidState.isSource()
                        ? Blocks.CRYING_OBSIDIAN.defaultBlockState()
                        : Blocks.END_STONE.defaultBlockState()));
    }

    static void registerCdpFluidInteractions() {
        if (cdpInteractionsRegistered)
            return;
        cdpInteractionsRegistered = true;
        FluidType dragonBreathType = BuiltInRegistries.FLUID.get(CDP_DRAGON_BREATH).getFluidType();
        FluidInteractionRegistry.addInteraction(dragonBreathType, new InteractionInformation(
                INFUSED_DRAGON_BREATH_TYPE,
                fluidState -> fluidState.isSource()
                        ? Blocks.OBSIDIAN.defaultBlockState()
                        : Blocks.END_STONE.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(INFUSED_DRAGON_BREATH_TYPE, new InteractionInformation(
                dragonBreathType,
                fluidState -> fluidState.isSource()
                        ? Blocks.AMETHYST_BLOCK.defaultBlockState()
                        : Blocks.END_STONE.defaultBlockState()));
    }

    static void registerOpenPipeEffects() {
        OpenPipeEffectHandler.REGISTRY.register(INFUSED_DRAGON_BREATH.getSource(), new DragonsBreathOpenPipeEffect());
    }
}
