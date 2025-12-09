package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Choppable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.enums.IngredientState;

public class CuttingStation extends Station {
    
    private int cutProgress = 0;
    private final int CUT_SPEED = 25; 

    public CuttingStation(int x, int y) {
        super(x, y, "Cutting Station", "C");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // Cek item di meja
        if (tableItem instanceof Processable) {
            Processable item = (Processable) tableItem;
            
            // 1. AMBIL HASIL (CHOPPED) 
            if (item.getState() == IngredientState.CHOPPED && !chef.hasItem()) {
                chef.setHeldItem(takeItem());
                cutProgress = 0;
                System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " (CHOPPED)");
                return;
            }
        }
        
        // 2. TARUH BAHAN (RAW) 
        if (isEmpty() && chef.hasItem()) {
            // Item harus Choppable DAN canBeChopped (yakni statusnya RAW)
            if (hand instanceof Choppable) {
                Choppable chopItem = (Choppable) hand;
                
                if (chopItem.canBeChopped()) { // Memastikan item RAW dan memang Choppable
                    
                    Item itemToPlace = chef.getHeldItem(); 
                    placeItem(itemToPlace); 
                    chef.setHeldItem(null);
                    cutProgress = 0; // Reset progress saat taruh item baru
                    
                    System.out.println("[Cutting] " + chef.getName() + " menaruh " + itemToPlace.getName());
                } else {
                    // Item Choppable, tapi tidak bisa dipotong (misal: sudah CHOPPED atau non-choppable)
                    System.out.println(">>> [TOLAK] " + chef.getName() + ", item ini sudah diproses atau tidak bisa dipotong!");
                }
            } else {
                // Item di tangan bukan Choppable sama sekali
                System.out.println(">>> [TOLAK] " + chef.getName() + ", item ini tidak bisa dipotong!");
            }
            return;
        }

        // 3. AMBIL BALIK / SWAP (Pembatalan Pemotongan)
        if (!isEmpty() && !chef.hasItem()) {
              // Jika chef mengambil barang balik, progress di-reset (bisa dilanjutkan nanti)
             chef.setHeldItem(takeItem());
             cutProgress = 0; 
             System.out.println("[Cutting] " + chef.getName() + " membatalkan pemotongan & mengambil barang balik.");
             return;
        }
        
        // Logic default (swap/ganti) tidak perlu karena sudah ditangani oleh tiga prioritas di atas
    }

    @Override
    public void update() {
        // SYARAT: harus ada Chef yang berdiri di sini (chefAtStation tidak null)
        if (chefAtStation == null) return;

        // Cek item di meja harus bertipe Choppable
        if (itemOnStation instanceof Choppable) {
            Choppable item = (Choppable) itemOnStation;
            
            // Gunakan canBeChopped() untuk validasi penuh (isChoppable DAN statusnya RAW)
            if (item.canBeChopped()) { 
                
                // Casting ke Processable tidak diperlukan, tapi jika diperlukan untuk nama:
                Processable pItem = (Processable) itemOnStation;
                String workingChef = chefAtStation.getName();
                
                cutProgress += CUT_SPEED;
                
                System.out.println("[" + workingChef + "] " + getProgressBar(20) + 
                                   " Memotong " + pItem.getName());

                // cek Selesai
                if (cutProgress >= 100) {
                    item.chop(); // Mengubah status menjadi CHOPPED
                    cutProgress = 100; // Stabilkan progress di 100
                    System.out.println(">>> [SELESAI] " + workingChef + " berhasil memotong " + pItem.getName() + "!");
                }
            }
        }
    }

    // helper Progress Bar
    public String getProgressBar(int width) {
        int percent = Math.min(100, cutProgress);
        int filled = (int) Math.round((percent / 100.0) * width);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < width; i++) sb.append(i < filled ? '#' : '-');
        sb.append("] ").append(percent).append('%');
        return sb.toString();
    }
}