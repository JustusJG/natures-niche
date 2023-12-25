package software.gunter.naturesniche.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
            GrowthConditionsConfig tmpGrowthConditions = new GrowthConditionsConfig(NaturesNicheMod.CONFIG.getDefaultGrowthConditions());
            if (tmpGrowthConditions.getHumidity() != growthConditions.getHumidity() && !Float.isNaN(growthConditions.getHumidity())) {
                tmpGrowthConditions.setHumidity(growthConditions.getHumidity());
            }
            if (tmpGrowthConditions.getTemperature() != growthConditions.getTemperature() && !Float.isNaN(growthConditions.getTemperature())) {
                tmpGrowthConditions.setTemperature(growthConditions.getTemperature());
            }
            if (tmpGrowthConditions.isPrecipitation() != growthConditions.isPrecipitation()) {
                tmpGrowthConditions.setPrecipitation(growthConditions.isPrecipitation());
            }
            if (!Objects.equals(tmpGrowthConditions.getFx(), growthConditions.getFx()) && !StringUtils.isBlank(growthConditions.getFx())) {
                tmpGrowthConditions.setFx(growthConditions.getFx());
            }
            return tmpGrowthConditions;
        }
        return getGrowthConditions();
    }

    public GrowthConditionsConfig getGrowthConditions() {
        GrowthConditionsConfig growthConditions = NaturesNicheMod.CONFIG.getDefaultGrowthConditions();
        if (this.growthConditions.getHumidity() != growthConditions.getHumidity()) {
            this.growthConditions.setHumidity(growthConditions.getHumidity());
        }
        if (this.growthConditions.getTemperature() != growthConditions.getTemperature()) {
            this.growthConditions.setTemperature(growthConditions.getTemperature());
        }
        if (this.growthConditions.isPrecipitation() != growthConditions.isPrecipitation()) {
            this.growthConditions.setPrecipitation(growthConditions.isPrecipitation());
        }
        if (!Objects.equals(this.growthConditions.getFx(), growthConditions.getFx())) {
            this.growthConditions.setFx(growthConditions.getFx());
        }
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
            String identifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));
            getBiomes().put(identifier, getGrowthConditions());
        });
    }
}
