package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;

public class PlateStorage extends Station {

    // Stack tunggal: Bisa isi piring BERSIH atau KOTOR
    private Stack<Plate> plateStack;

    public PlateStorage(int x, int y) {
        // x, y, Nama, Symbol "P"
        super(x, y, "Plate Storage", "P");
        
        this.plateStack = new Stack<>();
        
        // RULE Awal: 5 Piring Bersih
        for (int i = 0; i < 5; i++) {
            Plate p = new Plate();
            // p.setClean(true); // Asumsi default plate itu bersih
            plateStack.push(p);
        }
    }

    @Override
    public void interact(Chef chef) {
        // RULE 1: Tidak dapat melakukan drop item apapun (NO DROP)
        if (chef.hasItem()) {
            System.out.println("[!] Gaboleh naruh barang di sini (Storage No Drop).");
            return;
        }

        // RULE 2: Ambil Piring (Paling Atas)
        if (!plateStack.isEmpty()) {
            // Kita INTIP (Peek) dulu piring paling atas
            Plate topPlate = plateStack.peek();
            
            // Cek status kotor/bersih (Logic ini tergantung class Plate kamu)
            boolean isDirty = !topPlate.getContents().isEmpty(); 
            // Atau kalau kamu punya variabel boolean isClean: if (!topPlate.isClean()) ...

            // AMBIL (Pop)
            chef.setHeldItem(plateStack.pop());
            
            if (isDirty) {
                System.out.println("[Storage] " + chef.getName() + " terpaksa mengambil PIRING KOTOR (Cuci dulu!)");
            } else {
                System.out.println("[Storage] " + chef.getName() + " mengambil Piring Bersih.");
            }
            
            System.out.println("   (Sisa tumpukan: " + plateStack.size() + ")");
        } else {
            System.out.println("[!] Storage Kosong melompong.");
        }
    }

    // --- SYSTEM METHOD (Dipanggil oleh ServingStation) ---
    // RULE 3: Piring kotor dari serving langsung ke ATAS stack
    public void addDirtyPlateFromServing(Plate p) {
        // Hapus sisa makanan (karena sudah dimakan customer)
        p.getContents().clear();
        
        // Tandai kotor (jika class Plate punya status)
        // p.setClean(false); 
        // Atau kita anggap piring kosong yang masuk dari serving itu "Dirty" secara logika game
        
        plateStack.push(p);
        System.out.println(">>> [Auto] Piring kotor masuk ke tumpukan paling atas Storage.");
    }
}