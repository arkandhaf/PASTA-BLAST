package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Choppable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.kitchen_utensil.Plate; 

public class CuttingStation extends Station {
    
    private int cutProgress = 0;
    private final int CUT_SPEED = 34; 

    public CuttingStation(int x, int y) {
        super(x, y, "Cutting Station", "C");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // --- LOGIC INTERACT TETAP SAMA ---

        if (hand instanceof Plate && tableItem instanceof Processable) {
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) { 
                
                takeItem(); 
                plate.addIngredient(ingredient);
                
                System.out.println("[Cutting: Plating] " + ingredient.getName() + " pindah ke piring di tangan.");
                chef.setBusy(false); 
                cutProgress = 0; 
                return;
            }
        }
        
        // Cek item di meja
        if (tableItem instanceof Processable) {
            Processable item = (Processable) tableItem;
            
            // 2. AMBIL HASIL (CHOPPED) 
            if (item.getState() == IngredientState.CHOPPED && !chef.hasItem()) {
                chef.setHeldItem(takeItem());
                chef.setBusy(false); 
                cutProgress = 0;
                System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " (CHOPPED)");
                return;
            }
            
            // 3. TRIGGER PEMOTONGAN
            if (item instanceof Choppable && ((Choppable)item).canBeChopped() && !chef.isBusy()) {
                chef.setBusy(true); // Chef masuk ke busy state
                System.out.println("[Cutting] " + chef.getName() + " mulai memotong " + item.getName());
                return;
            }
        }
        
        // 4. TARUH BAHAN (RAW) 
        if (isEmpty() && chef.hasItem()) {
            if (hand instanceof Choppable) {
                Choppable chopItem = (Choppable) hand;
                
                if (chopItem.canBeChopped()) { 
                    Item itemToPlace = chef.getHeldItem(); 
                    placeItem(itemToPlace); 
                    chef.setHeldItem(null);
                    cutProgress = 0; 
                    
                    System.out.println("[Cutting] " + chef.getName() + " menaruh " + itemToPlace.getName());
                } else {
                    System.out.println(">>> [TOLAK] " + chef.getName() + ", item ini sudah diproses atau tidak bisa dipotong!");
                }
            } else {
                System.out.println(">>> [TOLAK] " + chef.getName() + ", item ini tidak bisa dipotong!");
            }
            return;
        }

        // 5. Pembatalan Pemotongan / Ambil Balik
        if (!isEmpty() && !chef.hasItem()) {
             chef.setHeldItem(takeItem());
             chef.setBusy(false); // keluar dari busy state
             System.out.println("[Cutting] " + chef.getName() + " membatalkan pemotongan & mengambil barang balik. Progress tersimpan.");
             return;
        }
    }

    @Override
    public void update() {
        if (chefAtStation == null) return;
        if (!chefAtStation.isBusy()) return; 

        if (itemOnStation instanceof Choppable) {
            Choppable item = (Choppable) itemOnStation;
            
            if (item.canBeChopped()) { 
                cutProgress += CUT_SPEED;
                // cek Selesai
                if (cutProgress >= 100) {
                    item.chop(); 
                    cutProgress = 100;
                    chefAtStation.setBusy(false); 
                    // Log ini boleh dipertahankan untuk debug kejadian penting
                    System.out.println(">>> [SELESAI] " + chefAtStation.getName() + " berhasil memotong!");
                }
            } else {
                chefAtStation.setBusy(false); 
            }
        } else {
            chefAtStation.setBusy(false);
        }
    }

    public int getCutProgress() {
        return cutProgress;
    }

}