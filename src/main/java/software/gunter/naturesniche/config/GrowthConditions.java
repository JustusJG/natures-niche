package software.gunter.naturesniche.config;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GrowthConditions {
    private final float maxDelta;
    private float temperature;
    private float humidity;
    private boolean precipitation;
    private String temperatureModifier;
    private String humidityModifier;
    private String precipitationModifier;
    private String deltaModifier;

    public GrowthConditions(
            float maxDelta,
            float temperature,
            float humidity,
            boolean precipitation) {
        this.maxDelta = maxDelta;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
    }

    public GrowthConditions(
            float maxDelta,
            float temperature,
            float humidity,
            boolean precipitation,
            String temperatureModifier,
            String humidityModifier,
            String precipitationModifier,
            String deltaModifier) {
        this.maxDelta = maxDelta;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.temperatureModifier = temperatureModifier;
        this.humidityModifier = humidityModifier;
        this.precipitationModifier = precipitationModifier;
        this.deltaModifier = deltaModifier;
    }

    public float computeClimateDelta(float temperature, float humidity, boolean precipitation) {
        float temperatureModifier = calculateTemperatureDifference(temperature); // min 0.0, max 1.0
        float humidityModifier = calculateHumidityDifference(humidity); // min 0.0, max 1.0
        float precipitationModifier = calculatePrecipitationModifier(precipitation);

        Argument temperatureModiferArgument = new Argument("t", temperatureModifier);
        Argument humidityModifierArgument = new Argument("h", humidityModifier);
        Argument precipitationModifierArgument = new Argument("p", precipitationModifier);
        Expression expression = new Expression(getDeltaModifier(), temperatureModiferArgument, humidityModifierArgument, precipitationModifierArgument);

        float delta = (float) expression.calculate();

        return delta / maxDelta;
    }

    private float calculateTemperatureDifference(float temperature) {
        float difference = temperature - getTemperature();
        return calculateDifference(difference, getTemperatureModifier());
    }

    private float calculateHumidityDifference(float humidity) {
        float difference = humidity - getHumidity();
        return calculateDifference(difference, getHumidityModifier());
    }

    private float calculateDifference(float difference, String modifier) {
        Argument differenceArgument = new Argument("x", difference);
        Expression expression = new Expression(modifier, differenceArgument);

        return (float) expression.calculate();
    }

    private float calculatePrecipitationModifier(boolean precipitation) {
        float difference = this.precipitation == precipitation ? 0.0f : 1.0f;
        return calculateDifference(difference, getPrecipitationModifier());
    }

    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public void setPrecipitation(boolean precipitation) { this.precipitation = precipitation; }

    // Getter-Methoden für die Felder
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public boolean isPrecipitation() { return precipitation; }

    public String getTemperatureModifier() {
        return temperatureModifier != null && !temperatureModifier.isEmpty() ? temperatureModifier : "x";
    }

    public String getHumidityModifier() {
        return humidityModifier != null && !humidityModifier.isEmpty() ? humidityModifier : "x";
    }

    public String getPrecipitationModifier() {
        return precipitationModifier != null && !precipitationModifier.isEmpty() ? precipitationModifier : "x";
    }

    public String getDeltaModifier() { return (deltaModifier != null && !deltaModifier.isEmpty() ? deltaModifier : "t + h + p"); }

    @Override
    public String toString() {
        return "GrowthConditions{" +
                "maxDelta=" + maxDelta +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", temperatureModifier='" + temperatureModifier + '\'' +
                ", humidityModifier='" + humidityModifier + '\'' +
                ", precipitationModifier='" + precipitationModifier + '\'' +
                ", deltaModifier='" + deltaModifier + '\'' +
                '}';
    }
}
