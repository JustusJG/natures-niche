package software.gunter.naturesniche.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import software.gunter.naturesniche.config.NaturesNicheConfig;
import software.gunter.naturesniche.NaturesNicheMod;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {
    public static final JsonParser JSON_PARSER = new JsonParser();
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
        File configFile = new File(String.valueOf(CONFIG_PATH));

        NaturesNicheMod.LOGGER.info("Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                NaturesNicheMod.LOGGER.info("No config file found, creating a new one...");
                String json = GSON.toJson(JSON_PARSER.parse(GSON.toJson(new NaturesNicheConfig())));
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                CONFIG = new NaturesNicheConfig();
                NaturesNicheMod.LOGGER.info("Successfully created default config file.");
            } else {
                NaturesNicheMod.LOGGER.info("A config file was found, loading it..");
                CONFIG = GSON.fromJson(new String(Files.readAllBytes(configFile.toPath())), NaturesNicheConfig.class);
                if(CONFIG == null) {
                    throw new NullPointerException("The config file was empty.");
                }else{
                    NaturesNicheMod.LOGGER.info("Successfully loaded config file.");
                }
            }
        }catch (Exception exception) {
            NaturesNicheMod.LOGGER.error("There was an error creating/loading the config file!", exception);
            CONFIG = new NaturesNicheConfig();
            NaturesNicheMod.LOGGER.warn("Defaulting to original config.");
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
