package software.gunter.naturesniche.utils;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtil {
    public static List<Biome> getBiomes() {
        List<Biome> biomes = new ArrayList<>();

        BuiltinRegistries.BIOME.forEach(biomes::add);

        return biomes;
    }

    public static List<Block> getCrops() {
        List<Block> crops = new ArrayList<>();

        Registry.ITEM.forEach(item -> {
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof SaplingBlock) {
                    crops.add(block);
                }
            }
        });

        return crops;
    }
}
