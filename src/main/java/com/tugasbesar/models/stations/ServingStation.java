package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.Dish; 
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.manager.OrderManager; 

public class ServingStation extends Station {

    // Kita hapus PlateStorage sementara biar gak error
    public ServingStation(int x, int y) {
        super(x, y, "Serving Counter", "S");
    }

    // Constructor overload buat MapParser (biar kompatibel)
    public ServingStation(int x, int y, Object ignored) {
        super(x, y, "Serving Counter", "S");
    }

    @Override
    public void interact(Chef chef) {
        if (!chef.hasItem() || !(chef.getHeldItem() instanceof Plate)) {
            System.out.println("⚠️ [Serving] Bawa piring berisi makanan ke sini!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        if (plate.getContents().isEmpty() || plate.isDirty()) {
            System.out.println("⚠️ [Serving] Piring kosong atau kotor!");
            return;
        }
        
        Processable content = plate.getContents().get(0);
        if (!(content instanceof Dish)) {
            System.out.println("⚠️ [Serving] Makanan belum dirakit sempurna.");
            return;
        }
        
        Dish dish = (Dish) content;

        boolean isCorrectOrder = OrderManager.getInstance().checkOrder(dish);
        
        if (isCorrectOrder) {
            System.out.println("🎉 [Serving] BENAR! " + dish.getRecipeName() + " disajikan.");
        } else {
            System.out.println("❌ [Serving] SALAH! Tidak ada pesanan " + dish.getRecipeName());
        }

        // Reset Piring (Jadi kotor & kosong)
        plate.markDirty();       
        plate.clearContents();   
        
        // Karena gak ada storage, piring kotor tetap di tangan Chef
        System.out.println("ℹ️ [Serving] Piring kotor dikembalikan ke Chef.");
    }
}