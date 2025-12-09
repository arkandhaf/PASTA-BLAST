package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.Dish; 
import com.tugasbesar.models.interfaces.Processable;


import com.tugasbesar.core.models.manager.OrderManager; 


public class ServingStation extends Station {

    private PlateStorage plateStorageRef;

    public ServingStation(int x, int y, PlateStorage storage) {
        super(x, y, "Serving Counter", "S");
        this.plateStorageRef = storage;
    }

    @Override
    public void interact(Chef chef) {
        // chef bawa plate
        if (!chef.hasItem() || !(chef.getHeldItem() instanceof Plate)) {
            System.out.println("[Serving] Bawa piring berisi makanan ke sini!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        // piring kosong
        if (plate.getContents().isEmpty()) {
            System.out.println("[Serving] Jangan sajikan piring kosong!");
            return;
        }

        // kondisi piring
        if (plate.isDirty()) {
            System.out.println("[Serving] Piring ini kotor (sisa). Silakan cuci dulu!");
            return;
        }
        
        // dah jadi dish apa belum
        Processable content = plate.getContents().get(0);
        if (!(content instanceof Dish)) {
            System.out.println("[Serving] Makanan belum dirakit sempurna. Silakan ke Assembly Station dulu!");
            return;
        }
        
        Dish dish = (Dish) content;


        
        // cek ke OrderManager cocok apa tidak
        boolean isCorrectOrder = OrderManager.getInstance().checkOrder(dish);
        
        if (isCorrectOrder) {
            System.out.println("🎉 >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan Tepat! Skor bertambah.");

        } else {
            System.out.println("❌ >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan SALAH! Skor berkurang.");
  
        }
    


        plate.markDirty();
    
        if (plateStorageRef != null) {
            plateStorageRef.addDirtyPlateFromServing(plate);
            System.out.println("[Serving] Piring kotor kembali ke Storage.");
        }

        // ksongkan tangan Chef
        chef.setHeldItem(null);
    }
}