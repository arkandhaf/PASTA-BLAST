package com.tugasbesar.models.manager;
import java.util.ArrayList;
import java.util.List;

import  com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.Ingredient;
public class Recipe {
    public String recipeName;
    public List<Ingredient> requirements;

    public Recipe(String recipeName, List<Ingredient> requirements){
        this.recipeName = recipeName;
        this.requirements = requirements;
    }

    public boolean matches (Dish dish){
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
                return false;
            }
        }
        return true;
    }
}
