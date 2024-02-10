package software.gunter.naturesniche.utils;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaturesNicheUtil {
    public static final Map<Identifier, Biome> BIOMES = new HashMap<>();
    public static List<String> getBiomeIdentifiers() {
        List<String> l = new ArrayList<>();
        List<Biome> biomes = NaturesNicheUtil.getBiomes();
        biomes.forEach(biome -> {
            l.add(String.valueOf(BuiltinRegistries.BIOME.getId(biome)));
        });
        return l;
    }

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

    public static boolean precipitates(World world, BlockPos blockPos) {
            if (!world.isRaining()) {
                return false;
            }
            if (!world.isSkyVisible(blockPos)) {
                return false;
            }
            if (world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
                return false;
            }
            return true;
    }

    public static boolean rains(World world, BlockPos blockPos) {
        return world.hasRain(blockPos);
    }

    public static boolean snows(World world, BlockPos blockPos) {
        if (!precipitates(world, blockPos)) {
            return false;
        }
        Biome biome = world.getBiome(blockPos).value();
        return biome.getPrecipitation() == Biome.Precipitation.SNOW && !biome.doesNotSnow(blockPos);
    }
}
