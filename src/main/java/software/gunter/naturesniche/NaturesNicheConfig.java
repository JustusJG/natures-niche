package software.gunter.naturesniche;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.interfaces.IConfig;
import software.gunter.naturesniche.utils.RegistryUtil;

import java.util.*;

public class NaturesNicheConfig {
    public void updateCrops() {
        RegistryUtil.getCrops().forEach(crop -> {
            String cropIdentifier = crop.toString()
                    .replace("Block{", "")
                    .replace("}", "");

            NaturesNicheMod.LOGGER.info("Updating crop " + cropIdentifier + "...");
            Map<String, Float> map = new HashMap<>();

            if (crops.containsKey(cropIdentifier)) {
                NaturesNicheMod.LOGGER.info(cropIdentifier + " is existing. Load cropConfig.");
                map = crops.get(cropIdentifier).getModifierMap();
                ;
            }

            for (Biome biome : RegistryUtil.getBiomes()) {
                map.putIfAbsent(String.valueOf(BuiltinRegistries.BIOME.getId(biome)), 1.0f);
            }
            crops.put(cropIdentifier, new CropConfig(map));
            NaturesNicheMod.LOGGER.info("Crop " + cropIdentifier + " updated.");
        });
    }


    public void updateBiomes() {
        RegistryUtil.getBiomes().forEach(biome -> {
            String biomeIdentifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));

            NaturesNicheMod.LOGGER.info("Updating biome " + biomeIdentifier + "...");
            Map<String, Float> map;

            if (biomes.containsKey(biomeIdentifier)) {
                NaturesNicheMod.LOGGER.info(biomeIdentifier + " is existing. Load biomeConfig.");
                map = biomes.get(biomeIdentifier).getModifierMap();
            } else {
                map = new HashMap<>();
            }

            RegistryUtil.getCrops().forEach(crop -> map.putIfAbsent(String.valueOf(Registry.BLOCK.getId(crop)), 1.0f));

            biomes.put(biomeIdentifier, new BiomeConfig(map));
            NaturesNicheMod.LOGGER.info("Biome " + biomeIdentifier + " updated.");
        });
    }

    public NaturesNicheConfig() {
        updateCrops();
        updateBiomes();
    }

    private final boolean priority = true; // true = crop, false = biome
    private final Map<String, CropConfig> crops = new HashMap<>();

    private final Map<String, BiomeConfig> biomes = new HashMap<>();

    private static class CropConfig implements IConfig {
        private final Map<String, Float> biomeModifier;

        public CropConfig(Map<String, Float> biomeModifier) {
            this.biomeModifier = biomeModifier;
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
