package health_app;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataPersistence {
    private static final String FOOD_DB_FILE = "health_database/food_database.csv";
    private static final String USER_DATA_DIR = "health_database/user_data";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DataPersistence() {
        // Create user data directory if it doesn't exist
        new File(USER_DATA_DIR).mkdirs();
    }

    public void saveFoodDatabase(FoodDatabase db) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FOOD_DB_FILE))) {
            // Write header
            writer.println("name,category,calories,fat,carbs,protein,sodium");
            
            // Write each food item
            for (Map.Entry<String, RecipeComponent> entry : db.foodMap.entrySet()) {
                RecipeComponent food = entry.getValue();
                if (food instanceof FoodItem) {
                    FoodItem item = (FoodItem) food;
                    writer.printf("%s,%s,%.1f,%.1f,%.1f,%.1f,%.1f%n",
                        item.name,
                        item.category,
                        item.calories,
                        item.fat,
                        item.carbs,
                        item.protein,
                        item.sodium);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving food database: " + e.getMessage());
        }
    }

    public FoodDatabase loadFoodDatabase() {
        FoodDatabase db = new FoodDatabase();
        File file = new File(FOOD_DB_FILE);
        if (!file.exists()) {
            return db;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    String name = parts[0];
                    String category = parts[1];
                    double calories = Double.parseDouble(parts[2]);
                    double fat = Double.parseDouble(parts[3]);
                    double carbs = Double.parseDouble(parts[4]);
                    double protein = Double.parseDouble(parts[5]);
                    double sodium = Double.parseDouble(parts[6]);
                    
                    FoodItem item = new FoodItem(name, category, calories, fat, carbs, protein, sodium);
                    db.addFood(name, item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading food database: " + e.getMessage());
        }
        return db;
    }

    public void saveUserProfile(UserProfile profile) {
        String userFile = USER_DATA_DIR + File.separator + profile.name + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
            // Write header
            writer.println("date,weight,calorie_goal");
            
            // Write each day's data
            for (Map.Entry<LocalDate, DailyLog> entry : profile.logMap.entrySet()) {
                LocalDate date = entry.getKey();
                DailyLog log = entry.getValue();
                writer.printf("%s,%.1f,%.1f%n",
                    date.format(DATE_FORMATTER),
                    log.weight != null ? log.weight : 0.0,
                    log.calorieGoal != null ? log.calorieGoal : 0.0);
                
                // Write food entries for this day
                String entriesFile = USER_DATA_DIR + File.separator + 
                    profile.name + "_" + date.format(DATE_FORMATTER) + "_entries.csv";
                try (PrintWriter entriesWriter = new PrintWriter(new FileWriter(entriesFile))) {
                    entriesWriter.println("food_name,servings");
                    for (LogEntry foodEntry : log.entries) {
                        entriesWriter.printf("%s,%.1f%n", foodEntry.foodName, foodEntry.servings);
                    }
                } catch (IOException e) {
                    System.err.println("Error saving food entries for " + date + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
        }
    }

    public UserProfile loadUserProfile(String username) {
        UserProfile profile = new UserProfile();
        profile.name = username;
        
        String userFile = USER_DATA_DIR + File.separator + username + ".csv";
        File file = new File(userFile);
        if (!file.exists()) {
            return profile;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                    double weight = Double.parseDouble(parts[1]);
                    double calorieGoal = Double.parseDouble(parts[2]);
                    
                    DailyLog log = profile.getLog(date);
                    log.weight = weight;
                    log.calorieGoal = calorieGoal;
                    
                    // Load food entries for this day
                    String entriesFile = USER_DATA_DIR + File.separator + 
                        username + "_" + date.format(DATE_FORMATTER) + "_entries.csv";
                    loadFoodEntries(entriesFile, log);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
        }
        return profile;
    }

    private void loadFoodEntries(String entriesFile, DailyLog log) {
        File file = new File(entriesFile);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String foodName = parts[0];
                    double servings = Double.parseDouble(parts[1]);
                    log.addEntry(new LogEntry(foodName, servings));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading food entries from " + entriesFile + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing servings value in " + entriesFile + ": " + e.getMessage());
        }
    }
} 