package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

@Mixin({CropBlock.class, CocoaBlock.class, StemBlock.class, SaplingBlock.class})
public abstract class FertilizableMixin extends Block implements Fertilizable {
    @Unique
    private boolean $shouldInject = true;

    public FertilizableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        String cropIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent() && $shouldInject) {
            String biomeIdentifier = biomeKeyOptional.get().getValue().toString();
            float multiplier = NaturesNicheMod.CONFIG.getModifier(cropIdentifier, biomeIdentifier);

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
        String cropIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent() && $shouldInject) {
            String biomeIdentifier = biomeKeyOptional.get().getValue().toString();
            float multiplier = NaturesNicheMod.CONFIG.getModifier(cropIdentifier, biomeIdentifier);

            if (multiplier < 1.0f) {
                if (random.nextFloat() >= multiplier) {
                    ci.cancel();
                }
            }
        }
    }
}