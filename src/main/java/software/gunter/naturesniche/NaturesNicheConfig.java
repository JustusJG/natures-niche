package software.gunter.naturesniche;

import java.util.*;

public class NaturesNicheConfig {
    private final Map<String, CropConfig> cropConfigs = new HashMap<>();

    private static class CropConfig {
        private final Map<String, Float> biomeModifier;

        private CropConfig(Map<String, Float> biomeModifier) {
            this.biomeModifier = biomeModifier;
        }

        public float getModifier(String biomeIdentifier) {
            if (this.biomeModifier.containsKey(biomeIdentifier)) {
                return this.biomeModifier.get(biomeIdentifier);
            }
            return 1.0f;
        }
    }

    public float getModifier(String cropIdentifier, String biomeIdentifier) {
        if (cropConfigs.containsKey(cropIdentifier)) {
            return cropConfigs.get(cropIdentifier).getModifier(biomeIdentifier);
        }
        return 1.0f;
    }
}
