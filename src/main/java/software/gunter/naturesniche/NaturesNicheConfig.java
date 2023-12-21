package software.gunter.naturesniche;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
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

            if (cropConfigs.containsKey(cropIdentifier)) {
                NaturesNicheMod.LOGGER.info(cropIdentifier + " is existing. Load cropConfig.");
                map = cropConfigs.get(cropIdentifier).getBiomeModifier();
                ;
            }

            for (Biome biome : RegistryUtil.getBiomes()) {
                map.putIfAbsent(String.valueOf(BuiltinRegistries.BIOME.getId(biome)), 1.0f);
            }
            cropConfigs.put(cropIdentifier, new CropConfig(map));
            NaturesNicheMod.LOGGER.info("Crop " + cropIdentifier + " updated.");
        });
    }


    public void updateBiomes() {
        RegistryUtil.getBiomes().forEach(biome -> {
            String biomeIdentifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));

            NaturesNicheMod.LOGGER.info("Updating biome " + biomeIdentifier + "...");
            Map<String, Float> map;

            if (biomeConfigs.containsKey(biomeIdentifier)) {
                NaturesNicheMod.LOGGER.info(biomeIdentifier + " is existing. Load biomeConfig.");
                map = biomeConfigs.get(biomeIdentifier).getCropModifier();
            } else {
                map = new HashMap<>();
            }

            RegistryUtil.getCrops().forEach(crop -> map.putIfAbsent(String.valueOf(Registry.BLOCK.getId(crop)), 1.0f));

            biomeConfigs.put(biomeIdentifier, new BiomeConfig(map));
            NaturesNicheMod.LOGGER.info("Biome " + biomeIdentifier + " updated.");
        });
    }

    public NaturesNicheConfig() {
        updateCrops();
        updateBiomes();
    }

    private final boolean priority = true; // true = crop, false = biome
    private final Map<String, CropConfig> cropConfigs = new HashMap<>();

    private final Map<String, BiomeConfig> biomeConfigs = new HashMap<>();

    private static class CropConfig {
        private final Map<String, Float> biomeModifier;

        public CropConfig(Map<String, Float> biomeModifier) {
            this.biomeModifier = biomeModifier;
        }

        public float getModifier(String biomeIdentifier) {
            if (this.biomeModifier.containsKey(biomeIdentifier)) {
                return this.biomeModifier.get(biomeIdentifier);
            }
            return 1.0f;
        }

        public Map<String, Float> getBiomeModifier() {
            return biomeModifier;
        }
    }

    private static class BiomeConfig {
        private final Map<String, Float> cropModifier;

        public BiomeConfig(Map<String, Float> cropModifier) {
            this.cropModifier = cropModifier;
        }

        public float getModifier(String cropIdentifier) {
            if (this.cropModifier.containsKey(cropIdentifier)) {
                return this.cropModifier.get(cropIdentifier);
            }
            return 1.0f;
        }

        public Map<String, Float> getCropModifier() {
            return cropModifier;
        }
    }

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        if (priority) {
            if (cropConfigs.containsKey(cropIdentifier)) {
                CropConfig config = cropConfigs.get(cropIdentifier);
                if (config.getBiomeModifier().containsKey(biomeIdentifier)) {
                    return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
                }
            }
            if (biomeConfigs.containsKey(biomeIdentifier)) {
                BiomeConfig config = biomeConfigs.get(biomeIdentifier);
                if (config.getCropModifier().containsKey(cropIdentifier)) {
                    return cropConfigs.get(biomeIdentifier).getModifier(cropIdentifier);
                }
            }
        } else {
            if (biomeConfigs.containsKey(biomeIdentifier)) {
                BiomeConfig config = biomeConfigs.get(biomeIdentifier);
                if (config.getCropModifier().containsKey(cropIdentifier)) {
                    return cropConfigs.get(biomeIdentifier).getModifier(cropIdentifier);
                }
            }
            if (cropConfigs.containsKey(cropIdentifier)) {
                CropConfig config = cropConfigs.get(cropIdentifier);
                if (config.getBiomeModifier().containsKey(biomeIdentifier)) {
                    return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
                }
            }
        }
        return 1.0f;
    }

    public boolean getPriority() {
        return priority;
    }
}
