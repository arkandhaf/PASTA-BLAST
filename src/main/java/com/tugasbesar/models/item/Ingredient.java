package com.tugasbesar.models.item;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Choppable;
import com.tugasbesar.models.interfaces.Cookable;
import com.tugasbesar.models.interfaces.Placeable;

public class Ingredient extends Item implements Choppable, Cookable, Placeable {
    
    private IngredientState state;
    private boolean isChoppable; // Ini False untuk Pasta

    public Ingredient(String name, boolean isChoppable) {
        super(name);
        this.state = IngredientState.RAW; 
        this.isChoppable = isChoppable;
    }

    // --- LOGIC CHOPPING ---
    @Override
    public boolean canBeChopped() {
        // Syarat: Harus boleh dipotong (True) DAN masih mentah (RAW)
        return isChoppable && state == IngredientState.RAW;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
        }
    }

    // --- LOGIC COOKING ---
    @Override
    public boolean canBeCooked() {
        // Bisa dimasak kalau:
        // 1. Sudah dipotong (CHOPPED) - misal Tomat/Lettuce
        // 2. ATAU Masih RAW tapi emang gak bisa dipotong (RAW) - misal Pasta
        if (state == IngredientState.CHOPPED) return true;
        if (state == IngredientState.RAW && !isChoppable) return true; 
        
        return false;
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
        }
    }
    
    // --- LOGIC PLATING ---
    @Override
    public boolean canBePlacedOnPlate() {
        // Yang gosong gak boleh disajikan
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