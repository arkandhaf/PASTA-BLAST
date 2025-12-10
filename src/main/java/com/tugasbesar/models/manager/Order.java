package com.tugasbesar.models.manager;
import com.tugasbesar.models.item.Dish;
public class Order {
    private final Recipe recipe;
    private float timerRemaining;
    private boolean isCompleted;
    private boolean isFailed;

    /**
     * create new order
     * @param recipe
     * @param timerRemaining
     */
    public Order(Recipe recipe, float timerRemaining){
        this.recipe = recipe;
        this.timerRemaining = timerRemaining;
        this.isCompleted = false;
        this.isFailed = false;
    }
    public void update(){
        this.timerRemaining--;
    }

    public boolean isExpired(){
        return this.timerRemaining <= 0;
    }
    /**
     * Validate dish by making conditional to the recipe attribute
     * @param dish
     * @return
     */
    public boolean validateDish(Dish dish){
        return this.recipe.matches(dish);
    }
    
    public void setOrderComplete(){
        this.isCompleted = true;
        this.isFailed = false;
    }
    public void setOrderFailed(){
        this.isCompleted = false;
        this.isFailed = true;
    }
    public Recipe getRecipe(){
        return this.recipe;
    }
    public boolean isFailed(){
        return this.isFailed;
    }
    public boolean isCompleted(){
        return this.isCompleted;
    }
    public float getTimerRemaining(){
        return this.timerRemaining;
    }

}
