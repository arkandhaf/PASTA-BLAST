package com.tugasbesar.models.interfaces;

import com.tugasbesar.models.enums.IngredientState;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();

    void chop();
    void cook();

    // Helper method agar Utensil bisa cek status bahan
    String getName();
    IngredientState getState();
}