package software.gunter.naturesniche.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.SuperMath;

import java.util.Optional;
import java.util.Random;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends Block implements Fertilizable {
    @Unique
    private boolean $shouldInject = true;
    @Unique
    private float growthThreshold = 25.0f;

    @Shadow
    @Final
    public static IntProperty AGE;

    @Shadow
    public abstract int getMaxAge();

    @Shadow
    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        return 0;
    }

    @Shadow
    public abstract BlockState withAge(int age);

    @Shadow public abstract IntProperty getAgeProperty();

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        float multiplierValue = Math.abs(NaturesNicheMod.CONFIG.getModifier(state, world, pos));
        float multiplier = (float) SuperMath.calculateAsymptoticFunctionValue(multiplierValue, 2.5, -2.5, 0.511);

        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int i = state.get(getAgeProperty());
            if (i < getMaxAge()) {
                float f = getAvailableMoisture(this, world, pos);
                f *= multiplier;
                int chance = random.nextInt((int) (growthThreshold / f) + 1);
                if (chance == 0) {
                    world.setBlockState(pos, withAge(i + 1), 2); // grow
                }
            }
        }
        ci.cancel();
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
