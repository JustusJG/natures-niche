package software.gunter.naturesniche.config;

import java.util.HashMap;
import java.util.Map;

public class Plant {
    private final GrowthConditions growthConditions;
    private final Map<String, GrowthConditions> biomeSpecificGrowthConditions;

    public Plant(GrowthConditions growthConditions) {
        this.growthConditions = growthConditions;
        this.biomeSpecificGrowthConditions = new HashMap<>();
    }

    public GrowthConditions getGrowthConditions(String biome) {
        return biomeSpecificGrowthConditions.getOrDefault(biome, growthConditions);
    }

    public Map<String, GrowthConditions> getBiomeSpecificGrowthConditions() {
        return biomeSpecificGrowthConditions;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "growthConditions=" + growthConditions +
                ", biomeSpecificGrowthConditions=" + biomeSpecificGrowthConditions +
                '}';
    }
}
