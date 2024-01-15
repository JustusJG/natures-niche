package software.gunter.naturesniche.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.config.BiomeConfig;
import software.gunter.naturesniche.config.GrowthConditions;
import software.gunter.naturesniche.config.NaturesNicheConfig;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.config.Plant;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final Gson GSON;

    public ConfigManager() {
        GSON = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, GrowthConditions>>() {
                }.getType(), new ConfigManager.BiomeSpecificGrowthConditionsDeserializer())
                .create();
    }

    public void loadConfig(ResourceManager manager) {
        // Clear Caches Here
        NaturesNicheMod.CONFIG.getPlants().clear();

        for (Identifier id : manager.findResources("plants", path -> path.endsWith(".json"))) {
            try (InputStream stream = manager.getResource(id).getInputStream()) {
                JsonReader reader = new JsonReader(new InputStreamReader(stream));
                JsonElement element = JsonParser.parseReader(reader);

                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();

                    String plantId;
                    if (jsonObject.has("id")) {
                        // Extrahieren der ID aus dem JSON-Objekt, falls vorhanden
                        plantId = jsonObject.get("id").getAsString();
                    } else {
                        // Extrahieren der ID aus dem Dateipfad, falls keine ID im JSON-Objekt vorhanden ist
                        String fullPath = id.getPath();
                        plantId = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.lastIndexOf('.'));
                    }

                    Plant plant = GSON.fromJson(jsonObject, Plant.class);
                    NaturesNicheMod.CONFIG.getPlants().put(plantId, plant);
                }
            } catch (Exception e) {
                NaturesNicheMod.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
        }

        for (Identifier id : manager.findResources("biomes", path -> path.endsWith(".json"))) {
            try (InputStream stream = manager.getResource(id).getInputStream()) {
                JsonReader reader = new JsonReader(new InputStreamReader(stream));
                JsonElement element = JsonParser.parseReader(reader);

                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();

                    String biomeId;
                    if (jsonObject.has("id")) {
                        biomeId = jsonObject.get("id").getAsString();
                    } else {
                        String fullPath = id.getPath();
                        biomeId = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.lastIndexOf('.'));
                    }

                    Identifier biomeIdentifier = Identifier.tryParse(biomeId);
                    JsonObject climateObject = jsonObject.get("climate").getAsJsonObject();
                    BiomeConfig nnBiome = new BiomeConfig(climateObject.get("temperature").getAsFloat(), climateObject.get("humidity").getAsFloat(), climateObject.get("precipitation").getAsBoolean(), Biome.TemperatureModifier.byName(climateObject.get("temperatureModifier").getAsString()));
                    assert biomeIdentifier != null;
                    NaturesNicheMod.CONFIG.getBiomes().put(String.valueOf(biomeIdentifier), nnBiome);
                }
            } catch (Exception e) {
                NaturesNicheMod.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
        }

        NaturesNicheMod.CONFIG.getPlants().forEach((key, value) -> {
            NaturesNicheMod.LOGGER.info(key + " : " + value.toString());
        });
    }

    protected static class BiomeSpecificGrowthConditionsDeserializer implements JsonDeserializer<Map<String, GrowthConditions>> {
        @Override
        public Map<String, GrowthConditions> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, GrowthConditions> biomeSpecificMap = new HashMap<>();
            JsonArray biomeSpecificArray = json.getAsJsonArray();

            for (JsonElement element : biomeSpecificArray) {
                JsonObject biomeObject = element.getAsJsonObject();
                String id = biomeObject.get("id").getAsString();

                GrowthConditions conditions = new GrowthConditions(
                        biomeObject.get("temperature").getAsFloat(),
                        biomeObject.get("humidity").getAsFloat(),
                        biomeObject.get("precipitation").getAsBoolean(),
                        biomeObject.has("fx") ? biomeObject.get("fx").getAsString() : null
                );

                biomeSpecificMap.put(id, conditions);
            }
            return biomeSpecificMap;
        }
    }
}
