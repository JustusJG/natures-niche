package software.gunter.naturesniche;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.*;

public class NaturesNicheConfig {
    public void updateCrops() {
        Registry.ITEM.forEach(item -> {
            if(item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if(block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof SaplingBlock) {
                    String cropIdentifier = ((BlockItem) item).getBlock().toString()
                            .replace("Block{", "")
                            .replace("}", "");

                    NaturesNicheMod.LOGGER.info("Updating crop " + cropIdentifier + "...");
                    Map<String, Float> map = new HashMap<>();

                    if (cropConfigs.containsKey(cropIdentifier)) {
                        NaturesNicheMod.LOGGER.info(cropIdentifier + " is existing. Load cropConfig.");
                        map = cropConfigs.get(cropIdentifier).getBiomeModifier();;
                    }

                    for (Biome biome : BuiltinRegistries.BIOME) {
                        map.putIfAbsent(String.valueOf(BuiltinRegistries.BIOME.getId(biome)), 1.0f);
                    }
                    cropConfigs.put(cropIdentifier, new CropConfig(map));
                    NaturesNicheMod.LOGGER.info("Crop " + cropIdentifier + " updated.");
                }
            }
        });
    }

    public NaturesNicheConfig() {
        updateCrops();
    }

    private final Map<String, CropConfig> cropConfigs = new HashMap<>();

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

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        if (cropConfigs.containsKey(cropIdentifier)) {
            return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
        }
        return 1.0f;
    }
}
