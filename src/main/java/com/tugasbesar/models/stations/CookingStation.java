package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.enums.IngredientState; 
import com.tugasbesar.models.item.Dish; 

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
            BaseCookingDevice utensilOnTable = (BaseCookingDevice) tableItem;

            
            if (chef.hasItem() && hand instanceof Plate && !utensilOnTable.isEmpty()) {
                Plate plate = (Plate) hand;
                Processable ingredient = (Processable) utensilOnTable.getContents().get(0);

             
                boolean isCooked = utensilOnTable.isCookedOrBurned();
                boolean isPlateClean = !plate.isDirty();
                boolean plateHasNoDish = !plate.getContents().stream().anyMatch(content -> content instanceof Dish);

                if (isCooked && isPlateClean && plateHasNoDish && plate.canAccept(ingredient)) {
                    
                    Processable ingredientToPlate = (Processable) utensilOnTable.takeItem(); 
                    plate.addIngredient(ingredientToPlate); 
                    
                    System.out.println("✅ [Stove: Plating] " + ingredientToPlate.getName() + " dari " + utensilOnTable.getName() + " pindah ke piring di tangan.");
                    return;
                } else if (!isPlateClean) {
                    System.out.println("⚠️ [Stove: Plating] Piring di tangan Chef kotor!");
                    return;
                } else if (!plateHasNoDish) {
                    System.out.println("⚠️ [Stove: Plating] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
                    return;
                }
            }
            
           
            if (chef.hasItem() && hand instanceof Cookable && utensilOnTable.isEmpty()) { 
                Cookable ingredient = (Cookable) hand;
                if (utensilOnTable.canAccept(ingredient)) { 
                    utensilOnTable.addIngredient(ingredient);
                    utensilOnTable.startCooking();
                    chef.setHeldItem(null);
                    System.out.println("🔥 [Stove] " + ((Processable)ingredient).getName() + " masuk ke " + utensilOnTable.getName() + ". Memasak dimulai.");
                    return;
                }
            }
            
            
            if (!chef.hasItem() && !utensilOnTable.isEmpty()) {
                Processable result = utensilOnTable.getContents().get(0); 
                
                
                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    Item takenItem = utensilOnTable.takeItem(); 
                    chef.setHeldItem(takenItem);
                    
                    System.out.println("🥄 [Stove] " + chef.getName() + " mengambil hasil masakan: " + result.getName() + " (" + result.getState() + ")");
                    return;
                } else {
                    // Masih RAW/Setengah Matang, Chef tidak bisa mengambilnya.
                    System.out.println("⏰ [Stove] Masakan belum matang!");
                    return;
                }
            }

            
            if (chef.hasItem() && hand instanceof BaseCookingDevice) {
                // Lakukan SWAP item, karena tableItem sudah pasti BaseCookingDevice di blok ini
                Item temp = chef.getHeldItem();
                chef.setHeldItem(itemOnStation); 
                itemOnStation = temp; 
                
                System.out.println("🔄 [Stove: Swap] Menukar Base Cooking Device di tangan (" + itemOnStation.getName() + ") dengan Utensil di meja (" + chef.getHeldItem().getName() + ").");
                return;
            } 
            
            
            if (chef.hasItem() && hand instanceof Plate) {
                System.out.println("⚠️ [Stove] Piring tidak bisa diletakkan di atas alat masak yang sedang digunakan.");
                return;
            }

        } 

        
     
        

        if (tableItem instanceof Plate && hand instanceof BaseCookingDevice) {
            Plate plate = (Plate) tableItem;
            BaseCookingDevice utensilInHand = (BaseCookingDevice) hand;
            
            if (!utensilInHand.isEmpty() && utensilInHand.isCookedOrBurned()) {
                Processable ingredient = (Processable) utensilInHand.getContents().get(0);

           
                boolean isPlateClean = !plate.isDirty();
                boolean plateHasNoDish = !plate.getContents().stream().anyMatch(content -> content instanceof Dish);

                if (isPlateClean && plateHasNoDish && plate.canAccept(ingredient)) {
                    
                    Processable ingredientToPlate = (Processable) utensilInHand.takeItem(); 
                    plate.addIngredient(ingredientToPlate); 
                    
                    System.out.println("✅ [Stove: Plating] " + ingredientToPlate.getName() + " pindah dari " + utensilInHand.getName() + " ke piring di meja.");
                    return;
                } else if (!isPlateClean) {
                    System.out.println("⚠️ [Stove: Plating] Piring di meja kotor!");
                    return;
                } else if (!plateHasNoDish) {
                    System.out.println("⚠️ [Stove: Plating] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
                    return;
                }
            }
        }


        if (!chef.hasItem() && tableItem instanceof Plate) {
            chef.setHeldItem(takeItem());
            System.out.println("[Stove: Take] Mengambil " + chef.getHeldItem().getName() + " dari meja.");
            return;
        }

        // G. Gagal: Taruh item non-utensil/non-plate
        if (chef.hasItem() && !(hand instanceof BaseCookingDevice || hand instanceof Plate) && tableItem instanceof BaseCookingDevice) {
            System.out.println("[!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " langsung di kompor.");
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