package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.interfaces.Preparable;

public class BoilingPot extends BaseCookingDevice {

    public BoilingPot() {
        super("Boiling Pot", 1);
    }

    @Override
    public boolean canAccept(Preparable item) {
        if (!contents.isEmpty()) return false;

        String name = item.getName().toLowerCase();
        
        // Pasta direbus, Tomat direbus jadi Saus
        return name.contains("pasta") || 
               name.contains("tomato");
    }
}