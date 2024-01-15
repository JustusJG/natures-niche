package software.gunter.naturesniche.config;

import net.minecraft.world.biome.Biome;

public class BiomeConfig {
    private float temperature;
    private float humidity;
    private Biome.Precipitation precipitation;
    private Biome.TemperatureModifier temperatureModifier;

    public BiomeConfig(float temperature, float humidity, Biome.Precipitation precipitation, Biome.TemperatureModifier temperatureModifier) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.temperatureModifier = temperatureModifier;
    }

    public BiomeConfig(float temperature, float humidity, boolean precipitation, Biome.TemperatureModifier temperatureModifier) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation ? temperature > 0.15 ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW : Biome.Precipitation.NONE;
        this.temperatureModifier = temperatureModifier;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public Biome.Precipitation getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Biome.Precipitation precipitation) {
        this.precipitation = precipitation;
    }

    public Biome.TemperatureModifier getTemperatureModifier() {
        return temperatureModifier;
    }

    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        this.temperatureModifier = temperatureModifier;
    }
}
