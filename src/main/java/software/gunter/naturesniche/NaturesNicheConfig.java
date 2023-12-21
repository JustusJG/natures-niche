package software.gunter.naturesniche;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NaturesNicheConfig {
    private final List<CropConfig> cropConfigs = new ArrayList<>();

    private record CropConfig(Identifier cropIdentifier, Map<Identifier, Float> biomeModifier) {

        public Float getModifier(Identifier biomeIdentifier) {
            return this.biomeModifier.get(biomeIdentifier);
        }
    }
}
