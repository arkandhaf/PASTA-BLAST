package com.tugasbesar.models.manager;
import java.util.ArrayList;
import java.util.List;

import  com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.Ingredient;
public class Recipe {
    private final String recipeName;
    public List<Ingredient> requirements;

    public Recipe(String recipeName, List<Ingredient> requirements){
        this.recipeName = recipeName;
        this.requirements = requirements;
    }

    public boolean matches (Dish dish){
<<<<<<< HEAD
        List<String> dishIngredients = dish.getRequiredIngredients();
        // make list for toString ingredient
        List<String> recipeIngredients = new ArrayList<>();
        for(Ingredient ingredient : this.requirements){
            recipeIngredients.add(ingredient.toString());
        }
        // checking ingredient length recipe == dish
        if (recipeIngredients.size() != dishIngredients.size()){
            return false;
        }
        // checking dish ingredeint in strings
        for (String ingredient : recipeIngredients ){
            if (!dishIngredients.contains(ingredient)){
=======
        List<String> dishIngredientsString = dish.getRequiredIngredients();
        // make list for toString ingredient
        List<String> recipeIngredientsString = new ArrayList<>();
        for(Ingredient ingredient : this.requirements){
            recipeIngredientsString.add(ingredient.toString());
        }
        // checking ingredient length recipe == dish
        if (recipeIngredientsString.size() != dishIngredientsString.size()){
            return false;
        }
        // checking dish ingredient in strings
        for (String ingredient : recipeIngredientsString ){
            if (!dishIngredientsString.contains(ingredient)){
>>>>>>> 4f7cc94824cef1bf9cfdde03e6bb5e3912fcbe03
                return false;
            }
        }
        return true;
    }

    public String getRecipeName(){
        return this.recipeName;
    }
}
