package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.gunter.naturesniche.NaturesNicheMod;

import java.util.Optional;
import java.util.Random;

@Mixin({CocoaBlock.class, StemBlock.class, SaplingBlock.class})
public abstract class FertilizableMixin extends Block implements Fertilizable {
    @Unique
    private boolean $shouldInject = true;

    public FertilizableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent() && $shouldInject) {
            float multiplier = NaturesNicheMod.CONFIG.getModifier(state, world, pos) * 2;

            if (multiplier <= 0.0f) {
                ci.cancel();
            } else if (multiplier < 1.0f) {
                if (random.nextFloat() >= multiplier) {
                    ci.cancel();
                }
            } else if (multiplier > 1.0f) {
                int extraTicks = (int) ((multiplier - 1.0f) * 5);
                for (int i = 0; i < extraTicks; i++) {
                    $shouldInject = false;
                    this.randomTick(state, world, pos, random);
                }
            }
        }
        $shouldInject = true;
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent() && $shouldInject) {
            float multiplier = NaturesNicheMod.CONFIG.getModifier(state, world, pos);

            if (multiplier < 1.0f) {
                if (random.nextFloat() >= multiplier) {
                    ci.cancel();
                }
            }
        }
    }
}