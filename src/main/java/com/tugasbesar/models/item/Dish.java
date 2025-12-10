package com.tugasbesar.models.item; 

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Placeable;   
import com.tugasbesar.models.enums.IngredientState; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Dish extends Item implements Placeable {

    private final String recipeName; 
    private final List<String> requiredIngredients; 
    private final IngredientState state = IngredientState.SERVED; 

    public Dish(String finalName, List<String> ingredients) {
        super(finalName); 
        this.recipeName = finalName;
        
        // Sorting ingredients is crucial for recipe matching later
        List<String> sortedIngredients = new ArrayList<>(ingredients);
        Collections.sort(sortedIngredients);
        this.requiredIngredients = sortedIngredients;
        
        setEdible(true); 
    }


    public String getRecipeName() { 
        return recipeName; 
    }
    public List<String> getRequiredIngredients() { 
        return requiredIngredients; 
    }
    

    @Override
    public String getName() {
        return super.getName();
    }
    
    @Override
    public IngredientState getState() { 
        return this.state; 
    }
    

    @Override
    public boolean canBePlacedOnPlate() { 
        return true; 
    }
    
    @Override
    public String toString() {
        return "Dish: " + recipeName;
    }
}