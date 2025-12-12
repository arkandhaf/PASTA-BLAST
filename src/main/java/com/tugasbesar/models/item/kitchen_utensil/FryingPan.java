package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 

public class FryingPan extends BaseCookingDevice {

    public FryingPan() {
        super("Frying Pan", 1); 
    }

    @Override
    public boolean canAccept(Cookable item) {
        if (!(item instanceof Processable)) return false;

        String name = ((Processable)item).getName().toLowerCase();

        // FRYING: Beef, Fish, Shrimp
        if (name.contains("beef") || name.contains("fish") || name.contains("shrimp")) {
            return item.canBeCooked();
        }
        
        System.out.println("[!] Frying Pan menolak " + name);
        return false;
    }
}