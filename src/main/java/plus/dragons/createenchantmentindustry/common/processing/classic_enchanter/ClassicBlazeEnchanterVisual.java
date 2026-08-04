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

package plus.dragons.createenchantmentindustry.common.processing.classic_enchanter;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.content.processing.burner.BlazeBurnerVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.client.renderer.blockentity.BlazeBlockEntityClient;
import plus.dragons.createdragonsplus.util.CodeReference;
import plus.dragons.createdragonsplus.util.FieldsNullabilityUnknownByDefault;

@FieldsNullabilityUnknownByDefault
@CodeReference(value = BlazeBurnerVisual.class, source = "create", license = "mit")
public class ClassicBlazeEnchanterVisual extends AbstractBlockEntityVisual<ClassicBlazeEnchanterBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {
    private BlazeBurnerBlock.HeatLevel heatLevel;
    private boolean active;
    private final TransformedInstance head;
    private TransformedInstance smallRods;
    private TransformedInstance largeRods;
    private ScrollInstance flame;
    private PartialModel gogglesModel;
    private TransformedInstance goggles;

    public ClassicBlazeEnchanterVisual(VisualizationContext ctx, ClassicBlazeEnchanterBlockEntity blockEntity, float partialTicks) {
        super(ctx, blockEntity, partialTicks);
        this.heatLevel = blockEntity.getHeatLevel();
        this.active = blockEntity.isActive();
        PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(heatLevel, active);
        this.head = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(blazeModel)).createInstance();
        this.head.light(LightTexture.FULL_BRIGHT);
        animate(partialTicks);
    }

    protected void animate(float partialTicks) {
        float animation = blockEntity.headAnimation().getValue(partialTicks) * .175f;
        boolean active = animation > 0.125f;
        BlazeBurnerBlock.HeatLevel heatLevel = blockEntity.getHeatLevelForRender();
        // Update head and rods
        if (active != this.active || heatLevel != this.heatLevel) {
            this.active = active;

            PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(heatLevel, active);
            instancerProvider()
                    .instancer(InstanceTypes.TRANSFORMED, Models.partial(blazeModel))
                    .stealInstance(head);

            boolean needsRods = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            boolean hasRods = this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);

            if (needsRods && !hasRods) {
                PartialModel rodsModel = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS
                        : AllPartialModels.BLAZE_BURNER_RODS;
                PartialModel rodsModel2 = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2
                        : AllPartialModels.BLAZE_BURNER_RODS_2;

                smallRods = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(rodsModel))
                        .createInstance();
                largeRods = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(rodsModel2))
                        .createInstance();

                smallRods.light(LightTexture.FULL_BRIGHT);
                largeRods.light(LightTexture.FULL_BRIGHT);

            } else if (!needsRods && hasRods) {
                if (smallRods != null)
                    smallRods.delete();
                if (largeRods != null)
                    largeRods.delete();
                smallRods = null;
                largeRods = null;
            }

            this.heatLevel = heatLevel;
        }
        // Update flame
        if (active && flame == null) {
            setupFlameInstance();
        } else if (!active && flame != null) {
            flame.delete();
            flame = null;
        }
        // Update goggles
        PartialModel gogglesModel = null;
        if (goggles == null) {
            if (gogglesModel != null) {
                goggles = instancerProvider()
                        .instancer(InstanceTypes.TRANSFORMED, Models.partial(gogglesModel))
                        .createInstance();
                goggles.light(LightTexture.FULL_BRIGHT);
            }
        } else {
            if (gogglesModel == null) {
                goggles.delete();
                goggles = null;
            } else if (this.gogglesModel != gogglesModel) {
                instancerProvider()
                        .instancer(InstanceTypes.TRANSFORMED, Models.partial(gogglesModel))
                        .stealInstance(goggles);
            }
            this.gogglesModel = gogglesModel;
        }
        // Setup transforms
        int seed = blockEntity.hashCode();
        float renderTime = AnimationTickHolder.getRenderTime(level);
        float seededRenderTime = renderTime + (seed % 13) * 16f;
        float offsetScale = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) ? 64 : 16;
        float offset = Mth.sin((seededRenderTime / 16f) % (2 * Mth.PI)) / offsetScale;
        float headY = offset - (animation * .75f) + 0.2f;
        float horizontalAngle = AngleHelper.rad(blockEntity.headAngle().getValue(partialTicks));

        head.setIdentityTransform()
                .translate(getVisualPosition())
                .translateY(headY)
                .translate(Translate.CENTER)
                .rotateY(horizontalAngle)
                .translateBack(Translate.CENTER)
                .setChanged();

        if (goggles != null && gogglesModel != null) {
            this.setupGogglesTransform(gogglesModel, goggles, headY, horizontalAngle);
        }

        if (smallRods != null) {
            float offsetSmallRods = Mth.sin(seededRenderTime / 16f + Mth.PI) % (2 * Mth.PI) / offsetScale;

            smallRods.setIdentityTransform()
                    .translate(getVisualPosition())
                    .translateY(offsetSmallRods + animation + .125f)
                    .setChanged();
        }

        if (largeRods != null) {
            float offsetLargeRods = Mth.sin((seededRenderTime / 16f + Mth.PI / 2) % (2 * Mth.PI)) / offsetScale;

            largeRods.setIdentityTransform()
                    .translate(getVisualPosition())
                    .translateY(offsetLargeRods + animation - 3 / 16f)
                    .setChanged();
        }
    }

    protected void setupGogglesTransform(PartialModel gogglesModel, TransformedInstance goggles, float headY, float horizontalAngle) {
        goggles.setIdentityTransform()
                .translate(getVisualPosition())
                .rotateCentered(horizontalAngle, Direction.UP)
                .translateY(headY + 0.2f)
                .setChanged();
    }

    protected void setupFlameInstance() {
        flame = instancerProvider()
                .instancer(AllInstanceTypes.SCROLLING, Models.partial(AllPartialModels.BLAZE_BURNER_FLAME))
                .createInstance();

        flame.position(getVisualPosition()).light(LightTexture.FULL_BRIGHT);

        SpriteShiftEntry spriteShift = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING
                ? AllSpriteShifts.SUPER_BURNER_FLAME
                : AllSpriteShifts.BURNER_FLAME;

        float spriteWidth = spriteShift.getTarget().getU1() - spriteShift.getTarget().getU0();
        float spriteHeight = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
        float speed = 1 / 32f + 1 / 64f * heatLevel.ordinal();

        flame.speedU = speed / 2;
        flame.speedV = speed;

        flame.scaleU = spriteWidth / 2;
        flame.scaleV = spriteHeight / 2;

        flame.diffU = spriteShift.getTarget().getU0() - spriteShift.getOriginal().getU0();
        flame.diffV = spriteShift.getTarget().getV0() - spriteShift.getOriginal().getV0();
    }

    @Override
    public void tick(TickableVisual.Context context) {
        BlazeBlockEntityClient.tickAnimation(blockEntity);
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        if (isVisible(context.frustum()) && !doDistanceLimitThisFrame(context)) {
            animate(context.partialTick());
        }
    }

    @Override
    protected void _delete() {
        head.delete();
        if (smallRods != null) {
            smallRods.delete();
        }
        if (largeRods != null) {
            largeRods.delete();
        }
        if (flame != null) {
            flame.delete();
        }
        if (goggles != null) {
            goggles.delete();
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {}

    @Override
    public void updateLight(float partialTick) {}
}
