package com.tugasbesar.models.manager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.Ingredient;
public class OrderManager {
    private static OrderManager instance;
    
    private List<Order> activeOrder;
    private List<Recipe> availableRecipe;
    private float spawnCooldown;
    private float spawnTimer;

    // Private constructor
    private OrderManager(){
        this.activeOrder = new ArrayList<>();
        this.availableRecipe = new ArrayList<>();
        this.spawnCooldown = 10.0f;  // Default value
        this.spawnTimer = 0;
    }
    
    // Get the single instance
    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    // Initialize with recipes (call once at game start)
    public void initialize(List<Recipe> recipes, float cooldown) {
        this.availableRecipe = recipes;
        this.spawnCooldown = cooldown;
    }
    /**
     * update times on orderManager side, doesnt include update timer for order
     */
    public void update (){
        if (spawnTimer == 0){
            this.spawnTimer = this.spawnCooldown;
        }
        else{
            spawnTimer--;
        }

    }
    /**
     * spawn new order and add it into active order
     */
    public void spawnOrder(){
        Random random = new Random();
        Recipe randomRecipe = availableRecipe.get(random.nextInt(availableRecipe.size()));
        int maxDuration = 20;
        int minDuration = 10;
        int randomOrderDuration = random.nextInt(maxDuration - minDuration + 1) + minDuration; 
        Order orderSpawn = new Order(randomRecipe, randomOrderDuration);
        this.activeOrder.add(orderSpawn);
    }
    /**
     * if dish recipe name is contained in the recipe name of active order
     * it means the dish can finish an order. A dish must be appropriate to the recipe
     * equals the dish has the same ingredients as the recipe.
     */
    public boolean findAndCompleteOrder(Dish dish){
        String dishRecipeName = dish.getRecipeName();
        // checks dish recipe is needed in one of activeOrder recipe
        for (Order order : this.activeOrder){
            String orderRecipeName = order.getRecipe().getRecipeName();
            if (orderRecipeName.equals(dishRecipeName)){
                completeOrder(order);
                this.activeOrder.remove(order);
                return true;
            }
        }
        // after checking all of active order, none need the dish inputted, give false
        return false;
    }

    public Recipe findMatchingRecipe(List<String> ingredientsString){
        for (Recipe recipe : availableRecipe) {
            // Get recipe ingredients as strings
            List<String> recipeIngredients = new ArrayList<>();
            for (Ingredient ingredient : recipe.requirements) {
                recipeIngredients.add(ingredient.toString());
            }
            
            // Check if sizes match
            if (recipeIngredients.size() != ingredientsString.size()) {
                continue;
            }
            
            // Check if all ingredients match (order doesn't matter)
            if (recipeIngredients.containsAll(ingredientsString) && 
                ingredientsString.containsAll(recipeIngredients)) {
                return recipe;  // Found matching recipe
            }
        }
        return null;  // No matching recipe found
    }

    public void completeOrder(Order order){
        order.setOrderComplete();
    }
    public void failOrder(Order order){
        order.setOrderFailed();
    }
    public void removeExpiredOrder(){
        activeOrder.removeIf(order -> order.isExpired());
    }

    public List<Order> getActiveOrder(){
        return this.activeOrder;
    }
    public List<Recipe> getAvailableRecipe() {
        return this.availableRecipe;
    }
    
    public float getSpawnCooldown() {
        return this.spawnCooldown;
    }
    
    public float getSpawnTimer() {
        return this.spawnTimer;
    }


}
