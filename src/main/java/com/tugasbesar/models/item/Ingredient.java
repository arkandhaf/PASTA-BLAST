// File: com.tugasbesar.models.item.Ingredient.java

package com.tugasbesar.models.item;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Choppable;
import com.tugasbesar.models.interfaces.Cookable;
import com.tugasbesar.models.interfaces.Placeable;



public class Ingredient extends Item implements Choppable, Cookable, Placeable {
    
    private IngredientState state;
    private boolean isChoppable; 

    public Ingredient(String name, boolean isChoppable) {
        super(name);
        this.state = IngredientState.RAW; 
        this.isChoppable = isChoppable;
    }

    @Override
    public boolean canBeChopped() {
        return isChoppable && state == IngredientState.RAW;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
        }
    }

    @Override
    public boolean canBeCooked() {
        return state == IngredientState.RAW || state == IngredientState.CHOPPED;
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
        }
    }
    
    
    @Override
    public boolean canBePlacedOnPlate() {
        //cuman yang gak gosong yang boleh ditaruh di piring
        return state != IngredientState.BURNED;
    }

    @Override
    public IngredientState getState() {
        return state;
    }

    
    public void burn() {
        this.state = IngredientState.BURNED;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return name + " (" + state + ")";
    }
}