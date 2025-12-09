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

        // --- 1. INTERAKSI DENGAN BASE COOKING DEVICE DI MEJA (Jika ada Utensil) ---
        if (tableItem instanceof BaseCookingDevice) {
            BaseCookingDevice utensilOnTable = (BaseCookingDevice) tableItem;
            
            // A. PLATING: Piring di tangan Chef
            if (chef.hasItem() && hand instanceof Plate && !utensilOnTable.isEmpty()) {
                Plate plate = (Plate) hand;
                Processable ingredient = (Processable) utensilOnTable.getContents().get(0);

                if (utensilOnTable.isCookedOrBurned() && !plate.isDirty() && plate.canAccept(ingredient)) {
                    
                    Processable ingredientToPlate = (Processable) utensilOnTable.takeItem(); 
                    plate.addIngredient(ingredientToPlate); 
                    
                    System.out.println("[Stove: Plating] " + ingredientToPlate.getName() + " dari " + utensilOnTable.getName() + " pindah ke piring di tangan.");
                    return;
                }
            }
            
            // B. MASUKKAN BAHAN KE UTENSIL DI MEJA
            if (chef.hasItem() && hand instanceof Cookable) { 
                Cookable ingredient = (Cookable) hand;
                if (utensilOnTable.canAccept(ingredient)) { 
                    utensilOnTable.addIngredient(ingredient);
                    utensilOnTable.startCooking();
                    chef.setHeldItem(null);
                    System.out.println("[Stove] " + ((Processable)ingredient).getName() + " masuk ke " + utensilOnTable.getName());
                    return;
                }
            }
            
            // C. AMBIL HASIL MASAKAN DARI UTENSIL DI MEJA (Hanya COOKED/BURNED)
            if (!chef.hasItem() && !utensilOnTable.isEmpty()) {
                Processable result = utensilOnTable.getContents().get(0); 
                
                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    Item takenItem = utensilOnTable.takeItem(); 
                    chef.setHeldItem(takenItem);
                    
                    System.out.println("[Stove] " + chef.getName() + " mengambil hasil masakan: " + result.getName() + " (" + result.getState() + ")");
                    return;
                } else {
                    // Masih RAW/Setengah Matang, Chef tidak bisa mengambilnya.
                    System.out.println("[Stove] Masakan belum matang!");
                    return;
                }
            }
        }
        
        // --- 2. INTERAKSI DENGAN PLATE DI MEJA / UTENSIL DI TANGAN ---
        
        // D. PLATING: Piring berada di meja (Assembly)
        if (tableItem instanceof Plate && hand instanceof BaseCookingDevice) {
            Plate plate = (Plate) tableItem;
            BaseCookingDevice utensilInHand = (BaseCookingDevice) hand;
            
            if (!utensilInHand.isEmpty() && utensilInHand.isCookedOrBurned()) {
                Processable ingredient = (Processable) utensilInHand.getContents().get(0);

                if (!plate.isDirty() && plate.canAccept(ingredient)) {
                    
                    Processable ingredientToPlate = (Processable) utensilInHand.takeItem(); 
                    plate.addIngredient(ingredientToPlate); 
                    
                    System.out.println("[Stove: Plating] " + ingredientToPlate.getName() + " pindah dari " + utensilInHand.getName() + " ke piring di meja.");
                    return;
                }
            }
        }

        // --- 3. SWAP / TARUH / AMBIL UTENSIL/PLATE (Fungsi Meja Assembly) ---
        
        // E. TARUH/SWAP UTENSIL/PLATE 
        if (chef.hasItem() && (hand instanceof BaseCookingDevice || hand instanceof Plate)) {
            // Lakukan SWAP item
            Item temp = chef.getHeldItem();
            chef.setHeldItem(itemOnStation); 
            itemOnStation = temp; 
            
            System.out.println("[Stove: Swap] Menukar item di tangan (" + itemOnStation.getName() + ") dengan item di meja (" + chef.getHeldItem().getName() + ").");
            return;
        }

        // F. AMBIL UTENSIL/PLATE 
        if (!chef.hasItem() && (tableItem instanceof BaseCookingDevice || tableItem instanceof Plate)) {
            chef.setHeldItem(takeItem());
            System.out.println("[Stove: Take] Mengambil " + chef.getHeldItem().getName() + " dari meja.");
            return;
        }

        // G. Gagal: Taruh item non-utensil/non-plate
        if (chef.hasItem() && !(hand instanceof BaseCookingDevice || hand instanceof Plate)) {
            System.out.println("[!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " langsung di kompor.");
            return;
        }

        System.out.println("[!] Interaksi tidak valid.");
    }

    @Override
    public void update() {
        // Proses Memasak HANYA BERJALAN jika ada BaseCookingDevice di stasiun.
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick(); 
        }
    }
}