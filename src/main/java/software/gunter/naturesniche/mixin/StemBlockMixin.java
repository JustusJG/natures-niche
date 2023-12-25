package software.gunter.naturesniche.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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

@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends Block implements Fertilizable {
    @Unique
    private float growthThreshold = 25.0f;

    @Shadow
    @Final
    public static IntProperty AGE;

    @Unique
    private static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        float f = 1.0f;
        BlockPos blockPos = pos.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.isOf(Blocks.FARMLAND)) {
                    g = 1.0f;
                    if (blockState.get(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
        boolean bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl3 = world.getBlockState(blockPos4.north()).isOf(block) || world.getBlockState(blockPos5.north()).isOf(block);
            boolean bl4 = world.getBlockState(blockPos5.south()).isOf(block) || world.getBlockState(blockPos4.south()).isOf(block);
            if (bl3 && bl4) {
                f /= 2.0f;
            }
        }
        return f;
    }

    @Shadow
    public GourdBlock getGourdBlock() {
        return null;
    }

    public StemBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        Optional<RegistryKey<Biome>> biomeKeyOptional = world.getBiome(pos).getKey();

        if (biomeKeyOptional.isPresent()) {
            float multiplierValue = NaturesNicheMod.CONFIG.getModifier(state, world, pos);
            if (multiplierValue <= 0) {
                ci.cancel();
                return;
            }
            float multiplier = (float) SuperMath.calculateAsymptoticFunctionValue(multiplierValue, 2.5, -2.5, 0.511);
            if (world.getBaseLightLevel(pos, 0) >= 9) {
                float f = getAvailableMoisture(this, world, pos);
                f *= multiplier;
                int chance = random.nextInt((int) (growthThreshold / f) + 1);
                if (chance == 0) {
                    int i = state.get(AGE);
                    if (i < 7) {
                        state = state.with(AGE, i + 1);
                        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
                    } else {
                        Direction direction = Direction.Type.HORIZONTAL.random(random);
                        BlockPos blockPos = pos.offset(direction);
                        BlockState blockState = world.getBlockState(blockPos.down());
                        if (world.getBlockState(blockPos).isAir() && (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT))) {
                            world.setBlockState(blockPos, this.getGourdBlock().getDefaultState());
                            world.setBlockState(pos, this.getGourdBlock().getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                        }
                    }
                }
            }
        }
        ci.cancel();
    }


    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void growInject(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        float multiplier = NaturesNicheMod.CONFIG.getModifier(state, world, pos);
        int i = Math.min(7, state.get(AGE) + MathHelper.nextInt(world.random, (int) (2.0 * multiplier), (int) Math.ceil(5.0 * multiplier)));
        BlockState blockState = state.with(AGE, i);
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        if (i == 7) {
            blockState.randomTick(world, pos, world.random);
        }
        ci.cancel();
    }
}
