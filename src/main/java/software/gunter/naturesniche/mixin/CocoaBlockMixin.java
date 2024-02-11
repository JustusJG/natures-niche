package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.gunter.naturesniche.NaturesNicheMod;

import java.util.Optional;
import java.util.Random;

@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin extends HorizontalFacingBlock implements Fertilizable {
    @Shadow
    @Final
    public static IntProperty AGE;

    @Shadow @Final public static int MAX_AGE;

    public CocoaBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        float multiplier = Math.abs(NaturesNicheMod.CONFIG.getModifier(state, world, pos));

        int i;
        if (world.random.nextInt((int) (5 / multiplier)) == 0 && (i = state.get(AGE)) < 2) {
            world.setBlockState(pos, state.with(AGE, i + 1), Block.NOTIFY_LISTENERS);
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        float multiplier = Math.abs(NaturesNicheMod.CONFIG.getModifier(state, world, pos));
        if (multiplier < 1.0f) {
            if (random.nextFloat() >= multiplier) {
                ci.cancel();
            }
        } else if (multiplier > 1.0f) {
            if (random.nextFloat() < multiplier - 1) {
                world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), Block.NOTIFY_LISTENERS);
            }
        }
    }
}
