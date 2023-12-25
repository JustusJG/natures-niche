package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import javax.annotation.Nullable;
import java.util.List;

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

    private static int updateConfig(CommandContext<ServerCommandSource> context, String type, @Nullable String identifier) {
        if (type.equals("crop")) {
        } else if (type.equals("biome")) {
        }

        NaturesNicheMod.CONFIG_MANAGER.saveConfig();
        String feedbackMessage = "Fehlende Konfigurationen " + (identifier == null ? "hinzugefügt." : "dem " + type + " " + identifier + " hinzugefügt.");
        context.getSource().sendFeedback(new LiteralText(feedbackMessage), true);

        return 1;
    }
}
