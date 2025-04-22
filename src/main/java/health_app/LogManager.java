package health_app;

import java.time.LocalDate;

public class LogManager {
    FoodDatabase db;
    DataPersistence persistence;

    public LogManager(FoodDatabase db, DataPersistence persistence) {
        this.db = db;
        this.persistence = persistence;
    }

    public void logFood(UserProfile user, LocalDate date, String foodName, double servings) {
        DailyLog log = user.getLog(date);
        log.addEntry(new LogEntry(foodName, servings));
        persistence.saveUserProfile(user);
    }
} 