package software.gunter.naturesniche.mixin;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor("TEMPERATURE_NOISE")
    static OctaveSimplexNoiseSampler getTemperatureNoise() {
        throw new AssertionError();
    }

    @Accessor("field_26750")
    static Codec<RegistryEntryList<Biome>> getKey() {
        throw new AssertionError();
    }
}
