package software.gunter.naturesniche;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NaturesNicheConfig {
    private final Map<Identifier, CropConfig> cropConfigs = new HashMap<>();

    private record CropConfig(Map<Identifier, Float> biomeModifier) {

        public float getModifier(Identifier biomeIdentifier) {
            if (this.biomeModifier.containsKey(biomeIdentifier)) {
                return this.biomeModifier.get(biomeIdentifier);
            }
            return 1.0f;
        }
    }

    public float getModifier(Identifier cropIdentifier, Identifier biomeIdentifier) {
        if (cropConfigs.containsKey(cropIdentifier)) {
            return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
        }
        return 1.0f;
    }
}
