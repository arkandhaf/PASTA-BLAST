package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.Dish; 
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.manager.OrderManager; 


public class ServingStation extends Station {

    private final OrderManager orderManager;
    private final PlateStorage plateStorageRef;

    // Konstruktor: Menerima OrderManager (Singleton atau Instance) dan PlateStorage
    public ServingStation(int x, int y, PlateStorage storage) {
        super(x, y, "Serving Counter", "S");
        // Kita menggunakan Singleton OrderManager karena lebih umum untuk Manager
        this.orderManager = OrderManager.getInstance();
        this.plateStorageRef = storage;
    }

    @Override
    public void interact(Chef chef) {
        
        if (!chef.hasItem() || !(chef.getHeldItem() instanceof Plate)) {
            System.out.println("⚠️ [Serving] Bawa piring berisi makanan ke sini!");
            return;
        }

        Plate plate = (Plate) chef.getHeldItem();

        // check plate
        if (plate.getContents().isEmpty()) {
            System.out.println("❌ [Serving] Jangan sajikan piring kosong!");
            chef.setHeldItem(null); 
            return;
        }

        if (plate.isDirty()) {
            System.out.println("🗑️ [Serving] Piring ini kotor (sisa). Silakan cuci dulu!");
            chef.setHeldItem(null); 
            return;
        }
        
        Processable content = plate.getContents().get(0);
        if (!(content instanceof Dish)) {
            System.out.println("⚠️ [Serving] Makanan belum dirakit sempurna. Silakan ke Assembly Station dulu!");
            chef.setHeldItem(null); // Bahan mentah dibuang
            return;
        }
        
        Dish dish = (Dish) content;


        

        boolean isCorrectOrder = this.orderManager.findAndCompleteOrder(dish);
        
        if (isCorrectOrder) {
            System.out.println("🎉 >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan Tepat! Skor bertambah dan order terhapus.");
        
        } else {
            System.out.println("❌ >>> [Serving] Disajikan: " + dish.getRecipeName() + " — Pesanan SALAH! Makanan dimakan Kak Jendra. Order tidak dihapus.");
        }
        
    


        plate.markDirty();
       
        plate.clearContents(); 

        
        if (plateStorageRef != null) {
            
            plateStorageRef.addDirtyPlateFromServing(plate);
            System.out.println("[Serving] Piring kotor kembali ke Storage.");
        }

        
        chef.setHeldItem(null);
    }
}