package software.gunter.naturesniche;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.mariuszgromada.math.mxparser.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.gunter.naturesniche.commands.NaturesNicheCommand;
import software.gunter.naturesniche.config.GrowthConditions;
import software.gunter.naturesniche.config.NaturesNicheConfig;
import software.gunter.naturesniche.config.Plant;
import software.gunter.naturesniche.utils.ConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NaturesNicheMod implements ModInitializer {
	public static String MOD_ID = "natures-niche";
	public static final Logger LOGGER = LoggerFactory.getLogger("Natures Niche");

	public static ConfigManager CONFIG_MANAGER;
	public static NaturesNicheConfig CONFIG;

	@Override
	public void onInitialize() {
		License.iConfirmNonCommercialUse("gunter.software");
		LOGGER.info("Hello Fabric world!");
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> NaturesNicheCommand.register(dispatcher));

		CONFIG = new NaturesNicheConfig();
		CONFIG_MANAGER = new ConfigManager();
		CONFIG_MANAGER.loadConfig();
	}
}
