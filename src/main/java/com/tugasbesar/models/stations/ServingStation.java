package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.core.GamePanel; 

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
        
        // --- LOGIC SEDERHANA VALIDASI ---
        // Nanti disambungkan ke OrderManager untuk cek resep asli.
        // Untuk sekarang, kita anggap apapun yang disajikan itu diterima dulu.
        
        System.out.println("🎉 [Serving] Menyajikan: " + plate.getDishName());
        if(gp != null) gp.showMessage("Serving: " + plate.getDishName());

        // Piring jadi kotor dan kosong
        plate.markDirty();       
        plate.clearContents();   
        
        System.out.println("ℹ️ Piring kotor dikembalikan ke Chef.");
    }
}   