package com.tugasbesar.models.manager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tugasbesar.models.item.Dish;
public class OrderManager {
    private List<Order> activeOrder;
<<<<<<< HEAD
    private List<Recipe> availableRecipe;
=======
    private final List<Recipe> availableRecipe;
>>>>>>> 4f7cc94824cef1bf9cfdde03e6bb5e3912fcbe03
    private float spawnCooldown;
    private float spawnTimer;

    public OrderManager(List<Recipe> availableRecipe, float spawnCooldown){
        this.activeOrder = new ArrayList<>();
        this.availableRecipe = availableRecipe;
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
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
<<<<<<< HEAD
    public boolean checkOrder(Dish dish){
        String dishRecipeName = dish.getRecipeName();
        // making List string for collecting recipe name for every active order
        List<String> activeOrderRecipeName = new ArrayList<>();
        for (Order order : this.activeOrder){
            activeOrderRecipeName.add(order.getRecipe().getRecipeName());
        }
        // checks if dishString recipeName is contained in the activeOrderRecipeName
        return activeOrderRecipeName.contains(dishRecipeName);
=======
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
>>>>>>> 4f7cc94824cef1bf9cfdde03e6bb5e3912fcbe03
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
