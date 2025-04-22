package health_app;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FoodDatabase implements Serializable {
    public Map<String, RecipeComponent> foodMap = new LinkedHashMap<>();

    public void addFood(String name, RecipeComponent item) {
        foodMap.put(name, item);
    }

    public RecipeComponent getFood(String name) {
        return foodMap.get(name);
    }
} 