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
                        .then(CommandManager.literal("biomelist")
                                .executes(context -> {
                                    NaturesNicheUtil.getBiomeIdentifiers().forEach(identifier -> {
                                        context.getSource().sendFeedback(Text.of(identifier), false);
                                    });
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("plantlist")
                                .executes(context -> {
                                    NaturesNicheUtil.getPlantIdentifiers().forEach(identifier -> {
                                        context.getSource().sendFeedback(Text.of(identifier), false);
                                    });
                                    return 1;
                                })
                        )
                )
        );
    }
}
