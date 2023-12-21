package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
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
                                .then(CommandManager.literal("update")
                                        .executes(context -> {
                                            NaturesNicheMod.CONFIG.updateCrops();
                                            NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                            context.getSource().sendFeedback(new LiteralText("Fehlende Konfiguationen den Crops hinzugefügt."), true);
                                            return 1;
                                        })
                                        .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                                .executes(context -> {
                                                    String identifier = IdentifierArgumentType.getIdentifier(context, "identifier").toString();
                                                    NaturesNicheMod.CONFIG.updateCrop(identifier);
                                                    NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                                    context.getSource().sendFeedback(new LiteralText("Fehlende Konfigurationen dem Crop " + identifier + " hinzugefügt."), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )

                        .then(CommandManager.literal("biome")
                                .executes(context -> {
                                    NaturesNicheMod.CONFIG.updateBiomes();
                                    NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                    context.getSource().sendFeedback(new LiteralText("Fehlende Konfiguationen den Biomen hinzugefügt."), true);
                                    return 1;
                                })
                                .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                        .executes(context -> {
                                            String identifier = IdentifierArgumentType.getIdentifier(context, "identifier").toString();
                                            NaturesNicheMod.CONFIG.updateBiome(identifier);
                                            NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                            context.getSource().sendFeedback(new LiteralText("Fehlende Konfigurationen dem Biome " + identifier + " hinzugefügt."), true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
