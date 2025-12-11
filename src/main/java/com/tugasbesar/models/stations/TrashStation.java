package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;

public class TrashStation extends Station {

    public TrashStation(int x, int y) {
        super(x, y, "Trash Can", "T");
    }

    @Override
    public void interact(Chef chef) {
        if (!chef.hasItem()) {
            System.out.println("⚠️ [Trash] Tidak ada yang bisa dibuang.");
            return;
        }

        Item item = chef.getHeldItem();

        // KASUS 1: PIRING (Buang isinya saja, piringnya jangan dibuang)
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            if (!plate.getContents().isEmpty()) {
                plate.clearContents(); // Hapus semua makanan di piring
                System.out.println("🗑️ [Trash] Sisa makanan dibuang dari Piring.");
            } else {
                System.out.println("⚠️ [Trash] Piring sudah kosong.");
            }
            return;
        }

        // KASUS 2: PANCI/WAJAN (Buang isinya saja, alatnya jangan)
        if (item instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) item;
            if (!utensil.isEmpty()) {
                utensil.takeItem(); // Ambil isinya dan hilangkan
                System.out.println("🔥 [Trash] Masakan gosong dibuang dari " + utensil.getName());
            } else {
                System.out.println("⚠️ [Trash] " + utensil.getName() + " sudah kosong.");
            }
            return;
        }

        // KASUS 3: BAHAN MAKANAN / ITEM LAIN (Buang Itemnya)
        // Hapus item dari tangan chef
        chef.setHeldItem(null);
        System.out.println("🗑️ [Trash] " + item.getName() + " dibuang ke tempat sampah.");
    }
}