## Create: Enchantment Industry 2.5.1-d for Fabric 1.20.1

This beta hotfix resolves two Fabric client regressions.

### Fixed

- Restored the hats on Blaze Enchanter and Blaze Forger item models by registering their custom renderers before the first model bake ([#478](https://github.com/DragonsPlusMinecraft/CreateEnchantmentIndustry/issues/478)). The same fix also covers the Classic Blaze Enchanter and the optional Blaze Composer.
- Fixed the Experience Lantern crashing Ponder scenes when its fluid tank was restored before the block entity had been attached to a world ([#479](https://github.com/DragonsPlusMinecraft/CreateEnchantmentIndustry/issues/479)). Lantern light levels now synchronize safely after initialization and only update the block state when needed.

### Compatibility

- Dependency requirements are unchanged from 2.5.1-c.
