package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import java.awt.Color;

public class TrashStation extends Station {

    public TrashStation(int x, int y) {
        super(x, y, "Trash Can", "T");
    }

    @Override
    public void interact(Chef chef) {
        if (!chef.hasItem()) {
            System.out.println("⚠️ [Trash] Tidak ada yang bisa dibuang.");
            notifyInteraction("Nothing to toss", new Color(255, 193, 7));
            return;
        }

        Item item = chef.getHeldItem();

        // KASUS 1: PIRING (Buang isinya saja, piringnya jangan dibuang)
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            if (!plate.getContents().isEmpty()) {
                plate.clearContents(); // Hapus semua makanan di piring
                System.out.println("🗑️ [Trash] Sisa makanan dibuang dari Piring.");
                notifyInteraction(plate, "Cleared plate", new Color(244, 143, 177));
            } else {
                System.out.println("⚠️ [Trash] Piring sudah kosong.");
                notifyInteraction(plate, "Already empty", new Color(129, 212, 250));
            }
            return;
        }

        // KASUS 2: PANCI/WAJAN (Buang isinya saja, alatnya jangan)
        if (item instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) item;
            if (!utensil.isEmpty()) {
                utensil.takeItem(); // Ambil isinya dan hilangkan
                System.out.println("🔥 [Trash] Masakan gosong dibuang dari " + utensil.getName());
                notifyInteraction((Item) utensil, "Cleared " + utensil.getName(), new Color(244, 67, 54));
            } else {
                System.out.println("⚠️ [Trash] " + utensil.getName() + " sudah kosong.");
                notifyInteraction((Item) utensil, "Already empty", new Color(129, 212, 250));
            }
            return;
        }

        // KASUS 3: BAHAN MAKANAN / ITEM LAIN (Buang Itemnya)
        // Hapus item dari tangan chef
        chef.setHeldItem(null);
        System.out.println("🗑️ [Trash] " + item.getName() + " dibuang ke tempat sampah.");
        notifyInteraction(item, "Discarded", new Color(244, 67, 54));
    }
}