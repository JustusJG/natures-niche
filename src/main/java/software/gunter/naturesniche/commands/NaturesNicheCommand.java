package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

public class NaturesNicheCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("naturesniche").requires((source) -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("debug")
                        .then(CommandManager.literal("plantlist")
                                .executes(context -> {
                                    NaturesNicheUtil.getPlantIdentifiers().forEach(identifier -> {
                                        context.getSource().sendFeedback(Text.of(identifier), false);
                                    });
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("fullconfig")
                                .executes(context -> {
                                    NaturesNicheMod.loadConfig();
                                    NaturesNicheMod.CONFIG.loadNewPlants();
                                    NaturesNicheMod.CONFIG.getPlants().forEach((identifier, plant) -> {
                                        plant.loadNewBiomes();
                                    });
                                    NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("reload")
                        .executes(context -> {
                            NaturesNicheMod.loadConfig();
                            context.getSource().sendFeedback(new LiteralText("NatureNiche reloaded"), true);
                            return 1;
                        })
                )
        );
    }
}
