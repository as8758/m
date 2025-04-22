package health_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends RecipeComponent implements Serializable {
    List<RecipeComponent> ingredients = new ArrayList<>();
    List<Double> servings = new ArrayList<>();

    public void addIngredient(RecipeComponent rc, double count) {
        ingredients.add(rc);
        servings.add(count);
    }

    public double getCalories() {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++)
            total += ingredients.get(i).getCalories() * servings.get(i);
        return total;
    }
    public double getFat() {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++)
            total += ingredients.get(i).getFat() * servings.get(i);
        return total;
    }
    public double getCarbs() {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++)
            total += ingredients.get(i).getCarbs() * servings.get(i);
        return total;
    }
    public double getProtein() {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++)
            total += ingredients.get(i).getProtein() * servings.get(i);
        return total;
    }
    public double getSodium() {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++)
            total += ingredients.get(i).getSodium() * servings.get(i);
        return total;
    }
} 