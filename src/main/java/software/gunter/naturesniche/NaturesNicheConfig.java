package software.gunter.naturesniche;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import software.gunter.naturesniche.interfaces.IConfig;
import software.gunter.naturesniche.utils.RegistryUtil;

import java.util.*;

public class NaturesNicheConfig {
    public void updateCrop(String identifier) {
        NaturesNicheMod.LOGGER.info("Updating crop " + identifier + "...");
        CropConfig cropConfig = crops.getOrDefault(identifier, new CropConfig());
        cropConfig.update();
        crops.put(identifier, cropConfig);
        NaturesNicheMod.LOGGER.info("Crop " + identifier + " updated.");
    }

    public void updateCrops() {
        RegistryUtil.getCrops().forEach(crop -> {
            String cropIdentifier = crop.toString()
                    .replace("Block{", "")
                    .replace("}", "");
            updateCrop(cropIdentifier);
        });
    }

    public void updateBiome(String identifier) {
        NaturesNicheMod.LOGGER.info("Updating biome " + identifier + "...");
        BiomeConfig biomeConfig = biomes.getOrDefault(identifier, new BiomeConfig());
        biomeConfig.update();
        biomes.put(identifier, biomeConfig);
        NaturesNicheMod.LOGGER.info("Biome " + identifier + " updated.");
    }

    public void updateBiomes() {
        RegistryUtil.getBiomes().forEach(biome -> {
            String biomeIdentifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));
            updateBiome(biomeIdentifier);
        });
    }

    private final boolean priority = true; // true = crop, false = biome
    private final Map<String, CropConfig> crops = new HashMap<>();

    private final Map<String, BiomeConfig> biomes = new HashMap<>();

    private static class CropConfig implements IConfig {
        private final Map<String, Float> biomeModifier;

        public CropConfig(Map<String, Float> biomeModifier) {
            this.biomeModifier = biomeModifier;
        }

        public CropConfig() {
            this(new HashMap<>());
            update();
        }

        @Override
        public void update() {
            RegistryUtil.getBiomes().forEach(biome -> biomeModifier.putIfAbsent(String.valueOf(BuiltinRegistries.BIOME.getId(biome)), 1.0f));
        }

        @Override
        public float getModifier(String biomeIdentifier) {
            if (this.biomeModifier.containsKey(biomeIdentifier)) {
                return this.biomeModifier.get(biomeIdentifier);
            }
            return 1.0f;
        }

        @Override
        public Map<String, Float> getModifierMap() {
            return biomeModifier;
        }
    }

    private static class BiomeConfig implements IConfig {
        private final Map<String, Float> cropModifier;

        public BiomeConfig(Map<String, Float> cropModifier) {
            this.cropModifier = cropModifier;
        }

        public BiomeConfig() {
            this(new HashMap<>());
            update();
        }

        @Override
        public void update() {
            RegistryUtil.getCrops().forEach(crop -> cropModifier.putIfAbsent(String.valueOf(Registry.BLOCK.getId(crop)), 1.0f));
        }

        @Override
        public float getModifier(String cropIdentifier) {
            if (this.cropModifier.containsKey(cropIdentifier)) {
                return this.cropModifier.get(cropIdentifier);
            }
            return 1.0f;
        }

        @Override
        public Map<String, Float> getModifierMap() {
            return cropModifier;
        }
    }

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        if (priority) {
            if (crops.containsKey(cropIdentifier)) {
                CropConfig config = crops.get(cropIdentifier);
                if (config.getModifierMap().containsKey(biomeIdentifier)) {
                    return crops.get(cropIdentifier).getModifier(biomeIdentifier);
                }
            }
            if (biomes.containsKey(biomeIdentifier)) {
                BiomeConfig config = biomes.get(biomeIdentifier);
                if (config.getModifierMap().containsKey(cropIdentifier)) {
                    return crops.get(biomeIdentifier).getModifier(cropIdentifier);
                }
            }
        } else {
            if (biomes.containsKey(biomeIdentifier)) {
                BiomeConfig config = biomes.get(biomeIdentifier);
                if (config.getModifierMap().containsKey(cropIdentifier)) {
                    return crops.get(biomeIdentifier).getModifier(cropIdentifier);
                }
            }
            if (crops.containsKey(cropIdentifier)) {
                CropConfig config = crops.get(cropIdentifier);
                if (config.getModifierMap().containsKey(biomeIdentifier)) {
                    return crops.get(cropIdentifier).getModifier(biomeIdentifier);
                }
            }
        }
        return 1.0f;
    }

    public boolean getPriority() {
        return priority;
    }
}
