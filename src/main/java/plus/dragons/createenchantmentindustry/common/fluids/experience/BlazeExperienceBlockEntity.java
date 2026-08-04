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

package plus.dragons.createenchantmentindustry.common.fluids.experience;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity.CreativeSmartFluidTank;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import java.util.List;
import java.util.function.Consumer;
import net.createmod.catnip.lang.LangBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.fluids.tank.ConfigurableFluidTank;
import plus.dragons.createdragonsplus.common.fluids.tank.FluidTankBehaviour;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlockEntity;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;
import plus.dragons.createenchantmentindustry.util.BlazeLightningHelper;
import plus.dragons.createenchantmentindustry.util.CEIFluidUnits;
import plus.dragons.createenchantmentindustry.util.CEILang;
import plus.dragons.createenchantmentindustry.util.CEITransfer;

@FieldsNullabilityUnknownByDefault
public abstract class BlazeExperienceBlockEntity extends BlazeBlockEntity implements IHaveGoggleInformation {
    public static final String LIGHTNING_BOLT_EXPERIENCE_CHARGE_KEY = BlazeLightningHelper.LIGHTNING_BOLT_EXPERIENCE_CHARGE_KEY;
    public static final TagKey<Block> LIGHTNING_ROD_BLOCKS = BlazeLightningHelper.LIGHTNING_ROD_BLOCKS;
    public static final TagKey<PoiType> LIGHTNING_ROD_POINT_OF_INTEREST_TYPES = BlazeLightningHelper.LIGHTNING_ROD_POINT_OF_INTEREST_TYPES;
    private boolean isCreative;
    protected FluidTankBehaviour tanks;

    public BlazeExperienceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract ConfigurableFluidTank createNormalTank(Consumer<FluidStack> fluidUpdateCallback);

    protected abstract ConfigurableFluidTank createSpecialTank(Consumer<FluidStack> fluidUpdateCallback);

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tanks = new FluidTankBehaviour(this, List.of(this::createNormalTank, this::createSpecialTank), false);
        behaviours.add(tanks);
    }

    @Override
    public boolean isCreative() {
        return isCreative;
    }

    @Override
    public HeatLevel getHeatLevel() {
        if (getSpecialExperience() > 0)
            return HeatLevel.SEETHING;
        double experience = getNormalExperience();
        if (experience > 0) {
            boolean lowPercent = experience / getNormalTank().getCapacity() < 0.0125;
            return lowPercent ? HeatLevel.FADING : HeatLevel.KINDLED;
        }
        return HeatLevel.SMOULDERING;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("isCreative", isCreative);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        isCreative = compound.getBoolean("isCreative");
        if (isCreative)
            setCreativeTanks(getHeatLevelFromBlock());
    }

    public SmartFluidTank getNormalTank() {
        return tanks.getHandlers()[0];
    }

    public SmartFluidTank getSpecialTank() {
        return tanks.getHandlers()[1];
    }

    public int getNormalExperience() {
        return Math.toIntExact(CEIFluidUnits.toMillibuckets(getNormalTank().getFluid().getAmount()));
    }

    public int getSpecialExperience() {
        return Math.toIntExact(CEIFluidUnits.toMillibuckets(getSpecialTank().getFluid().getAmount()));
    }

    public int getTotalExperience() {
        return getNormalExperience() + getSpecialExperience();
    }

    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return tanks != null && !isRemoved() && (side == null || side == Direction.DOWN)
                ? tanks.getCapability()
                : null;
    }

    public boolean consumeExperience(int amount, boolean special, boolean simulate) {
        var fluid = CEIFluidUnits.stack(CEIFluids.EXPERIENCE.getSource(), amount);
        var tank = special ? getSpecialTank() : getNormalTank();
        return CEITransfer.extractExact(tank, fluid, simulate);
    }

    public boolean applyExperienceFuel(ExperienceFuel fuel, boolean forceOverflow, boolean simulate) {
        assert level != null;
        if (isCreative)
            return false;
        boolean special = fuel.special();
        var tank = special ? getSpecialTank() : getNormalTank();
        if (!(tank instanceof ConfigurableFluidTank configurableTank)) {
            return false;
        }
        var fluid = configurableTank.getFluid();
        if (!fluid.isEmpty() && fluid.getFluid() != CEIFluids.EXPERIENCE.getSource())
            return false;
        int experience = fuel.experience();
        var experienceFluid = CEIFluidUnits.stack(CEIFluids.EXPERIENCE.getSource(), experience);
        try (Transaction transaction = Transaction.openOuter()) {
            long fill = configurableTank.insert(
                    experienceFluid.getType(), experienceFluid.getAmount(), transaction, true);
            if (fill == 0 || fill != experienceFluid.getAmount() && !forceOverflow)
                return false;
            if (simulate)
                return true;
            transaction.commit();
        }
        if (level.isClientSide)
            spawnParticleBurst(special);

        HeatLevel heat = getHeatLevelFromBlock();
        playSound();
        updateBlockState();

        if (heat != getHeatLevelFromBlock())
            level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
                    .125f + level.random.nextFloat() * .125f,
                    1.15f - level.random.nextFloat() * .25f);
        notifyUpdate();
        return true;
    }

    public void applyCreativeFuel() {
        assert level != null;
        isCreative = true;
        HeatLevel next = getHeatLevelFromBlock().nextActiveLevel();
        if (level.isClientSide) {
            spawnParticleBurst(next.isAtLeast(HeatLevel.SEETHING));
            return;
        }
        playSound();
        if (next == HeatLevel.FADING)
            next = next.nextActiveLevel();
        setCreativeTanks(next);
        setBlockHeat(next);
        notifyUpdate();
    }

    protected void setCreativeTanks(HeatLevel heatLevel) {
        switch (heatLevel) {
            case KINDLED -> {
                long capacity = getNormalTank().getCapacity();
                tanks.setTank(0, callback -> new CreativeSmartFluidTank(capacity, callback));
                getNormalTank().setFluid(new FluidStack(FluidVariant.of(CEIFluids.EXPERIENCE.getSource()), capacity));
            }
            case SEETHING -> {
                long capacity = getSpecialTank().getCapacity();
                tanks.setTank(1, callback -> new CreativeSmartFluidTank(capacity, callback));
                getSpecialTank().setFluid(new FluidStack(FluidVariant.of(CEIFluids.EXPERIENCE.getSource()), capacity));
            }
            default -> {
                tanks.setTank(0, this::createNormalTank);
                tanks.setTank(1, this::createSpecialTank);
            }
        }
    }

    protected @Nullable BlockPos getStrikePos() {
        assert level != null;
        return BlazeLightningHelper.getStrikePos(level, worldPosition);
    }

    protected boolean strikeLightning(ServerLevel level, BlockPos strikePos) {
        return BlazeLightningHelper.strikeLightning(level, strikePos);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);
        boolean special = false;
        for (var tank : tanks.getHandlers()) {
            CEILang.translate(special ? "gui.goggles.blaze_experience.super_experience" : "gui.goggles.blaze_experience.experience")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);
            CreateLang.builder()
                    .add(CreateLang.number(CEIFluidUnits.toMillibuckets(tank.getFluid().getAmount()))
                            .add(mb)
                            .style(special ? ChatFormatting.BLUE : ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(CEIFluidUnits.toMillibuckets(tank.getCapacity()))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 2);
            special = true;
        }
        return true;
    }
}
