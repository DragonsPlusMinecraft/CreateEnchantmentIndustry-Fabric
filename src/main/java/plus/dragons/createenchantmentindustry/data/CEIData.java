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

package plus.dragons.createenchantmentindustry.data;

import static plus.dragons.createenchantmentindustry.common.CEICommon.REGISTRATE;

import com.tterrag.registrate.providers.ProviderType;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.RegistrySetBuilder;
import plus.dragons.createenchantmentindustry.client.ponder.CEIPonderPlugin;
import plus.dragons.createenchantmentindustry.common.CEICommon;
import plus.dragons.createenchantmentindustry.common.registry.CEIAdvancements;
import plus.dragons.createenchantmentindustry.integration.ModIntegration;

public class CEIData implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        initializeIntegrationData(
                ModIntegration.APOTHEOSIS,
                "plus.dragons.createenchantmentindustry.integration.apotheosis.data.CEIAXData");
        initializeIntegrationData(
                ModIntegration.APOTHIC_ENCHANTING,
                "plus.dragons.createenchantmentindustry.integration.apothic_enchanting.data.CEIAData");
        REGISTRATE.registerBuiltinLocalization("interface");
        REGISTRATE.registerForeignLocalization();
        if (PonderIndex.streamPlugins().noneMatch(plugin -> plugin.getModId().equals(CEICommon.ID)))
            PonderIndex.addPlugin(new CEIPonderPlugin());
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> PonderIndex
                .getLangAccess()
                .provideLang(CEICommon.ID, provider::add));
        REGISTRATE.registerExtraLocalization(CEIAdvancements::provideLang);

        ExistingFileHelper existingFileHelper = ExistingFileHelper.withResourcesFromArg();
        FabricDataGenerator.Pack pack = generator.createPack();
        REGISTRATE.setExistingFileHelper(existingFileHelper);
        REGISTRATE.setupDatagen(pack, existingFileHelper);
        pack.addProvider(CEIGenerateEntriesProvider::new);
        pack.addProvider((FabricDataOutput output) -> new CEIDataMapProvider(output));
        pack.addProvider((FabricDataOutput output) -> new CEIRecipeProvider(output));
        pack.addProvider(CEIAdvancements::new);
        pack.addProvider(CEIEnchantmentTagsProvider::new);
        registerIntegrationProviders(
                pack,
                ModIntegration.APOTHIC_ENCHANTING,
                "plus.dragons.createenchantmentindustry.integration.apothic_enchanting.data.CEIAData");
        registerIntegrationProviders(
                pack,
                ModIntegration.APOTHEOSIS,
                "plus.dragons.createenchantmentindustry.integration.apotheosis.data.CEIAXData");
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        CEIGenerateEntriesProvider.addBootstraps(builder);
    }

    private static void initializeIntegrationData(ModIntegration integration, String className) {
        invokeIntegration(integration, className, "initialize", new Class<?>[0], new Object[0]);
    }

    private static void registerIntegrationProviders(
            FabricDataGenerator.Pack pack, ModIntegration integration, String className) {
        invokeIntegration(
                integration,
                className,
                "registerProviders",
                new Class<?>[] { FabricDataGenerator.Pack.class },
                new Object[] { pack });
    }

    private static void invokeIntegration(
            ModIntegration integration,
            String className,
            String method,
            Class<?>[] parameterTypes,
            Object[] arguments) {
        if (!integration.enabledForRegistration())
            return;
        try {
            Class.forName(className, true, CEIData.class.getClassLoader())
                    .getMethod(method, parameterTypes)
                    .invoke(null, arguments);
        } catch (ClassNotFoundException ignored) {
            // The optional source set is omitted from core-only builds.
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException(
                    "Failed to initialize " + integration.id() + " data generation", exception);
        }
    }
}
