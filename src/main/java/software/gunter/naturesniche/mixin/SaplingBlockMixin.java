package software.gunter.naturesniche.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.gunter.naturesniche.NaturesNicheMod;

import java.util.Random;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin extends Block implements Fertilizable {

    public SaplingBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        float multiplier = NaturesNicheMod.CONFIG.getModifier(state, world, pos) * 2;
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt((int) (7 / multiplier)) == 0) {
            this.generate(world, pos, state, random);
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "canGrow", cancellable = true)
    public void canGrowInject(World world, Random random, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        float multiplier = NaturesNicheMod.CONFIG.getModifier(state, (ServerWorld) world, pos);
        cir.setReturnValue((double)world.random.nextFloat() < 0.45 * multiplier);
        cir.cancel();
    }
}
