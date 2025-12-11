package com.tugasbesar.models.item;

public class IngredientFactory {

    public static Ingredient createTomato() {
        return new Ingredient("Tomato", true);
    }

    public static Ingredient createPasta() {
        return new Ingredient("Pasta", false); // False = Langsung Masak
    }

    public static Ingredient createBeef() {
        return new Ingredient("Beef", true);
    }

    public static Ingredient createFish() {
        return new Ingredient("Fish", true);
    }

    public static Ingredient createShrimp() {
        return new Ingredient("Shrimp", true);
    }

}
