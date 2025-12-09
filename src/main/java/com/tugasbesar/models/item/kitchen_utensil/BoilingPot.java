package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable;


public class BoilingPot extends BaseCookingDevice {


    public BoilingPot() {
        super("Boiling Pot", 1);
    }

    
    @Override
    public boolean canAccept(Cookable item) {
        

        if (!(item instanceof Processable)) {
             System.out.println("[!] Item tidak dikenali.");
             return false;
        }
        
        String name = ((Processable)item).getName().toLowerCase();
        

        if (name.contains("pasta") || name.contains("tomato")) {
            return item.canBeCooked();
        }
        
        System.out.println("[!] Boiling Pot hanya untuk memasak Pasta atau Tomat!");
        return false;
    }

}