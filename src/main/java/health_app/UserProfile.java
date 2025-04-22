package health_app;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class UserProfile implements Serializable {
    public String name;
    public Map<LocalDate, DailyLog> logMap = new TreeMap<>();

    public DailyLog getLog(LocalDate date) {
        return logMap.computeIfAbsent(date, DailyLog::new);
    }
} 