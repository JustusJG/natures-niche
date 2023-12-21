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
                                    context.getSource().sendFeedback(new LiteralText("Konfiguration neugeladen."), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("updatecrops")
                                .executes(context -> {
                                    NaturesNicheMod.CONFIG.updateCrops();
                                    NaturesNicheMod.CONFIG_MANAGER.saveConfig();
                                    context.getSource().sendFeedback(new LiteralText("Fehlende Crops der Konfiguration hinzugefügt."), false);
                                    return 1;
                                })
                        )
                ));
    }
}
