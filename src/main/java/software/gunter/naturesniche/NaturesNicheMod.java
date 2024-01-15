package software.gunter.naturesniche;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.mariuszgromada.math.mxparser.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.gunter.naturesniche.commands.NaturesNicheCommand;
import software.gunter.naturesniche.config.BiomeConfig;
import software.gunter.naturesniche.config.GrowthConditions;
import software.gunter.naturesniche.config.NaturesNicheConfig;
import software.gunter.naturesniche.config.Plant;
import software.gunter.naturesniche.utils.ConfigManager;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NaturesNicheMod implements ModInitializer {
    public static String MOD_ID = "natures-niche";
    public static final Logger LOGGER = LoggerFactory.getLogger("Natures Niche");

    public static final ThreadLocal<Long2FloatLinkedOpenHashMap> TEMPERATURE_CACHE = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            @Override
            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));
    public static ConfigManager CONFIG_MANAGER;
    public static NaturesNicheConfig CONFIG;

    @Override
    public void onInitialize() {
        License.iConfirmNonCommercialUse("gunter.software");
        LOGGER.info("Hello Fabric world!");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> NaturesNicheCommand.register(dispatcher));

        CONFIG = new NaturesNicheConfig();
        CONFIG_MANAGER = new ConfigManager();

        ServerWorldEvents.LOAD.register((server, world) -> {
            System.out.println(server);
            System.out.println(world);
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("natures-niche", "reload");
            }

            @Override
            public void reload(ResourceManager manager) {
                CONFIG_MANAGER.loadConfig(manager);
                TEMPERATURE_CACHE.get().clear();
            }
        });
    }
}
