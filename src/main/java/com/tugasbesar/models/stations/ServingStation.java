package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
// import com.tugasbesar.core.models.manager.OrderManager; // Buka komen ini nanti kalau sudah ada Manager

public class ServingStation extends Station {

    private PlateStorage plateStorageRef;

    public ServingStation(int x, int y, PlateStorage storage) {
        super(x, y, "Serving Counter", "S");
        this.plateStorageRef = storage;
    }

    @Override
    public void interact(Chef chef) {
        // 1. Validasi: Chef harus bawa item
        if (!chef.hasItem()) {
            System.out.println("[Serving] Mana makanannya? Bawa piring berisi makanan ke sini.");
            return;
        }

        // 2. Validasi: Item harus Piring
        if (!(chef.getHeldItem() instanceof Plate)) {
            System.out.println("[Serving] Makanan harus ditaruh di Piring dulu!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        // 3. Validasi: Piring tidak boleh kosong
        if (plate.getContents().isEmpty()) {
            System.out.println("[Serving] Jangan sajikan piring kosong! (Pelanggan Marah)");
            // Bisa kurangi skor di sini
            return;
        }

        // ========================================================
        // 4. LOGIKA PENILAIAN (ORDER CHECKING)
        // ========================================================
        
        // Nanti kamu bisa hubungkan dengan OrderManager di sini.
        // Contoh logika sederhana sementara:
        System.out.println(">>> [Serving] Makanan disajikan!"); 
        
        // Cek isi piring (Misal: Apakah ini Pasta Carbonara?)
        // Dish dish = plate.getDish(); 
        // boolean isCorrect = OrderManager.getInstance().checkOrder(dish);
        
        // if (isCorrect) ScoreManager.add(100);
        // else ScoreManager.minus(50);
        
        // ========================================================
        // 5. BERSIHKAN & KIRIM PIRING KOTOR (SESUAI NOTES)
        // ========================================================

        // Makanan dimakan (hapus isi)
        plate.getContents().clear(); 
        
        // Kirim LANGSUNG ke PlateStorage (Sesuai notes kamu: "langsung dikirim")
        if (plateStorageRef != null) {
            plateStorageRef.addDirtyPlateFromServing(plate);
        }

        // Kosongkan tangan Chef
        chef.setHeldItem(null);
    }
}