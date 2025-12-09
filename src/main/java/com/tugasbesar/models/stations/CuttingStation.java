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
        
        // --- 1. MEMBATALKAN PEMOTONGAN (Progress Tersimpan) ---
        // Jika Chef menekan interaksi saat sedang busy, batalkan busy state.
        if (chef.isBusy()){
            chef.setBusy(false);
            System.out.println("[Cutting] " + chef.getName() + " menghentikan pemotongan. Progress tersimpan (" + cutProgress + "%)");
            return;
        }
        
        // --- 2. LOGIC INTERAKSI ITEM DI MEJA (Hanya jika Chef tidak busy) ---
        
        // A. PLATING: Piring di tangan -> Ambil item dari meja
        if (hand instanceof Plate && tableItem instanceof Processable) {
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) { 
                
                Item takenItem = takeItem(); 
                plate.addIngredient((Processable) takenItem); 
                
                System.out.println("[Cutting: Plating] " + ingredient.getName() + " pindah ke piring di tangan.");
                cutProgress = 0; // Reset progress karena item dipindahkan
                return;
            }
        }
        
        // B. Cek item di meja (Untuk Ambil Hasil / Trigger Pemotongan)
        if (tableItem instanceof Choppable && tableItem instanceof Processable) {
            Processable pItem = (Processable) tableItem;
            Choppable cItem = (Choppable) tableItem;
            
            // i. AMBIL HASIL (CHOPPED) 
            if (pItem.getState() == IngredientState.CHOPPED && !chef.hasItem()) {
                chef.setHeldItem(takeItem());
                cutProgress = 0; // Reset progress
                System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " (CHOPPED)");
                return;
            }
            
            // ii. TRIGGER PEMOTONGAN (Hanya jika RAW, bisa dipotong, dan tangan kosong)
            if (pItem.getState() == IngredientState.RAW && cItem.canBeChopped() && !chef.hasItem()) {
                chef.setBusy(true); // Chef masuk ke busy state
                System.out.println("[Cutting] " + chef.getName() + " mulai memotong " + pItem.getName());
                return;
            }
        }
        
        // C. TARUH BAHAN / ITEM DI MEJA 
        if (isEmpty() && chef.hasItem()) {
            
            // i. IZINKAN PLATE (PIRING) DITARUH (Fungsi Meja Assembly)
            if (hand instanceof Plate) {
                Item itemToPlace = chef.getHeldItem(); 
                placeItem(itemToPlace); 
                chef.setHeldItem(null);
                System.out.println("[Cutting] " + chef.getName() + " menaruh Piring di meja.");
                cutProgress = 0; 
                return;
            }
            
            // ii. IZINKAN BAHAN RAW DITARUH
            if (hand instanceof Choppable && hand instanceof Processable) {
                Choppable chopItem = (Choppable) hand;
                Processable pItem = (Processable) hand;
                
                // Pastikan item yang ditaruh adalah RAW dan bisa dipotong
                if (chopItem.canBeChopped() && pItem.getState() == IngredientState.RAW) { 
                    Item itemToPlace = chef.getHeldItem(); 
                    placeItem(itemToPlace); 
                    chef.setHeldItem(null);
                    cutProgress = 0; 
                    
                    System.out.println("[Cutting] " + chef.getName() + " menaruh " + itemToPlace.getName());
                } else {
                    System.out.println(">>> [TOLAK] Item harus RAW dan bisa dipotong!");
                }
            } else {
                 System.out.println(">>> [TOLAK] Item ini tidak bisa dipotong atau ditaruh di stasiun ini!");
            }
            return;
        }

        // D. AMBIL ITEM DARI MEJA (Ambil Balik Item/Piring)
        // Jika stasiun ada item dan tangan chef kosong
        if (!isEmpty() && !chef.hasItem()) {
             chef.setHeldItem(takeItem());
             // Busy state sudah di-handle di Poin 1 jika Chef sedang memotong.
             System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
             return;
        }
    }

    @Override
    public void update() {
        // Hanya proses jika Chef berada di stasiun DAN sedang busy
        if (chefAtStation == null || !chefAtStation.isBusy()) return;

        if (itemOnStation instanceof Choppable && itemOnStation instanceof Processable) {
            Processable pItem = (Processable) itemOnStation;
            Choppable cItem = (Choppable) itemOnStation;
            
            // Progres hanya bertambah jika item adalah RAW dan bisa dipotong
            if (cItem.canBeChopped() && pItem.getState() == IngredientState.RAW) { 
                cutProgress += CUT_SPEED;
                
                // Cek Selesai
                if (cutProgress >= 100) {
                    cItem.chop(); // Mengubah state menjadi CHOPPED
                    cutProgress = 100;
                    chefAtStation.setBusy(false); 
                    System.out.println(">>> [SELESAI] " + chefAtStation.getName() + " berhasil memotong " + pItem.getName() + "!");
                }
            } else {
                // Jika Chef masih busy tapi item sudah berubah/tidak valid, batalkan busy state
                chefAtStation.setBusy(false); 
            }
        } else {
            // Jika ada masalah (item hilang/tidak valid) batalkan busy state
            chefAtStation.setBusy(false);
        }
    }

    /**
     * Method untuk mendapatkan kemajuan pemotongan (Progress Bar).
     * @return Nilai progress antara 0 hingga 100.
     */
    public int getCutProgress() {
        return cutProgress;
    }
}