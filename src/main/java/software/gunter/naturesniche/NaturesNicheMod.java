package software.gunter.naturesniche;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.gunter.naturesniche.utils.ConfigManager;

public class NaturesNicheMod implements ModInitializer {
	public static String MOD_ID = "natures-niche";
	public static final Logger LOGGER = LoggerFactory.getLogger("Natures Niche");

	public static ConfigManager CONFIG_MANAGER;
	public static NaturesNicheConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		for (Biome biome : BuiltinRegistries.BIOME) {
			LOGGER.info(BuiltinRegistries.BIOME.getId(biome).toString());
		}

		Registry.ITEM.forEach(item -> {
			if(item instanceof BlockItem) {
				Block block = ((BlockItem) item).getBlock();
				if(block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof SaplingBlock) {
					LOGGER.info(((BlockItem) item).getBlock().toString());
				}
			}
		});

		initConfig();
	}

	public static void initConfig() {
		CONFIG_MANAGER = new ConfigManager();
		CONFIG = CONFIG_MANAGER.getConfig();
	}
}
