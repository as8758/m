package health_app;

public class RecipeManager {
    FoodDatabase foodDb;
    DataPersistence persistence;

    public RecipeManager(FoodDatabase db, DataPersistence persistence) {
        this.foodDb = db;
        this.persistence = persistence;
    }

    public Recipe createRecipe(String name) {
        return new Recipe();
    }

    public void saveFoodDatabase() {
        persistence.saveFoodDatabase(foodDb);
    }
} 