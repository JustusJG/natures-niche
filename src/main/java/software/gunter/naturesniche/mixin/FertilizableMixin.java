package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
            if (multiplier < 1.0f) {
                while (multiplier > 0f) {
                    float rand = random.nextFloat();
                    if (multiplier >= rand) {
                        $shouldInject = false;
                        this.randomTick(state, world, pos, random);
                        multiplier -= 1f;
                    }
                }
                $shouldInject = true;
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        String cropIdentifier = Registry.BLOCK.getId(state.getBlock()).toString();
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent() && $shouldInject) {
            String biomeIdentifier = biomeKeyOptional.get().getValue().toString();
            NaturesNicheMod.LOGGER.info(biomeIdentifier);
            float multiplier = NaturesNicheMod.CONFIG.getModifier(cropIdentifier, biomeIdentifier);
            NaturesNicheMod.LOGGER.info(String.valueOf(multiplier));
            if (multiplier < 1.0f) {
                while (multiplier > 0f) {
                    float rand = random.nextFloat();
                    if (multiplier >= rand) {
                        $shouldInject = false;
                        this.grow(world, random, pos, state);
                        multiplier -= 1f;
                    }
                }
                $shouldInject = true;
                ci.cancel();
            }
        }
    }
}