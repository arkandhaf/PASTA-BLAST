package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 

import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 

import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
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

     
            // cek apakah item di tangan Cookable
            if (chef.hasItem() && hand instanceof Cookable) { 
                Cookable ingredient = (Cookable) hand;

                
                if (utensil.canAccept(ingredient)) { 
                    utensil.addIngredient(ingredient);
                    utensil.startCooking(); // auto cook 

                    chef.setHeldItem(null);
                    System.out.println("[Stove] " + ((Processable)ingredient).getName() + " masuk ke " + utensil.getName());
                    return;
                }
            }
            
            //AMBIL HASIL MASAKAN (Matang atau Gosong)
            // Chef tangan kosong mengambil hasil dari utensil di stove
            if (!chef.hasItem() && !utensil.isEmpty()) {
                
            
                Processable result = utensil.getContents().get(0); 

                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    
                    // ambil item dari Utensil 
                    Item takenItem = utensil.takeItem(); 
                    chef.setHeldItem(takenItem);
                    
            
                    System.out.println("[Stove] " + chef.getName() + " mengambil hasil masakan: " + result.getName() + " (" + result.getState() + ")");
                    return;
                }
            }
        }
        

        // 3. taruh Panci / angkat Panci / safety 
        // safety: cegah taruh barang sembarangan di stove kosong (takda utensil)
        if (isEmpty() && chef.hasItem() && !(chef.getHeldItem() instanceof BaseCookingDevice)) {
            System.out.println("[!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " di api.");
            return;
        }

        // angkat / taruh Panci
        defaultInteract(chef);
    }

    @Override
    public void update() {
        // auto-cook (timer jalan terus)
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick();
        }
    }
}