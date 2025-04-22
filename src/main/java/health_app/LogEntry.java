package health_app;

import java.io.Serializable;

public class LogEntry implements Serializable {
    public String foodName;
    public double servings;

    public LogEntry(String foodName, double servings) {
        this.foodName = foodName;
        this.servings = servings;
    }
} 