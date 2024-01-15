package software.gunter.naturesniche.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.config.BiomeConfig;

import java.util.logging.Logger;

@Mixin(Biome.class)
public class BiomeMixin {
    @Shadow
    @Final
    private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(1234L)), ImmutableList.of(Integer.valueOf(0)));

    @Inject(at = @At("RETURN"), method = "getTemperature()F", cancellable = true)
    private void getTemperatureInject(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(2.0f);
        Biome thisBiome = (Biome) (Object) this;
        Identifier biomeId = BuiltinRegistries.BIOME.getId(thisBiome);
        if (biomeId == null) {
            return;
        }
        BiomeConfig nnBiome = NaturesNicheMod.CONFIG.getBiomes().get(biomeId.toString());
        if (nnBiome != null) {
            cir.setReturnValue(nnBiome.getTemperature());
        }
    }

    @Inject(at = @At("RETURN"), method = "getDownfall()F", cancellable = true)
    private void getDownfallInject(CallbackInfoReturnable<Float> cir) {
        Biome thisBiome = (Biome) (Object) this;
        Identifier biomeId = BuiltinRegistries.BIOME.getId(thisBiome);
        if (biomeId == null) {
            return;
        }
        BiomeConfig nnBiome = NaturesNicheMod.CONFIG.getBiomes().get(biomeId.toString());
        if (nnBiome != null) {
            cir.setReturnValue(nnBiome.getHumidity());
        }
    }

    @Inject(at = @At("RETURN"), method = "getPrecipitation", cancellable = true)
    private void getPrecipitationInject(CallbackInfoReturnable<Biome.Precipitation> cir) {
        Biome thisBiome = (Biome) (Object) this;
        Identifier biomeId = BuiltinRegistries.BIOME.getId(thisBiome);
        if (biomeId == null) {
            return;
        }
        BiomeConfig nnBiome = NaturesNicheMod.CONFIG.getBiomes().get(biomeId.toString());
        if (nnBiome != null) {
            cir.setReturnValue(nnBiome.getPrecipitation());
        }
    }

    @Inject(at = @At("RETURN"), method = "computeTemperature(Lnet/minecraft/util/math/BlockPos;)F", cancellable = true)
    private void computeTemperatureInject(BlockPos pos, CallbackInfoReturnable<Float> cir) {
        try {
            cir.setReturnValue(computeTemperature(pos));
        } catch (Exception e) {
            // NaturesNicheMod.LOGGER.error(e.toString());
        }
    }

    @Unique
    private float computeTemperature(BlockPos pos) throws Exception {
        Biome thisBiome = (Biome) (Object) this;
        Identifier biomeId = BuiltinRegistries.BIOME.getId(thisBiome);
        if (biomeId == null) {
            throw new Exception("Biome not found");
        }
        BiomeConfig nnBiome = NaturesNicheMod.CONFIG.getBiomes().get(biomeId.toString());
        if (nnBiome != null) {
            float t = thisBiome.getTemperature();
            float f = nnBiome.getTemperatureModifier().getModifiedTemperature(pos, t);
            if (pos.getY() > 80) {
                float g = (float) (BiomeAccessor.getTemperatureNoise().sample((float) pos.getX() / 8.0f, (float) pos.getZ() / 8.0f, false) * 8.0);
                return f - (g + (float) pos.getY() - 80.0f) * 0.05f / 40.0f;
            }
            return f;
        }
        throw new Exception("Biome not in config");
    }

    @Inject(at = @At(value = "RETURN"), method = "getTemperature(Lnet/minecraft/util/math/BlockPos;)F", cancellable = true)
    private void getTemperatureInject(BlockPos blockPos, CallbackInfoReturnable<Float> cir) {

        long l = blockPos.asLong();

        NaturesNicheMod.TEMPERATURE_CACHE.get().clear();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = NaturesNicheMod.TEMPERATURE_CACHE.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            cir.setReturnValue(f);
        }
        float g;
        try {
            g = computeTemperature(blockPos);
        } catch (Exception ignored) {
            return;
        }
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, g);
        cir.setReturnValue(g);

    }
}
