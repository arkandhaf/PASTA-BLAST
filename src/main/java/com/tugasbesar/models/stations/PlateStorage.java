package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;

public class PlateStorage extends Station {
    
    // stack untuk menyimpan piring (bersih & kotor)
    // piring yang paling atas adalah yang diambil terlebih dahulu
   
    private Stack<Plate> plateStack;

    public PlateStorage(int x, int y) {
        super(x, y, "Plate Storage", "P");
        
        this.plateStack = new Stack<>();
        
        // asumsi 5 piring bersih
        for (int i = 0; i < 5; i++) {
            Plate p = new Plate();
            // asumsi default plate itu bersih
            plateStack.push(p);
        }
    }

    @Override
    public void interact(Chef chef) {
        // tidak dapat melakukan drop item apapun (NO DROP)
        if (chef.hasItem()) {
            System.out.println("[!] Gaboleh naruh barang di sini (Storage No Drop).");
            return;
        }

        // kalau storage ga kosong, ambil piring paling atas
        if (!plateStack.isEmpty()) {
            // cek piring paling atas
            Plate topPlate = plateStack.peek();
            boolean isDirty = topPlate.isDirty(); 
        
            //kalau kotor, harus dicuci dulu jadi gabisa langsung diambil
            if (isDirty) {
                System.out.println("[!] Tumpukan tertutup PIRING KOTOR! Tidak bisa diambil.");
                return;
            } else {
                chef.setHeldItem(plateStack.pop());
                System.out.println("[Storage] " + chef.getName() + " mengambil Piring Bersih.");
                System.out.println("   (Sisa tumpukan: " + plateStack.size() + ")");
            }
        
        } else {
            System.out.println("[!] Storage Kosong.");
        }
    }

    // piring kotor dari serving langsung ke ATAS stack
    public void addDirtyPlateFromServing(Plate p) {
        
        plateStack.push(p);
        System.out.println(">>> [Auto] Piring kotor masuk ke tumpukan paling atas Storage.");
    }
}