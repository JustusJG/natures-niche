package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import software.gunter.naturesniche.NaturesNicheMod;

import javax.annotation.Nullable;

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
                                .then(CommandManager.literal("init")
                                        .executes(context -> updateConfig(context, "crop", null))
                                        .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                                .executes(context -> {
                                                    String identifier = IdentifierArgumentType.getIdentifier(context, "identifier").toString();
                                                    return updateConfig(context, "crop", identifier);
                                                })
                                        ))
                        )

                        .then(CommandManager.literal("biome")
                                .then(CommandManager.literal("init")
                                        .executes(context -> updateConfig(context, "biome", null))
                                        .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                                .executes(context -> {
                                                    String identifier = IdentifierArgumentType.getIdentifier(context, "identifier").toString();
                                                    return updateConfig(context, "biome", identifier);
                                                })
                                        ))
                        )
                )
        );
    }

    private static int updateConfig(CommandContext<ServerCommandSource> context, String type, @Nullable String identifier) {
        if (type.equals("crop")) {
            if (identifier == null) {
                NaturesNicheMod.CONFIG.updateCrops();
            } else {
                NaturesNicheMod.CONFIG.updateCrop(identifier);
            }
        } else if (type.equals("biome")) {
            if (identifier == null) {
                NaturesNicheMod.CONFIG.updateBiomes();
            } else {
                NaturesNicheMod.CONFIG.updateBiome(identifier);
            }
        }

        NaturesNicheMod.CONFIG_MANAGER.saveConfig();
        String feedbackMessage = "Fehlende Konfigurationen " + (identifier == null ? "hinzugefügt." : "dem " + type + " " + identifier + " hinzugefügt.");
        context.getSource().sendFeedback(new LiteralText(feedbackMessage), true);

        return 1;
    }
}
