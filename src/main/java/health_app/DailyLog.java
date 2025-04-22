package health_app;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyLog implements Serializable {
    public LocalDate date;
    public List<LogEntry> entries = new ArrayList<>();
    public Double weight;
    public Double calorieGoal;

    public DailyLog(LocalDate date) {
        this.date = date;
    }

    public void addEntry(LogEntry entry) {
        entries.add(entry);
    }
} 