package weather_station;

public interface ITempSensor {
    /**
     *  Returns the current atmospheric temperature reading.
     * 
     * @return the current temperature in kelvin
     */
    
    int reading();
}
