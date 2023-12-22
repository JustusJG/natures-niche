package software.gunter.naturesniche.config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.RegistryUtil;

import java.util.*;
import java.util.function.Function;

public class NaturesNicheConfig {
    private final GrowthConditionsConfig defaultGrowthConditions = new GrowthConditionsConfig(0.5f, 0.5f, true);
    private final Map<String, GrowthConditionsConfig> growthConditions = new HashMap<>();
    private final String modifierOrder = "cropFirst"; // Default is "cropFirst". Possible["cropFirst", "biomeFirst"]
    private final Map<String, ModifierConfig> crops = new HashMap<>();
    private final Map<String, ModifierConfig> biomes = new HashMap<>();

    public void updateGrowthCondition(String identifier) {
        NaturesNicheMod.LOGGER.info("Updating growthConditions for " + identifier + "...");
        growthConditions.putIfAbsent(identifier, defaultGrowthConditions);
        NaturesNicheMod.LOGGER.info("growthConditions for " + identifier + " updated.");
    }

    public void updateCrop(String identifier) {
        NaturesNicheMod.LOGGER.info("Updating crop " + identifier + "...");
        ModifierConfig cropConfig = crops.getOrDefault(identifier, new ModifierConfig());
        cropConfig.updateModifiers(RegistryUtil.getBiomes(), biome -> String.valueOf(BuiltinRegistries.BIOME.getId((Biome) biome)));
        crops.put(identifier, cropConfig);
        NaturesNicheMod.LOGGER.info("Crop " + identifier + " updated.");
    }

    public void updateBiome(String identifier) {
        NaturesNicheMod.LOGGER.info("Updating biome " + identifier + "...");
        ModifierConfig biomeConfig = biomes.getOrDefault(identifier, new ModifierConfig());
        biomeConfig.updateModifiers(RegistryUtil.getCrops(), crop -> String.valueOf(Registry.BLOCK.getId((Block) crop)));
        biomes.put(identifier, biomeConfig);
        NaturesNicheMod.LOGGER.info("Biome " + identifier + " updated.");
    }

    public void updateGrowthConditions() {
        RegistryUtil.getCrops().forEach(crop -> {
            String cropIdentifier = crop.toString()
                    .replace("Block{", "")
                    .replace("}", "");
            updateGrowthCondition(cropIdentifier);
        });
    }

    public void updateCrops() {
        RegistryUtil.getCrops().forEach(crop -> {
            String cropIdentifier = crop.toString()
                    .replace("Block{", "")
                    .replace("}", "");
            updateCrop(cropIdentifier);
        });
    }

    public void updateBiomes() {
        RegistryUtil.getBiomes().forEach(biome -> {
            String biomeIdentifier = String.valueOf(BuiltinRegistries.BIOME.getId(biome));
            updateBiome(biomeIdentifier);
        });
    }

    public float getModifier(BlockState state, ServerWorld world, BlockPos pos) {
        String cropIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();

        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();
        if (biomeKeyOptional.isPresent()) {
            String biomeIdentifier = biomeKeyOptional.get().getValue().toString();
            float modifier = getModifier(cropIdentifier, biomeIdentifier);
            if (modifier >= 0.0f) {
                return getModifier(cropIdentifier, biomeIdentifier);
            }
        }

        Biome biome = world.getBiome(pos).value();
        GrowthConditionsConfig conditions = growthConditions.get(cropIdentifier);
        if (conditions != null) {
            return growthConditions.get(cropIdentifier).calculateGrowthModifier(biome.getTemperature(), biome.getDownfall(), !biome.getPrecipitation().equals(Biome.Precipitation.NONE));
        }
        return defaultGrowthConditions.calculateGrowthModifier(biome.getTemperature(), biome.getDownfall(), !biome.getPrecipitation().equals(Biome.Precipitation.NONE));
    }

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        return getModifierBasedOnOrder(cropIdentifier, biomeIdentifier, "cropFirst".equals(modifierOrder));
    }

    private float getModifierBasedOnOrder(String cropIdentifier, String biomeIdentifier, boolean cropFirst) {
        float modifier = checkConfig(cropFirst ? crops : biomes, cropFirst ? cropIdentifier : biomeIdentifier, cropFirst ? biomeIdentifier : cropIdentifier);
        if (modifier != -1.0f) {
            return modifier;
        }
        return checkConfig(cropFirst ? biomes : crops, cropFirst ? biomeIdentifier : cropIdentifier, cropFirst ? cropIdentifier : biomeIdentifier);
    }

    private float checkConfig(Map<String, ModifierConfig> configs, String primaryIdentifier, String secondaryIdentifier) {
        ModifierConfig config = configs.get(primaryIdentifier);
        if (config != null) {
            return config.getModifier(secondaryIdentifier);
        }
        return -1.0f; // Rückgabewert, wenn kein Modifikator gefunden wird
    }

    public void update() {
        NaturesNicheMod.LOGGER.info("Updating config for newly added Biomes/Crops...");
        updateBiomes();
        updateCrops();
        updateGrowthConditions();
        NaturesNicheMod.LOGGER.info("Config updated.");
    }

    public String getModifierOrder() {
        return modifierOrder;
    }

    private static class ModifierConfig {
        private final Float defaultModifier = 1.0f;
        private final Map<String, Float> modifiers = new HashMap<>();

        public void updateModifiers(Collection<?> elements, Function<Object, String> identifierFunction) {
            elements.forEach(element -> modifiers.putIfAbsent(identifierFunction.apply(element), 1.0f));
        }

        public Float getDefaultModifier() {
            return defaultModifier;
        }

        public float getModifier(String identifier) {
            return modifiers.getOrDefault(identifier, defaultModifier);
        }

        public Map<String, Float> getModifierMap() {
            return modifiers;
        }
    }
}
