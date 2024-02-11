package software.gunter.naturesniche.config;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.*;

public class NaturesNicheConfig {
    private final GameRules.Key<DoubleRule> DEFAULT_DELTA_MAX =
        GameRuleRegistry.register("nnDeltaMax", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(4));

    private final GameRules.Key<DoubleRule> DEFAULT_TEMPERATURE =
            GameRuleRegistry.register("nnTemperature", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.8));
    private final GameRules.Key<DoubleRule> DEFAULT_HUMIDITY =
            GameRuleRegistry.register("nnHumidity", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.4));
    private final GameRules.Key<GameRules.BooleanRule> DEFAULT_PRECIPITATION =
            GameRuleRegistry.register("nnPrecipitation", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    private final Map<String, Plant> plants = new HashMap<>();

    public float getModifier(BlockState state, ServerWorld world, BlockPos pos) {
        String plantIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();

        GameRules gameRules = world.getGameRules();
        float defaultDeltaMax = (float) gameRules.get(DEFAULT_DELTA_MAX).get();
        float defaultTemperature = (float) gameRules.get(DEFAULT_TEMPERATURE).get();
        float defaultHumidity = (float) gameRules.get(DEFAULT_HUMIDITY).get();
        boolean defaultPrecipitation = gameRules.get(DEFAULT_PRECIPITATION).get();
        Plant defaultPlant = new Plant(new GrowthConditions(defaultDeltaMax, defaultTemperature, defaultHumidity, defaultPrecipitation));

        RegistryEntry<Biome> biomeRegistryEntry = world.getBiome(pos);
        Optional<RegistryKey<Biome>> biomeRegistryKey = biomeRegistryEntry.getKey();
        Biome biome;
        if (biomeRegistryKey.isPresent()) {
            biome = BuiltinRegistries.BIOME.get(biomeRegistryKey.get());
        } else {
            biome = biomeRegistryEntry.value();
        }

        Identifier biomeId = BuiltinRegistries.BIOME.getId(biome);
        Plant plant = getPlants().getOrDefault(plantIdentifier, defaultPlant);
        assert biome != null;
        return plant.getGrowthConditions(String.valueOf(biomeId))
                .computeClimateDelta(biome.getTemperature(), biome.getDownfall(), NaturesNicheUtil.precipitates(world, pos));
    }

    public Map<String, Plant> getPlants() {
        return plants;
    }
}
