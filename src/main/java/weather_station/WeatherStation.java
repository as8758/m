package weather_station;
/*
 * Initial Author
 *      Michael J. Lutz
 *
 * Other Contributers
 *
 * Acknowledgements
 */
 
/*
 * Class for a simple computer based weather station that reports the current
 * temperature (in Celsius) every second. The station is attached to a
 * sensor that reports the temperature as a 16-bit number (0 to 65535)
 * representing the Kelvin temperature to the nearest 1/100th of a degree.
 *
 * This class is implements Runnable so that it can be embedded in a Thread
 * which runs the periodic sensing.
 *
 * The class also extends Observable so that it can notify registered
 * objects whenever its state changes. Convenience functions are provided
 * to access the temperature in different schemes (Celsius, Kelvin, etc.)
 */
import java.util.Observable ;

@SuppressWarnings("deprecation")
public class WeatherStation extends Observable implements Runnable {
    IBarometer interfaceBar; //Interface temp sensor
    ITempSensor interfaceTemp; //Interface bar pressure

    private final long PERIOD = 1000 ;      // 1 sec = 1000 ms
    private final int KTOC = -27315 ;       // Kelvin to Celsius conversion.

    private int currentReading ;
    private double currentPressure ;

    /*
     * When a WeatherStation object is created, it in turn creates the sensor
     * object it will use from interfaces of barometer and temp sensor.
     */
    public WeatherStation(IBarometer barometerI, ITempSensor tempSensorI) {
        interfaceBar = barometerI;
        interfaceTemp = tempSensorI;

        currentReading = interfaceTemp.reading();
        currentPressure = interfaceBar.pressure();
    }

    /*
     * The "run" method called by the enclosing Thread object when started.
     * Repeatedly sleeps a second, acquires the current temperature from its
     * sensor, and notifies registered Observers of the change.
     */
    public void run() {
        while( true ) {
            try {
                Thread.sleep(PERIOD) ;
            } catch (Exception e) {}    // ignore exceptions

            /*
             * Get next reading and notify any Observers.
             */
            synchronized(this) {
                currentReading = interfaceTemp.reading() ;
                currentPressure = interfaceBar.pressure() ;

            }
            setChanged() ;
            notifyObservers() ;
        }
    }

    /*
     * Return the current reading in degrees celsius as a
     * double precision number.
     */
    public synchronized double getCelsius() {
        return (currentReading + KTOC) / 100.0 ;
    }

    /*
     * Return the current reading in degrees Kelvin as a
     * double precision number.
     */
    public synchronized double getKelvin() {
        return currentReading / 100.0 ;
    }

    /*
     * Return the current reading in degrees Fahrenheit as a
     * double precision number.
     */
    public synchronized double getFahrenheit() {
        return (currentReading + KTOC) / 100.0 * 9.0 / 5.0 + 32.0 ;
    }

    /*
     * Return the current reading in inches of mercury as a
     * double precision number.
     */
    public synchronized double getPressure() {
        return currentPressure ;
    }

    /*
     * Return the current reading in millibars as a
     * double precision number.
     */
    public synchronized double getMillibars() {
        return currentPressure * 33.864 ;
    }
}
