package software.gunter.naturesniche.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlantConfig {
    private final GrowthConditionsConfig growthConditions = new GrowthConditionsConfig(0.5f, 0.5f, true);
    @SerializedName("biomeSpecific")
    private Map<String, GrowthConditionsConfig> biomes;

    public boolean canGrow(ServerWorld world, BlockPos pos) {
        return getGrowthModifier(world, pos) > 0.0f;
    }

    public float getGrowthModifier(ServerWorld world, BlockPos pos) {
        Biome biome = world.getBiome(pos).value();
        return getGrowthConditions(world, pos).calculateGrowthModifier(biome.getTemperature(), biome.getDownfall(), !biome.getPrecipitation().equals(Biome.Precipitation.NONE));
    }

    public GrowthConditionsConfig getGrowthConditions(ServerWorld world, BlockPos pos) {
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();
        if (biomeKeyOptional.isPresent()) {
            String identifier = biomeKeyOptional.get().getValue().toString();
            return getBiomes().getOrDefault(identifier, getGrowthConditions());
        }
        return getGrowthConditions();
    }

    public GrowthConditionsConfig getGrowthConditions() {
        return growthConditions;
    }

    public Map<String, GrowthConditionsConfig> getBiomes() {
        if (biomes == null) {
            biomes = new HashMap<>();
        }
        return biomes;
    }

    public void loadNewBiomes() {
        NaturesNicheUtil.getBiomes().forEach(biome -> {
            System.out.println(biome);
            String identifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));
            getBiomes().put(identifier, getGrowthConditions());
        });
    }
}
