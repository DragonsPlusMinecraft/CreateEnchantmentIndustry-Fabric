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

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class ExperienceFluidDropContext {
    private static final ThreadLocal<Integer> SUPPRESSED = ThreadLocal.withInitial(() -> 0);

    private ExperienceFluidDropContext() {}

    public static void suppressDuring(Runnable action) {
        SUPPRESSED.set(SUPPRESSED.get() + 1);
        try {
            action.run();
        } finally {
            int depth = SUPPRESSED.get() - 1;
            if (depth <= 0) SUPPRESSED.remove();
            else SUPPRESSED.set(depth);
        }
    }

    public static void dropExperience(ServerLevel level, BlockState removedState, BlockPos pos, int experience) {
        if (experience <= 0 || SUPPRESSED.get() > 0)
            return;
        ExperienceOrb.award(level, Vec3.atCenterOf(pos), experience);
    }
}
