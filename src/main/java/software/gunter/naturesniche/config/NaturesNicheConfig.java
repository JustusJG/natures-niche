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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.*;

public class NaturesNicheConfig {
    private final GameRules.Key<DoubleRule> DEFAULT_GROWTH_TEMPERATURE =
            GameRuleRegistry.register("defaultGrowthTemperature", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.8, -2.5, 2.5));
    private final GameRules.Key<DoubleRule> DEFAULT_GROWTH_HUMIDITY =
            GameRuleRegistry.register("defaultGrowthHumidity", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.4, 0.0, 1.0));
    private final GameRules.Key<GameRules.BooleanRule> DEFAULT_GROWTH_PRECIPITATION =
            GameRuleRegistry.register("defaultGrowthPrecipitation", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    private final Map<String, Plant> plants = new HashMap<>();

    public float getModifier(BlockState state, ServerWorld world, BlockPos pos) {
        String plantIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();

        GameRules gameRules = world.getGameRules();
        float defaultTemperature = (float) gameRules.get(DEFAULT_GROWTH_TEMPERATURE).get();
        float defaultHumidity = (float) gameRules.get(DEFAULT_GROWTH_HUMIDITY).get();
        boolean defaultPrecipitation = gameRules.get(DEFAULT_GROWTH_PRECIPITATION).get();
        Plant defaultPlant = new Plant(new GrowthConditions(defaultTemperature, defaultHumidity, defaultPrecipitation, "-1 + 1 * e^(0.7 * x)"));

        Biome biome = world.getBiome(pos).value();
        Identifier biomeId = BuiltinRegistries.BIOME.getId(biome);
        return getPlants()
                .getOrDefault(plantIdentifier, defaultPlant).getGrowthConditions(String.valueOf(biomeId))
                .calculateGrowthModifier(biome.getTemperature(), biome.getDownfall(), !biome.getPrecipitation().equals(Biome.Precipitation.NONE));
    }


    public Map<String, Plant> getPlants() {
        return plants;
    }
}
