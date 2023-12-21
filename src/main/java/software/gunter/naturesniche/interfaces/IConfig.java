package software.gunter.naturesniche.interfaces;

import java.util.Map;

public interface IConfig {
    void update();
    float getModifier(String identifier);
    Map<String, Float> getModifierMap();
}
