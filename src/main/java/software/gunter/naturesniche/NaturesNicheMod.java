package software.gunter.naturesniche;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.mariuszgromada.math.mxparser.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.gunter.naturesniche.commands.NaturesNicheCommand;
import software.gunter.naturesniche.config.NaturesNicheConfig;
import software.gunter.naturesniche.utils.ConfigManager;

public class NaturesNicheMod implements ModInitializer {
    public static String MOD_ID = "natures-niche";
    public static final Logger LOGGER = LoggerFactory.getLogger("Natures Niche");
    public static ConfigManager CONFIG_MANAGER;
    public static NaturesNicheConfig CONFIG;

    @Override
    public void onInitialize() {
        License.iConfirmNonCommercialUse("gunter.software");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> NaturesNicheCommand.register(dispatcher));

        CONFIG = new NaturesNicheConfig();
        CONFIG_MANAGER = new ConfigManager();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("natures-niche", "reload");
            }

            @Override
            public void reload(ResourceManager manager) {
                CONFIG_MANAGER.loadConfig(manager);
            }
        });
    }
}
