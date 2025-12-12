package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable;

public class BoilingPot extends BaseCookingDevice {

    public BoilingPot() {
        super("Boiling Pot", 1);
    }

    @Override
    public boolean canAccept(Cookable item) {
        if (!(item instanceof Processable)) return false;
        
        String name = ((Processable)item).getName().toLowerCase();
        
        // BOILING: Hanya Pasta dan Tomato
        if (name.contains("pasta") || name.contains("tomato")) {
            // Terima jika bisa dimasak (Raw/Chopped)
            return item.canBeCooked();
        }
        
        System.out.println("[!] Boiling Pot menolak " + name);
        return false;
    }
}