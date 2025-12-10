package com.tugasbesar.models.stations;

import java.awt.Color;
import java.awt.Graphics2D;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 

// --- IMPORTS DARI LOGIC TEMAN (PENTING) ---
import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.enums.IngredientState; 

public class CookingStation extends Station {

    // Constructor 1: Yang dipanggil MapParser/GamePanel (Hanya x, y)
    public CookingStation(int x, int y) {
        super(x, y, "Stove", "S"); 
        this.itemOnStation = null; // Awalnya kosong (atau isi panci default jika mau)
    }

    // Constructor 2: Versi Teman (Jika map mendefinisikan alat masak di awal)
    public CookingStation(int x, int y, BaseCookingDevice startingUtensil) {
        super(x, y, "Stove", "S"); 
        this.itemOnStation = startingUtensil; 
    }

    @Override
    public void interact(Chef chef) {
        // --- PAKAI LOGIC CANGGIH PUNYA TEMAN ---
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // 1. Jika di meja ada Alat Masak (Panci/Wajan)
        if (tableItem instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) tableItem;

            // A. MASUKKAN BAHAN (Chef bawa bahan -> Masuk Panci)
            if (chef.hasItem() && hand instanceof Cookable) { 
                Cookable ingredient = (Cookable) hand;

                if (utensil.canAccept(ingredient)) { 
                    utensil.addIngredient(ingredient);
                    utensil.startCooking(); // Auto cook 

                    chef.setHeldItem(null);
                    System.out.println("🔥 [Stove] " + ((Processable)ingredient).getName() + " masuk ke " + utensil.getName());
                    return;
                }
            }
            
            // B. AMBIL HASIL MASAKAN (Matang atau Gosong)
            // Chef tangan kosong mengambil hasil dari utensil di stove
            if (!chef.hasItem() && !utensil.isEmpty()) {
                
                // Cek isi panci
                Processable result = utensil.getContents().get(0); 

                // Kalau Matang atau Gosong, baru boleh diambil
                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    
                    // Ambil item dari Utensil 
                    Item takenItem = utensil.takeItem(); 
                    chef.setHeldItem(takenItem);
                    
                    System.out.println("✅ [Stove] " + chef.getName() + " mengambil: " + result.getName() + " (" + result.getState() + ")");
                    return;
                }
            }
        }
        
        // 2. SAFETY: Cegah taruh barang sembarangan di stove kosong (kecuali Panci)
        if (isEmpty() && chef.hasItem() && !(chef.getHeldItem() instanceof BaseCookingDevice)) {
            System.out.println("⚠️ [!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " langsung di api.");
            return;
        }

        // 3. DEFAULT: Angkat Panci / Taruh Panci
        defaultInteract(chef);
    }

    @Override
    public void update() {
        // --- PAKAI LOGIC UPDATE PUNYA TEMAN ---
        // Auto-cook (timer jalan terus di dalam objek Panci)
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick();
        }
    }

    // --- PAKAI VISUAL PUNYA KAMU ---
    @Override
    public void draw(Graphics2D g2) {
        // 1. Gambar Kotak Stove & Item di atasnya (Panci)
        super.draw(g2); 
        
        // 2. Visual Tambahan: Progress Bar Masak
        if (itemOnStation instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) itemOnStation;
            
            // Kita perlu akses timer di dalam utensil buat bikin bar
            // Asumsi: utensil punya method getCookProgress() atau sejenisnya.
            // Kalau belum ada, minimal kita kasih tanda kalau lagi masak.
            
            if (!utensil.isEmpty()) { // Kalau panci ada isinya
                int tileSize = 48; // Hardcode size
                
                // Gambar Indikator Sedang Masak (Api Kecil)
                g2.setColor(Color.ORANGE);
                g2.fillRect(posX * tileSize + 5, posY * tileSize + 5, 10, 10);
                
                // Nanti minta Person 2 bikin method: utensil.getCookingPercentage() 
                // biar bisa bikin Loading Bar hijau disini.
            }
        }
    }
}