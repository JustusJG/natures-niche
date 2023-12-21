package software.gunter.naturesniche.interfaces;

import java.util.Map;

public interface IConfig {
    float getModifier(String identifier);
    Map<String, Float> getModifierMap();
}
