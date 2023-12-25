package software.gunter.naturesniche.utils;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class NaturesNicheUtil {
    public static List<String> getPlantIdentifiers() {
        List<String> l = new ArrayList<>();
        List<Block> plants = NaturesNicheUtil.getPlants();
        plants.forEach(plant -> {
            l.add(String.valueOf(Registry.BLOCK.getId(plant)));
        });
        return l;
    }

    public static List<Biome> getBiomes() {
        List<Biome> biomes = new ArrayList<>();

        BuiltinRegistries.BIOME.forEach(biomes::add);

        return biomes;
    }

    public static List<Block> getPlants() {
        List<Block> plants = new ArrayList<>();

        Registry.ITEM.forEach(item -> {
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (isPlant(block)) {
                    plants.add(block);
                }
            }
        });

        return plants;
    }

    public static boolean isPlant(Block block) {
        return block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof CocoaBlock
                || block instanceof SaplingBlock;
    }
}
