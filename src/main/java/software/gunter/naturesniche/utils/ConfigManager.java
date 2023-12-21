package software.gunter.naturesniche.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import software.gunter.naturesniche.NaturesNicheConfig;
import software.gunter.naturesniche.NaturesNicheMod;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(NaturesNicheMod.MOD_ID + ".json");

    private static NaturesNicheConfig CONFIG;

    public NaturesNicheConfig getConfig() {
        return CONFIG;
    }

    public ConfigManager() {
        NaturesNicheMod.LOGGER.info("Initialize config");
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                copyDefaultConfig();
            }
            CONFIG = GSON.fromJson(Files.newBufferedReader(CONFIG_PATH), NaturesNicheConfig.class);
            if (CONFIG == null) {
                throw new NullPointerException("The config file was empty.");
            }
        } catch (IOException e) {
            NaturesNicheMod.LOGGER.error("Failed to load config: ", e);
            CONFIG = new NaturesNicheConfig(); // Default configuration
        } catch (NullPointerException e) {
            NaturesNicheMod.LOGGER.error("There was an error creating/loading the config file!", e);
        }
    }

    private void copyDefaultConfig() throws IOException {
        String DEFAULT_CONFIG_RESOURCE_PATH = "/assets/natures-niche/config.json";
        try (InputStream defaultConfigStream = ConfigManager.class.getResourceAsStream(DEFAULT_CONFIG_RESOURCE_PATH)) {
            if (defaultConfigStream == null) {
                throw new FileNotFoundException("Default config file not found in resources.");
            }
            Files.copy(defaultConfigStream, CONFIG_PATH);
        }
    }

    public void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(CONFIG, writer);
        } catch (IOException e) {
            NaturesNicheMod.LOGGER.error("Failed to save config: ", e);
        }
    }
}
