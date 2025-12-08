package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.interfaces.Preparable;

public class FryingPan extends BaseCookingDevice {

    public FryingPan() {
        super("Frying Pan", 1); // Kapasitas 1 (Goreng satu-satu)
    }

    @Override
    public boolean canAccept(Preparable item) {
        if (!contents.isEmpty()) return false; // Harus kosong
        
        String name = item.getName().toLowerCase();
        
        
        return name.contains("beef") || 
               name.contains("fish") || 
               name.contains("shrimp");
    }
}