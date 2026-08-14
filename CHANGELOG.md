## Create: Enchantment Industry 2.5.1-c for Fabric 1.20.1

This beta hotfix addresses the production-only Spout crash reported in [#477](https://github.com/DragonsPlusMinecraft/CreateEnchantmentIndustry/issues/477).

### Fixed

- Fixed Spouts crashing when they attempted to process any item in a production Fabric environment. All three Mending integration injection points are now remapped correctly.

### Dependencies

- Updated the required Create: Dragons Plus Fabric version to `1.11.7-c`, which includes production-safe Fabric Mixin mappings.
