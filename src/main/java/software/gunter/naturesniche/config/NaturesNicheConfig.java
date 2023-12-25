package software.gunter.naturesniche.config;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.*;

public class NaturesNicheConfig {
    private final GrowthConditionsConfig defaultGrowthConditions = new GrowthConditionsConfig(0.5f, 0.5f, true, "-1 + 1 * e^(0.7 * x)");
    private final Map<String, PlantConfig> plants = new HashMap<>();

    public NaturesNicheConfig() {
        loadNewPlants();
    }

    public float getModifier(BlockState state, ServerWorld world, BlockPos pos) {
        String plantIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();
        if (plants.containsKey(plantIdentifier)) {
            return plants.get(plantIdentifier).getGrowthModifier(world, pos);
        }

        Biome biome = world.getBiome(pos).value();
        return defaultGrowthConditions.calculateGrowthModifier(biome.getTemperature(), biome.getDownfall(), !biome.getPrecipitation().equals(Biome.Precipitation.NONE));
    }

    public void loadNewPlants() {
        NaturesNicheUtil.getPlants().forEach(plant -> {
            String identifier = plant.toString()
                    .replace("Block{", "")
                    .replace("}", "");
            plants.putIfAbsent(identifier, new PlantConfig());
        });
    }

    public GrowthConditionsConfig getDefaultGrowthConditions() {
        return defaultGrowthConditions;
    }

    public Map<String, PlantConfig> getPlants() {
        return plants;
    }
}
