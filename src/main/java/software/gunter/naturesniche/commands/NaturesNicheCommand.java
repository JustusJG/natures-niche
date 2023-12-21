package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import software.gunter.naturesniche.NaturesNicheMod;

public class NaturesNicheCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("naturesniche").requires((source) -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("reload")
                                .executes(context -> {
                                    NaturesNicheMod.initConfig();
                                    context.getSource().sendFeedback(new LiteralText("Konfiguration neugeladen."), true);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("crop")
                                .then(CommandManager.literal("update").executes(context -> {
                                            NaturesNicheMod.CONFIG.updateCrops();
                                            NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                            context.getSource().sendFeedback(new LiteralText("Fehlende Crops der Konfiguration hinzugefügt."), true);
                                            return 1;
                                        })
                                )
                        )

                        .then(CommandManager.literal("biome")
                                .then(CommandManager.literal("update").executes(context -> {
                                            NaturesNicheMod.CONFIG.updateBiomes();
                                            NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                            context.getSource().sendFeedback(new LiteralText("Fehlende Biomes der Konfiguration hinzugefügt."), true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
