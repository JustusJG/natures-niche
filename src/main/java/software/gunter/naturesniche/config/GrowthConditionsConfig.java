package software.gunter.naturesniche.config;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import software.gunter.naturesniche.NaturesNicheMod;
import software.gunter.naturesniche.utils.SuperMath;

public class GrowthConditionsConfig {
    private float temperature;
    private float humidity;
    private boolean precipitation;
    private String fx;

    public GrowthConditionsConfig(GrowthConditionsConfig growthConditionsConfig) {
        this.temperature = growthConditionsConfig.temperature;
        this.humidity = growthConditionsConfig.humidity;
        this.precipitation = growthConditionsConfig.precipitation;
        this.fx = growthConditionsConfig.fx;
    }

    public GrowthConditionsConfig(float temperature, float humidity, boolean precipitation) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
    }

    public GrowthConditionsConfig(float temperature, float humidity, boolean precipitation, String fx) {
        this(temperature, humidity, precipitation);
        this.fx = fx;
    }

    public float calculateGrowthModifier(float temperature, float humidity, boolean precipitation) {
        float temperatureModifier = calculateTemperatureModifier(temperature); // min 0.0, max 1.0
        float humidityModifier = calculateHumidityModifier(humidity); // min 0.0, max 1.0
        float precipitationModifier = this.precipitation == precipitation ? 1.0f : 0.0f; // Annahme: Niederschlag erhöht das Wachstum

        float modifier = getModifier(temperatureModifier, humidityModifier, precipitationModifier);
        NaturesNicheMod.LOGGER.debug("calculatedModifier: " + modifier);
        return modifier;
    }

    private float getModifier(float temperatureModifier, float humidityModifier, float precipitationModifier) {
        float maxModifier = 1.0f;
        float combinedModifier = (temperatureModifier + humidityModifier + precipitationModifier) * (maxModifier / 3.0f);

        Argument combinedModifierArgument = new Argument("x", combinedModifier);
        Expression expression = new Expression(fx, combinedModifierArgument);

        float modifier = (float) expression.calculate();
        modifier = Math.max(0.0f, Math.min(maxModifier, modifier));
        return modifier * 2.0f;
    }

    private float calculateTemperatureModifier(float temperature) {
        float maxModifier = 1.0f;
        float difference = Math.abs(temperature - getTemperature());
        float modifier = (float) SuperMath.calculateAsymptoticFunctionValue(difference, 0, maxModifier, 1.0);
        modifier = Math.max(0.0f, Math.min(maxModifier, modifier));
        return modifier;
    }

    private float calculateHumidityModifier(float humidity) {
        float maxModifier = 1.0f;
        float difference = Math.abs(humidity - getHumidity());
        float modifier = (float) SuperMath.calculateAsymptoticFunctionValue(difference, 0, maxModifier, 4.0);
        modifier = Math.max(0.0f, Math.min(maxModifier, modifier));
        return modifier;
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

    public String getFx() {
        return fx;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setPrecipitation(boolean precipitation) {
        this.precipitation = precipitation;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }
}
