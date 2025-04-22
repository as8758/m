package health_app;

import java.io.Serializable;

public abstract class RecipeComponent implements Serializable {
    protected String name;

    public abstract double getCalories();
    public abstract double getFat();
    public abstract double getCarbs();
    public abstract double getProtein();
    public abstract double getSodium();
} 