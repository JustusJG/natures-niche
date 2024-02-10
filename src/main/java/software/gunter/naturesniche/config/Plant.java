package software.gunter.naturesniche.config;

import java.util.HashMap;
import java.util.Map;

public class Plant {
    private final GrowthConditions growthConditions;
    private final Map<String, GrowthConditions> biomeSpecificGrowthConditions = new HashMap<>();

    public Plant(GrowthConditions growthConditions) {
        this.growthConditions = growthConditions;
    }

    public GrowthConditions getGrowthConditions(String biome) {
        return getBiomeSpecificGrowthConditions().getOrDefault(biome, growthConditions);
    }

    public Map<String, GrowthConditions> getBiomeSpecificGrowthConditions() {
        if (biomeSpecificGrowthConditions == null) {
            return new HashMap<String, GrowthConditions>();
        }
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
