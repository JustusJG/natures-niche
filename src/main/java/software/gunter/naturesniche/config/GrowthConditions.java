package software.gunter.naturesniche.config;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import software.gunter.naturesniche.utils.SuperMath;

public class GrowthConditions {
    private float temperature;
    private float humidity;
    private boolean precipitation;
    private String fx;

    public GrowthConditions(float temperature, float humidity, boolean precipitation) {
        this(temperature, humidity, precipitation, "x");
    }

    public GrowthConditions(float temperature, float humidity, boolean precipitation, String fx) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.fx = fx;
    }

    public float calculateGrowthModifier(float temperature, float humidity, boolean precipitation) {
        float temperatureModifier = calculateTemperatureModifier(temperature); // min 0.0, max 1.0
        float humidityModifier = calculateHumidityModifier(humidity); // min 0.0, max 1.0
        float precipitationModifier = calculatePrecipitationModifier(precipitation);

        float maxModifier = 1.0f;
        float combinedModifier = (temperatureModifier + humidityModifier + precipitationModifier) * (maxModifier / 3.0f);

        Argument combinedModifierArgument = new Argument("x", combinedModifier);
        Expression expression = new Expression(getFx(), combinedModifierArgument);

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

    private float calculatePrecipitationModifier(boolean precipitation) {
        return this.precipitation == precipitation ? 1.0f : 0.5f;
    }

    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public void setPrecipitation(boolean precipitation) { this.precipitation = precipitation; }
    public void setFx(String fx) {
        this.fx = fx;
    }

    // Getter-Methoden für die Felder
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public boolean isPrecipitation() { return precipitation; }
    public String getFx() { return (fx != null && !fx.isEmpty() ? fx : "x"); }

    // Optional: toString-Methode für die Ausgabe der Wachstumsbedingungen
    @Override
    public String toString() {
        return "GrowthConditions{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", fx='" + fx + '\'' +
                '}';
    }
}
