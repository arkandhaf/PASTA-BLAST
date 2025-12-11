package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.Dish; 
import com.tugasbesar.models.interfaces.Processable;

// ASUMSI: Import OrderManager dan ScoreManager (jika ada) sudah disiapkan
import com.tugasbesar.models.manager.OrderManager; 
// import com.tugasbesar.core.models.manager.ScoreManager; // Asumsi ada ScoreManager

public class ServingStation extends Station {

    private GamePanel gp; 

    public ServingStation(int x, int y, GamePanel gp) {
        super(x, y, "Serving Counter", "S");
        this.gp = gp;
    }

    @Override
    public void interact(Chef chef) {
        // Harus bawa piring
        if (!chef.hasItem() || !(chef.getHeldItem() instanceof Plate)) {
            System.out.println("⚠️ Bawa piring berisi makanan ke sini!");
            if(gp != null) gp.showMessage("Butuh Piring!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        // Piring harus ada isinya (makanan jadi)
        if (plate.getContents().isEmpty() || plate.isDirty()) {
            System.out.println("⚠️ Piring kosong atau kotor!");
            if(gp != null) gp.showMessage("Piring Kosong!");
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
        boolean isCorrectOrder = OrderManager.getInstance().findAndCompleteOrder(dish);
        
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