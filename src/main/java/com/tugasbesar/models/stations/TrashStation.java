package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;

public class TrashStation extends Station {

    public TrashStation(int x, int y) {
        // x, y, Nama, Symbol "T"
        super(x, y, "Trash Can", "T");
    }

    @Override
    public void interact(Chef chef) {
        if (!chef.hasItem()) return;

        Item item = chef.getHeldItem();

        // ==========================================
        // 1. KASUS PIRING (Buang isi, Piring tetap)
        // ==========================================
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            
            if (!plate.getContents().isEmpty()) {
                plate.getContents().clear(); // Kosongkan isi
                // plate.setClean(false); // Opsional: Piring jadi kotor (minyak)
                
                System.out.println("[Trash] Membuang sisa makanan dari Piring.");
            } else {
                System.out.println("[!] Piring sudah kosong, tidak ada yang dibuang.");
            }
            return; // Selesai, jangan buang piringnya
        }

        // ==========================================
        // 2. KASUS PANCI/WAJAN (Buang isi, Alat tetap)
        // ==========================================
        if (item instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) item;
            
            // Kita cek apakah ada isinya (lewat akses ke list contents)
            // Asumsi KitchenUtensil punya akses ke list contents
            if (!utensil.getContents().isEmpty()) {
                utensil.getContents().clear(); // Kosongkan masakan (gosong/jadi)
                
                // PENTING: Reset timer di utensil (kalau ada method reset)
                // utensil.resetCookingTimer(); 
                
                System.out.println("[Trash] Membuang masakan dari " + utensil.getName());
            } else {
                System.out.println("[!] " + utensil.getName() + " sudah bersih/kosong.");
            }
            return; // Selesai, jangan buang pancinya
        }

        // ==========================================
        // 3. KASUS BAHAN / DAGING (Buang Itemnya)
        // ==========================================
        // Kalau Chef bawa Tomat/Daging langsung di tangan -> Hancurkan
        chef.setHeldItem(null);
        System.out.println("[Trash] " + item.getName() + " dibuang ke tempat sampah.");
    }
}