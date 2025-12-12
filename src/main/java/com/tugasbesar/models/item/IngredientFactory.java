package com.tugasbesar.models.item;

public class IngredientFactory {

    // Boiling (Rebus) - Langsung Masak
    public static Ingredient createPasta() {
        return new Ingredient("Pasta", false); 
    }

    // Boiling (Rebus) - Potong Dulu
    public static Ingredient createTomato() {
        return new Ingredient("Tomato", true);
    }

    // Frying (Goreng) - Potong Dulu
    public static Ingredient createBeef() {
        return new Ingredient("Beef", true);
    }
    
    public static Ingredient createShrimp() {
        return new Ingredient("Shrimp", true);
    }
    
    public static Ingredient createFish() {
        return new Ingredient("Fish", true);
    }
}