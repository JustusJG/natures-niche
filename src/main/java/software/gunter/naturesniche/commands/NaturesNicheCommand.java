package software.gunter.naturesniche.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.NaturesNicheUtil;

import java.util.Optional;

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
                        .then(CommandManager.literal("climate")
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    try {
                                        World world = source.getWorld();
                                        BlockPos playerPos = new BlockPos(source.getPosition());

                                        String message = posClimateString(world, playerPos);
                                        context.getSource().sendFeedback(Text.of(message), false);
                                    } catch (Exception e) {
                                        source.sendError(new LiteralText("Fehler beim Ermitteln des Biomes."));
                                        NaturesNicheMod.LOGGER.error(e.toString());
                                        return 0; // Befehl fehlgeschlagen
                                    }
                                    return 1;
                                }).
                                then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                        .executes(context -> {
                                            Identifier identifier = IdentifierArgumentType.getIdentifier(context, "identifier");
                                            Biome biome = BuiltinRegistries.BIOME.get(identifier);

                                            if (biome == null) {
                                                String message = String.format("Biome-Details für '%s' konnten nicht gefunden werden.", identifier);
                                                context.getSource().sendFeedback(Text.of(message), false);
                                                return 1;
                                            }

                                            String message = biomeClimateString(biome, identifier);
                                            context.getSource().sendFeedback(Text.of(message), false);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private static String biomeClimateString(Biome biome, Identifier identifier) {
        float temperature = biome.getTemperature();
        float humidity = biome.getDownfall();
        Biome.Precipitation precipitation = biome.getPrecipitation();

        String precipitationText = precipitation == Biome.Precipitation.NONE ? "Kein" : precipitation.name();

        return String.format("Biome-Details für '%s':\n - Temperatur: %.2f\n - Luftfeuchtigkeit: %.2f\n - Niederschlag: %s",
                identifier, temperature, humidity, precipitationText);
    }

    private static String posClimateString(World world, BlockPos pos) {
        RegistryEntry<Biome> biomeReg = world.getBiome(pos);
        Biome biome;

        Identifier biomeId = biomeReg.getKey().get().getValue();
        biome = BuiltinRegistries.BIOME.get(biomeId);

        if (biome == null) {
            return String.format("Biome-Details für '%s' konnten nicht gefunden werden.", pos);
        }

        Optional<RegistryKey<Biome>> biomeKey = world.getBiome(pos).getKey();
        if (biomeKey.isEmpty()) {
            return String.format("Biome-Details für '%s' konnten nicht gefunden werden.", biome);
        }
        Identifier identifier = biomeKey.get().getValue();

        float temperature = biome.getTemperature();
        float humidity = biome.getDownfall();
        Biome.Precipitation precipitation = biome.getPrecipitation();

        String precipitationText = precipitation == Biome.Precipitation.NONE ? "Kein" : precipitation.name();

        return String.format("Biome-Details für '%s' an der Position '%s':\n- Temperatur: %.2f\n - Luftfeuchtigkeit: %.2f\n - Niederschlag: %s\n\n - Kalt: %b\n - Heiß: %b\n - Schneit nicht: %b\n - Untere gefrorene Ozeanoberfläche: %b\n - Kann Schnee setzen: %b",
                identifier, pos, temperature, humidity, precipitationText, biome.isCold(pos), biome.isHot(pos), biome.doesNotSnow(pos),
                biome.shouldGenerateLowerFrozenOceanSurface(pos), biome.canSetSnow(world, pos));
    }
}
