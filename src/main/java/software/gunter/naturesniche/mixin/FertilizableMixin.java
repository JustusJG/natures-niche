package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.gunter.naturesniche.NaturesNicheMod;

import java.util.Optional;
import java.util.Random;

@Mixin({CropBlock.class, CocoaBlock.class, StemBlock.class, SaplingBlock.class})
public abstract class FertilizableMixin extends Block implements Fertilizable {
    @Shadow public abstract void grow(ServerWorld world, Random random, BlockPos pos, BlockState state);

    public FertilizableMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent()) {
            Identifier biomeIdentifier = biomeKeyOptional.getValue();
            float multiplier = NaturesNicheMod.CONFIG.getModifier(cropIdentifier, biomeIdentifier);
            if (multiplier < 1.0f) {
                while (multiplier > 0f) {
                    float rand = random.nextFloat();
                    if (multiplier >= rand) {
                        this.randomTick(state, world, pos, random);
                        multiplier -= 1f;
                    }
                }
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        Identifier cropIdentifier = Registry.BLOCK.getId(state.getBlock());
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent()) {
            Identifier biomeIdentifier = biomeKeyOptional.getValue();
            float multiplier = NaturesNicheMod.CONFIG.getModifier(cropIdentifier, biomeIdentifier);
            if (multiplier < 1.0f) {
                while (multiplier > 0f) {
                    float rand = random.nextFloat();
                    if (multiplier >= rand) {
                        this.grow(world, random, pos, state);
                        multiplier -= 1f;
                    }
                }
                ci.cancel();
            }
        }
    }
}