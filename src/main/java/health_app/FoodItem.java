package health_app;

import java.io.Serializable;

public class FoodItem extends RecipeComponent implements Serializable {
    double calories, fat, carbs, protein, sodium;
    String category;

    public FoodItem(String name, String category, double calories, double fat, double carbs, double protein, double sodium) {
        this.name = name;
        this.category = category;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.sodium = sodium;
    }

    public double getCalories() { return calories; }
    public double getFat() { return fat; }
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
    public double getSodium() { return sodium; }
    public String getCategory() { return category; }
} 