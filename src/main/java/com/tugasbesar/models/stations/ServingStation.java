package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.Dish; 
import com.tugasbesar.models.interfaces.Processable;

// ASUMSI: Import OrderManager dan ScoreManager (jika ada) sudah disiapkan
import com.tugasbesar.models.manager.OrderManager; 
// import com.tugasbesar.core.models.manager.ScoreManager; // Asumsi ada ScoreManager

public class ServingStation extends Station {

    private PlateStorage plateStorageRef;

    public ServingStation(int x, int y, PlateStorage storage) {
        super(x, y, "Serving Counter", "S");
        this.plateStorageRef = storage;
    }

    @Override
    public void interact(Chef chef) {
        // 1. Validasi Awal (Chef bawa Plate)
        if (!chef.hasItem() || !(chef.getHeldItem() instanceof Plate)) {
            System.out.println("[Serving] Bawa piring berisi makanan ke sini!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        // 2. Validasi Piring
        if (plate.getContents().isEmpty()) {
            System.out.println("[Serving] Jangan sajikan piring kosong! Pelanggan tidak senang.");
            return;
        }

        if (plate.isDirty()) {
            System.out.println("[Serving] Piring ini kotor (sisa). Silakan cuci dulu!");
            return;
        }
        
        // 3. Validasi Dish (Apakah sudah dirakit menjadi Dish?)
        Processable content = plate.getContents().get(0);
        if (!(content instanceof Dish)) {
            System.out.println("[Serving] Makanan belum dirakit sempurna. Silakan ke Assembly Station dulu!");
            return;
        }
        
        Dish dish = (Dish) content;


        
        // Cek ke OrderManager apakah Dish ini cocok dengan salah satu pesanan aktif
        boolean isCorrectOrder = OrderManager.getInstance().checkOrder(dish);
        
        if (isCorrectOrder) {
            System.out.println("🎉 >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan Tepat! Skor bertambah.");

        } else {
            System.out.println("❌ >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan SALAH/Kadaluarsa! Skor berkurang.");
  
        }
    


        plate.markDirty();
    
        if (plateStorageRef != null) {
            plateStorageRef.addDirtyPlateFromServing(plate);
            System.out.println("[Serving] Piring kotor kembali ke Storage.");
        }

        // Kosongkan tangan Chef
        chef.setHeldItem(null);
    }
}