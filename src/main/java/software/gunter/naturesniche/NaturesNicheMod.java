package software.gunter.naturesniche;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.gunter.naturesniche.commands.NaturesNicheCommand;
import software.gunter.naturesniche.utils.ConfigManager;

public class NaturesNicheMod implements ModInitializer {
	public static String MOD_ID = "natures-niche";
	public static final Logger LOGGER = LoggerFactory.getLogger("Natures Niche");

	public static ConfigManager CONFIG_MANAGER;
	public static NaturesNicheConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> NaturesNicheCommand.register(dispatcher));
		loadConfig();
	}

	public static void loadConfig() {
		CONFIG_MANAGER = new ConfigManager();
		CONFIG = CONFIG_MANAGER.getConfig();
	}
}
