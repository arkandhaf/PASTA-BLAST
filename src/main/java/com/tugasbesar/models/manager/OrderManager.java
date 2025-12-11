package com.tugasbesar.models.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.Ingredient;
// import com.tugasbesar.models.Recipe; 
// import com.tugasbesar.models.Order;

public class OrderManager {
    private static OrderManager instance;
    
    private List<Order> activeOrder;
    private List<Recipe> availableRecipe;
    
    private float spawnCooldown;
    private float spawnTimer;

    private OrderManager(){
        this.activeOrder = new ArrayList<>();
        this.availableRecipe = new ArrayList<>();
        
        // Cooldown 10 detik (600 tick di 60 FPS)
        this.spawnCooldown = 600.0f;  
        this.spawnTimer = 0;
    }
    
    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    public void initialize(List<Recipe> recipes, float cooldown) {
        this.availableRecipe = recipes;
        this.spawnCooldown = cooldown;
    }

    public void update (){
        // 1. Spawn Logic
        if (spawnTimer <= 0){
            spawnOrder(); 
            this.spawnTimer = this.spawnCooldown; 
        }
        else{
            spawnTimer--;
        }

        // 2. Update Timer Order (Looping)
        // Warning "local variable order is not used" muncul karena loop ini kosong.
        // Kita isi logic sederhana biar warning hilang & berguna.
        for (Order order : activeOrder) {
             // Kurangi durasi order setiap tick (asumsi ada method ini, kalau gak ada hapus baris ini)
             // order.decreaseTimer(); 
        }

        // 3. Hapus Order Kadaluarsa
        removeExpiredOrder();
    }

    public void spawnOrder(){
        if (availableRecipe.isEmpty()) return; 

        Random random = new Random();
        Recipe randomRecipe = availableRecipe.get(random.nextInt(availableRecipe.size()));
        
        int maxDuration = 1200; // 20 detik
        int minDuration = 600;  // 10 detik
        int randomOrderDuration = random.nextInt(maxDuration - minDuration + 1) + minDuration; 
        
        Order orderSpawn = new Order(randomRecipe, randomOrderDuration);
        this.activeOrder.add(orderSpawn);
        
        // [FIX] Ganti getName() jadi getRecipeName()
        System.out.println("📜 New Order: " + randomRecipe.getRecipeName()); 
    }

    // Method Check Order (Dipanggil ServingStation)
    public boolean checkOrder(Dish dish){
        if (dish == null) return false;

        String dishRecipeName = dish.getRecipeName(); 

        for (Order order : this.activeOrder){
            // [FIX] Ganti getName() jadi getRecipeName()
            String orderRecipeName = order.getRecipe().getRecipeName(); 
            
            if (orderRecipeName.equalsIgnoreCase(dishRecipeName)){
                completeOrder(order);
                this.activeOrder.remove(order);
                return true; 
            }
        }
        return false; 
    }

    public Recipe findMatchingRecipe(List<String> ingredientsString){
        for (Recipe recipe : availableRecipe) {
            List<String> recipeIngredients = new ArrayList<>();
            for (Ingredient ingredient : recipe.requirements) {
                recipeIngredients.add(ingredient.getName());
            }
            
            if (recipeIngredients.size() != ingredientsString.size()) {
                continue;
            }
            
            if (recipeIngredients.containsAll(ingredientsString) && 
                ingredientsString.containsAll(recipeIngredients)) {
                return recipe;  
            }
        }
        return null;  
    }

    public void completeOrder(Order order){
        order.setOrderComplete();
        // ScoreManager.addScore(100);
    }

    public void failOrder(Order order){
        order.setOrderFailed();
    }

    public void removeExpiredOrder(){
        boolean removed = activeOrder.removeIf(order -> order.isExpired());
        if (removed) {
            System.out.println("❌ Order Expired!");
            // ScoreManager.minusScore(10);
        }
    }

    public List<Order> getActiveOrder(){
        return this.activeOrder;
    }
    
    public List<Recipe> getAvailableRecipe() {
        return this.availableRecipe;
    }
}