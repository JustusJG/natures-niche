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
                    Map<String, Float> map = new HashMap<>();
                    for (Biome biome : BuiltinRegistries.BIOME) {
                        map.put(String.valueOf(BuiltinRegistries.BIOME.getId(biome)), 1.0f);
                    }
                    String cropIdentifier = ((BlockItem) item).getBlock().toString()
                            .replace("Block{", "")
                            .replace("}", "");
                    cropConfigs.put(cropIdentifier, new CropConfig(map));
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
    }

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        if (cropConfigs.containsKey(cropIdentifier)) {
            return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
        }
        return 1.0f;
    }
}
