package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.KitchenUtensil;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.interfaces.Placeable; 

import java.util.stream.Collectors;

public class Plate extends KitchenUtensil {

    private boolean isDirty;

    public Plate() {
        super("Plate"); 
        this.isDirty = false; 
    }

    // --- LOGIC UTAMA (Wajib Ada untuk Station) ---

    // Dipanggil oleh WashingStation saat selesai mencuci
    public void clean() {
        this.contents.clear(); // Kosongkan isi
        this.isDirty = false;  // Ubah status jadi bersih
        System.out.println("[Plate] Piring dicuci dan sekarang bersih.");
    }

    // Dipanggil oleh ServingStation setelah makanan disajikan
    public void markDirty() {
        this.isDirty = true; 
        // Biasanya piring kotor itu kosong dari makanan, tapi punya status 'dirty'
        // Kita tidak clear() disini, biar ServingStation yang ngatur logika sisa makanannya
    }

    // Dipanggil oleh TrashStation & WashingStation
    @Override
    public void clearContents() {
        super.clearContents(); // Hapus semua ingredient di dalamnya
    }

    // ------------------------------------------------

    @Override
    public void addIngredient(Processable item) {
        if (canAccept(item)) {
            super.addIngredient(item); 
            System.out.println("[Plate] " + item.getName() + " diletakkan di piring.");
        } 
    }

    @Override
    public boolean canAccept(Processable item) {
        // 1. Cek Kebersihan
        if (isDirty) {
            System.out.println("[!] Piring kotor! Cuci dulu di Washing Station.");
            return false;
        }

        // 2. Cek apakah item bisa ditaruh (Interface Placeable)
        if (!(item instanceof Placeable)) {
            System.out.println("[!] Item ini tidak dapat diletakkan di piring.");
            return false;
        }
        
        // 3. Cek status item (misal: Steak Mentah gaboleh di piring)
        Placeable placeableItem = (Placeable) item;
        if (!placeableItem.canBePlacedOnPlate()) {
            System.out.println("[!] Item belum siap plating (masih mentah/gosong).");
            return false;
        }
        
        return true;
    }

    // --- HELPER METHODS ---

    public boolean isDirty() {
        return isDirty;
    }

    public String getDishName() {
        if (isDirty) return "Dirty Plate";
        if (contents.isEmpty()) return "Empty Plate";

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