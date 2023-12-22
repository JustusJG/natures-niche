package software.gunter.naturesniche.config;

public class GrowthConditionsConfig {
    private final float temperature;
    private final float humidity;
    private final boolean precipitation;

    public GrowthConditionsConfig(float temperature, float humidity, boolean precipitation) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public boolean isPrecipitation() {
        return precipitation;
    }
}
