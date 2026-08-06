## Create: Enchantment Industry 2.5.0-pre.2 for Fabric 1.20.1

This preview ports the complete Create: Enchantment Industry 2.5 feature set to Fabric 1.20.1 with Java 17 and Create Fabric 6.0.8.1.

### Highlights

- Ported the core machines, recipes, fluids, Ponder scenes, Flywheel visuals, networking, and data reload support to Fabric.
- Ported the optional Apotheosis integrations to Zenith 1.2.5, including enchanting, affix, recipe, loot, and JEI integration.
- Added optional JEI support while keeping both JEI and Zenith safe to omit.
- Preserved the `create_enchantment_industry` namespace, content identifiers, configuration keys, and data paths.

### Fixed in pre.2

- Restored full-bright rendering for model elements that declare `block_light` and `sky_light` metadata.
- Fixed the glowing panels on full Ender Woven Bags in placed, contraption, and item rendering contexts.
- Applied the same model-lighting fix to Experience Lanterns, Infuser needles, Gem Cutters, and Affix Augmentors.

### Compatibility

- Requires Fabric API, Create Fabric 6.0.8.1, and Create: Dragons Plus Fabric 1.11.4-preview.1.
- JEI 15.20.0.106 and Zenith 1.2.5 are optional integrations.
- Touhou Little Maid integration is not included because the mod is unavailable for Fabric 1.20.1.
- This port supports new worlds only; Forge and older Fabric worlds are not supported for upgrading.
