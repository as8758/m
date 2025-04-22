package weather_station;

/*
 *  Author: Colin Chomas
 * 
 */
import java.util.Observer;
import java.util.Observable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridLayout;

@SuppressWarnings("deprecation")
public class Launcher extends JFrame implements Observer {
    private final WeatherStation station;
    private JLabel celsiusField; // put current celsius reading here
    private JLabel kelvinField; // put current kelvin reading here
    private JLabel fahrenheitField; // put current fahrenheit reading here
    private JLabel inches; // put current barometer reading here in inches
    private JLabel millibars; // put current barometer reading here in millibars

    private static Font labelFont = new Font(Font.SERIF, Font.PLAIN, 72);

    public Launcher(WeatherStation station) {
        super("Weather Station");

        this.station = station;
        this.station.addObserver(this);

        this.setLayout(new GridLayout(1, 0));
        JPanel panel;

        panel = new JPanel(new GridLayout(2, 1));
        this.add(panel);
        createLabel("Kelvin ", panel);
        kelvinField = createLabel("", panel);

        panel = new JPanel(new GridLayout(2, 1));
        this.add(panel);
        createLabel("Celsius ", panel);
        celsiusField = createLabel("", panel);

        panel = new JPanel(new GridLayout(2, 1));
        this.add(panel);
        createLabel("Fahrenheit ", panel);
        fahrenheitField = createLabel("", panel);

        panel = new JPanel(new GridLayout(2, 1));
        this.add(panel);
        createLabel("Inches ", panel);
        inches = createLabel("", panel);

        panel = new JPanel(new GridLayout(2, 1));
        this.add(panel);
        createLabel("Millibars ", panel);
        millibars = createLabel("", panel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

    }

    /*
     * Set the label holding the Kelvin temperature.
     */
    public void setKelvinJLabel(double temperature) {
        kelvinField.setText(String.format("%6.2f", temperature));
    }

    /*
     * Set the label holding the Celsius temperature.
     */
    public void setCelsiusJLabel(double temperature) {
        celsiusField.setText(String.format("%6.2f", temperature));
    }

    /*
     * Set the label holding the Fahrenheit temperature.
     */
    public void setFahrenheitJLabel(double temperature) {
        fahrenheitField.setText(String.format("%6.2f", temperature));
    }

    /*
     * Set the label holding the barometer reading in inches.
     */
    public void setInchesJLabel(double pressure) {
        inches.setText(String.format("%6.2f", pressure));
    }

    /*
     * Set the label holding the barometer reading in millibars.
     */
    public void setMillibarsJLabel(double pressure) {
        millibars.setText(String.format("%6.2f", pressure));
    }

    private JLabel createLabel(String string, JPanel panel) {
        JLabel label = new JLabel(string);

        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.TOP);
        label.setFont(labelFont);
        panel.add(label);

        return label;
    }

    public void update(Observable obs, Object ignore) {
        if (station != obs) {
            return;
        }

        setKelvinJLabel(station.getKelvin());
        setCelsiusJLabel(station.getCelsius());
        setFahrenheitJLabel(station.getFahrenheit());
        setInchesJLabel(station.getPressure());
        setMillibarsJLabel(station.getMillibars());

    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        IBarometer barometer = new Barometer();
        ITempSensor tempSensor = new KelvinTempSensorAdapter();
        WeatherStation ws = new WeatherStation(barometer, tempSensor);
        Thread thread = new Thread(ws);
        Launcher ui = new Launcher(ws);

        thread.start();
    }
}
