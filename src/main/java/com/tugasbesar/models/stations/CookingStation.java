package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import java.awt.Color;
import java.awt.Graphics2D;

// Import ini sementara dimatikan biar gak error (karena filenya belum ada)
// import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 

public class CookingStation extends Station {

    // --- VARIABEL SIMULASI MASAK ---
    private boolean isCooking = false;
    private int cookingTimer = 0;
    private final int MAX_COOK_TIME = 120; // 2 Detik (60 FPS * 2)

    // Constructor Sederhana (Agar cocok dengan GamePanel setupGame)
    public CookingStation(int x, int y) {
        super(x, y, "Stove", "S"); // "S" simbol di layar
    }

    @Override
    public void interact(Chef chef) {
        Item handItem = chef.getHeldItem();

        // 1. TARUH BARANG & MULAI MASAK
        // Syarat: Chef bawa barang & Kompor kosong
        if (handItem != null && isEmpty()) {
            placeItem(handItem);       // Taruh item ke station
            chef.setHeldItem(null);    // Kosongkan tangan chef
            
            // Mulai Timer Masak
            isCooking = true;
            cookingTimer = MAX_COOK_TIME;
            
            System.out.println("🔥 [Stove] Mulai memasak " + handItem.getName() + "...");
        }
        
        // 2. AMBIL BARANG
        // Syarat: Chef tangan kosong & Kompor ada isi
        else if (handItem == null && !isEmpty()) {
            
            // Opsional: Kalau mau dipaksa nunggu matang dulu
            if (isCooking) {
                System.out.println("⚠️ [Stove] Sabar! Belum matang! (" + cookingTimer + ")");
            } else {
                chef.setHeldItem(takeItem()); // Ambil item balik
                System.out.println("✅ [Stove] Mengambil " + chef.getHeldItem().getName());
                
                // Reset status masak
                isCooking = false;
                cookingTimer = 0;
            }
        }
    }

    @Override
    public void update() {
        // --- LOGIKA TIMER MASAK ---
        if (isCooking) {
            cookingTimer--;
            
            // Kalau waktu habis
            if (cookingTimer <= 0) {
                isCooking = false;
                cookingTimer = 0;
                System.out.println("🔔 [Stove] TING! Makanan Matang!");
                
                // Nanti di sini logic ubah: Tomato -> TomatoSauce
            }
        }
        
        /* --- LOGIKA ASLI DEPA (NANTI DIAKTIFKAN) ---
           Kalau BaseCookingDevice sudah ada, pakai ini:
           
           if (itemOnStation instanceof BaseCookingDevice) {
               ((BaseCookingDevice) itemOnStation).processCookingTick();
           } 
        */
    }

    // Override Draw untuk nambahin efek visual pas lagi masak
    @Override
    public void draw(Graphics2D g2) {
        // Panggil gambar kotak dasar dari Station
        super.draw(g2); 
        
        int tileSize = 48; // Hardcode size sementara

        // Kalau lagi masak, gambar Bar Loading / Api
        if (isCooking) {
            // Gambar Api (Kotak Orange Kecil)
            g2.setColor(Color.ORANGE);
            g2.fillRect(posX * tileSize + 5, posY * tileSize + 5, 10, 10);
            
            // Gambar Progress Bar Sederhana
            g2.setColor(Color.GREEN);
            int barWidth = (int) (((double) cookingTimer / MAX_COOK_TIME) * tileSize);
            g2.fillRect(posX * tileSize, posY * tileSize - 5, barWidth, 5);
        }
    }
}