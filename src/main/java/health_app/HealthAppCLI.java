package health_app;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.Map;

public class HealthAppCLI {
    Scanner scanner = new Scanner(System.in);
    UserProfile currentUser;
    FoodDatabase db;
    LogManager logManager;
    RecipeManager recipeManager;
    DataPersistence persistence;

    public void start() {
        System.out.println("Welcome to HealthNCare App CLI");
        persistence = new DataPersistence();
        db = persistence.loadFoodDatabase();
        
        // Initialize with some basic foods if database is empty
        if (db.foodMap.isEmpty()) {
            initializeFoodDatabase();
        }
        
        logManager = new LogManager(db, persistence);
        recipeManager = new RecipeManager(db, persistence);
        
        // Prompt for username
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        currentUser = new UserAuthenticator(persistence).login(username);
        System.out.println("Welcome back, " + username + "!");
        
        mainMenu();
    }

    private void initializeFoodDatabase() {
        // Add some basic foods
        db.addFood("bread", new FoodItem("bread", "Grains", 80, 1.0, 15.0, 3.0, 170.0));
        db.addFood("apple", new FoodItem("apple", "Fruits", 95, 0.3, 25.0, 0.5, 2.0));
        db.addFood("banana", new FoodItem("banana", "Fruits", 105, 0.4, 27.0, 1.3, 1.0));
        db.addFood("chicken breast", new FoodItem("chicken breast", "Protein", 165, 3.6, 0.0, 31.0, 74.0));
        db.addFood("rice", new FoodItem("rice", "Grains", 130, 0.3, 28.0, 2.7, 1.0));
        persistence.saveFoodDatabase(db);
    }

    void mainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Add Food to Log");
            System.out.println("2. View Today's Log");
            System.out.println("3. Manage Foods");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    addFoodToLog();
                    break;
                case 2:
                    viewTodaysLog();
                    break;
                case 3:
                    manageFoods();
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    void manageFoods() {
        while (true) {
            System.out.println("\nFood Management:");
            System.out.println("1. View Available Foods");
            System.out.println("2. Add New Food");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        viewAvailableFoods();
                        break;
                    case 2:
                        addNewFood();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Please enter a valid number (1, 2, or 3).");
                scanner.nextLine(); // clear the invalid input
            }
        }
    }

    void viewAvailableFoods() {
        System.out.println("\nAvailable Foods:");
        for (Map.Entry<String, RecipeComponent> entry : db.foodMap.entrySet()) {
            FoodItem food = (FoodItem) entry.getValue();
            System.out.printf("%s: %.0f calories, %.1f fat, %.1f carbs, %.1f protein, %.1f sodium\n",
                food.name, food.calories, food.fat, food.carbs, food.protein, food.sodium);
        }
    }

    void addNewFood() {
        System.out.print("Enter food name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter category (e.g., Fruits, Vegetables, Protein, Grains, Dairy, Nuts, Snacks): ");
        String category = scanner.nextLine();
        
        System.out.print("Enter calories: ");
        double calories = scanner.nextDouble();
        
        System.out.print("Enter fat (g): ");
        double fat = scanner.nextDouble();
        
        System.out.print("Enter carbs (g): ");
        double carbs = scanner.nextDouble();
        
        System.out.print("Enter protein (g): ");
        double protein = scanner.nextDouble();
        
        System.out.print("Enter sodium (mg): ");
        double sodium = scanner.nextDouble();
        
        scanner.nextLine(); // consume newline
        
        FoodItem newFood = new FoodItem(name, category, calories, fat, carbs, protein, sodium);
        db.addFood(name, newFood);
        persistence.saveFoodDatabase(db);
        System.out.println("Food added successfully!");
    }

    void addFoodToLog() {
        System.out.println("\nAvailable Foods:");
        viewAvailableFoods();
        
        System.out.print("\nEnter food name: ");
        String foodName = scanner.nextLine();
        
        if (db.getFood(foodName) == null) {
            System.out.println("Food not found in database. Please add it first in the Manage Foods menu.");
            return;
        }
        
        System.out.print("Enter servings: ");
        double servings = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        
        logManager.logFood(currentUser, LocalDate.now(), foodName, servings);
        System.out.println("Food added to log successfully!");
    }

    void viewTodaysLog() {
        DailyLog todayLog = currentUser.getLog(LocalDate.now());
        
        if (todayLog.entries.isEmpty()) {
            System.out.println("No entries for today.");
            return;
        }
        
        System.out.println("\nToday's Log:");
        double totalCalories = 0;
        for (LogEntry entry : todayLog.entries) {
            RecipeComponent food = db.getFood(entry.foodName);
            if (food != null) {
                double calories = food.getCalories() * entry.servings;
                totalCalories += calories;
                System.out.printf("%s: %.1f servings (%.0f calories)\n", 
                    food.name, entry.servings, calories);
            }
        }
        System.out.printf("\nTotal Calories: %.0f\n", totalCalories);
    }

    public static void main(String[] args) {
        new HealthAppCLI().start();
    }
}
