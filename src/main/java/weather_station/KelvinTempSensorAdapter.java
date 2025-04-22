package weather_station;

public class KelvinTempSensorAdapter implements ITempSensor {
    private KelvinTempSensor sensor;
    
    public KelvinTempSensorAdapter() {
        sensor = new KelvinTempSensor();
    }
    
    public int reading() {
        return sensor.reading();
    }
}