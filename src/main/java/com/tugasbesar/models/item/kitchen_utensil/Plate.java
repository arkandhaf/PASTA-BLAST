package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.KitchenUtensil;
import com.tugasbesar.models.interfaces.Preparable;
import java.util.stream.Collectors;

public class Plate extends KitchenUtensil {

    // STATUS KEBERSIHAN
    // True = Bekas makan / berminyak (harus dicuci)
    // False = Bersih / Baru (siap dipakai)
    private boolean isDirty;

    public Plate() {
        super("Plate"); // Panggil parent untuk siapkan List contents
        this.isDirty = false; // Default bersih
    }

    // --- INTERAKSI MENAMBAH BAHAN ---

    @Override
    public void addIngredient(Preparable item) {
        // Cek dulu, boleh ga dimasukin?
        if (canAccept(item)) {
            // Panggil method 'addIngredient' milik Parent (KitchenUtensil)
            // Parent yang akan memasukkan item ke dalam List contents
            super.addIngredient(item); 
        } else {
            System.out.println("[!] Piring kotor! Cuci dulu di Washing Station.");
        }
    }

    // Validasi khusus Plate
    public boolean canAccept(Preparable item) {
        // Piring kotor gaboleh diisi makanan
        return !isDirty;
    }

    // --- STATE MANAGEMENT (Cuci & Makan) ---

    // Dipanggil oleh WashingStation (Proses Cuci Selesai)
    public void wash() {
        super.clearContents(); // Kosongkan isi (pakai method parent)
        this.isDirty = false;  // Ubah status jadi BERSIH
    }

    // Dipanggil oleh ServingStation (Proses Makan Selesai)
    public void markDirty() {
        super.clearContents(); // Makanan dimakan habis
        this.isDirty = true;   // Piring jadi KOTOR
    }

    public boolean isClean() {
        return !isDirty;
    }

    // --- LOGIC NAMA HIDANGAN (PENTING BUAT SKOR) ---
    // Menggabungkan isi List menjadi satu String nama menu
    public String getDishName() {
        if (isDirty) return "Dirty Plate";
        if (contents.isEmpty()) return "Empty Plate";

        // Menggabungkan nama bahan-bahan yang ada di piring
        // Contoh output: "Pasta + Beef"
        return contents.stream()
                .map(item -> item.getName())
                .collect(Collectors.joining(" + "));
    }

    @Override
    public String toString() {
        if (isDirty) return "Plate (Dirty)";
        if (contents.isEmpty()) return "Plate (Clean)";
        
        return "Plate [" + getDishName() + "]";
    }
}