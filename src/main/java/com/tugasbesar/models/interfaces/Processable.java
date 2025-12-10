package com.tugasbesar.models.interfaces;

import com.tugasbesar.models.enums.IngredientState;

public interface Processable {
    // digunakan oleh Station/Utensil untuk mendapatkan nama
    String getName();
    // digunakan oleh Station/Utensil untuk ngecek status
    IngredientState getState();
}
