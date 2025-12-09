package com.tugasbesar.models.manager;
import Recipe.java;
public class Order {
    public Recipe recipe;
    public float timerRemaining;
    public boolean isCompleted;
    public boolean isFailed;

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
        timerRemaining--;
    }

    public boolean isExpired(){
        if (timerRemaining <= 0){
            this.isFailed = true;
            return isFailed;
        }
        return false;
    }

    public boolean  validateDish(Dish dish){

    }
    

}
