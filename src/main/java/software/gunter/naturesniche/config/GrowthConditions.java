package software.gunter.naturesniche.config;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GrowthConditions {
    private float temperature;
    private float humidity;
    private boolean precipitation;
    private String deltaModifier;

    public GrowthConditions(
            float temperature,
            float humidity,
            boolean precipitation) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
    }

    public GrowthConditions(
            float temperature,
            float humidity,
            boolean precipitation,
            String deltaModifier) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.deltaModifier = deltaModifier;
    }

    public float computeClimateDelta(float temperature, float humidity, boolean precipitation) {
        Argument temperatureModiferArgument = new Argument("t", temperature - getTemperature());
        Argument humidityModifierArgument = new Argument("h", humidity - getHumidity());
        Argument precipitationModifierArgument = new Argument("p", this.precipitation == precipitation ? 0.0f : 1.0f);
        Expression expression = new Expression(getDeltaModifier(), temperatureModiferArgument, humidityModifierArgument, precipitationModifierArgument);

        return (float) expression.calculate();
    }

    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public void setPrecipitation(boolean precipitation) { this.precipitation = precipitation; }

    // Getter-Methoden für die Felder
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public boolean isPrecipitation() { return precipitation; }

    public String getDeltaModifier() { return (deltaModifier != null && !deltaModifier.isEmpty() ? deltaModifier : "|t| + |h| + |p|"); }

    @Override
    public String toString() {
        return "GrowthConditions{" +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", deltaModifier='" + deltaModifier + '\'' +
                '}';
    }
}
