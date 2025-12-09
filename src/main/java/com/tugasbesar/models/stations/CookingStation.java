package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.enums.IngredientState; 


public class CookingStation extends Station {

    public CookingStation(int x, int y, BaseCookingDevice startingUtensil) {
        super(x, y, "Stove", "R"); 
        this.itemOnStation = startingUtensil; 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        if (tableItem instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) tableItem;
            
            // PLATING: Piring di tangan Chef
            if (chef.hasItem() && hand instanceof Plate && utensil.isCookedOrBurned()) {
                Plate plate = (Plate) hand;
                if (!plate.isDirty() && plate.canAccept(utensil.getContents().get(0))) {
                    
                    Processable ingredientToPlate = (Processable) utensil.takeItem(); 
                    plate.addIngredient(ingredientToPlate);            
                    
                    System.out.println("[Stove: Plating] " + ingredientToPlate.getName() + " dari " + utensil.getName() + " pindah ke piring di tangan.");
                    return;
                }
            }
            
            
            if (chef.hasItem() && hand instanceof Cookable) { 
                Cookable ingredient = (Cookable) hand;
                if (utensil.canAccept(ingredient)) { 
                    utensil.addIngredient(ingredient);
                    utensil.startCooking();
                    chef.setHeldItem(null);
                    System.out.println("[Stove] " + ((Processable)ingredient).getName() + " masuk ke " + utensil.getName());
                    return;
                }
            }
            
            
            if (!chef.hasItem() && !utensil.isEmpty()) {
                Processable result = utensil.getContents().get(0); 
                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    Item takenItem = utensil.takeItem(); 
                    chef.setHeldItem(takenItem);
                    System.out.println("[Stove] " + chef.getName() + " mengambil hasil masakan: " + result.getName() + " (" + result.getState() + ")");
                    return;
                }
            }
        }
        
       
        // PLATING: Piring berada di meja
        if (tableItem instanceof Plate && hand instanceof BaseCookingDevice && !((BaseCookingDevice)hand).isEmpty()) {
            Plate plate = (Plate) tableItem;
            BaseCookingDevice utensil = (BaseCookingDevice) hand;
            
            if (utensil.isCookedOrBurned() && !plate.isDirty() && plate.canAccept(utensil.getContents().get(0))) {
                
                Processable ingredientToPlate = (Processable) utensil.takeItem(); 
                plate.addIngredient(ingredientToPlate);            
                
                System.out.println("[Stove: Plating] " + ingredientToPlate.getName() + " pindah dari " + utensil.getName() + " ke piring di meja.");
                return;
            }
        }

 
        if (isEmpty() && chef.hasItem() && !(chef.getHeldItem() instanceof BaseCookingDevice)) {
            System.out.println("[!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " di api.");
            return;
        }

      
        defaultInteract(chef);
    }

    @Override
    public void update() {
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick();
        }
    }
}