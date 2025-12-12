package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // =============================================================
        // 1. CHEF TANGAN KOSONG & MEJA ADA PIRING (MASALAH UTAMA KAMU)
        // =============================================================
        if (hand == null && tableItem instanceof Plate) {
            Plate p = (Plate) tableItem;

            // A. CEK APAKAH SUDAH JADI DISH (MAKANAN JADI)?
            // Kalau isinya cuma 1 item dan item itu adalah DISH, berarti siap diambil.
            boolean isFinishedDish = !p.isEmpty() &&
                    p.getContents().size() == 1 &&
                    p.getContents().get(0) instanceof Dish;

            if (isFinishedDish) {
                // AMBIL PIRINGNYA!
                chef.setHeldItem(takeItem());
                System.out.println("⬆️ [Assembly] Mengambil Dish Jadi: " + chef.getHeldItem().getName());
                notifyInteraction(chef.getHeldItem(), "Dish ready", new Color(76, 175, 80));
                return;
            }

            // B. KALAU BELUM JADI DISH, COBA RAKIT
            // Kalau piring ada isinya (bahan-bahan) tapi belum jadi Dish
            if (!p.isEmpty()) {
                boolean success = performAssembly(p);

                // Kalau BERHASIL dirakit jadi Dish, biarkan di meja dulu (biar pemain lihat
                // perubahannya).
                // Pemain harus tekan spasi SEKALI LAGI untuk mengambilnya (masuk ke logika A di
                // atas).
                if (success) {
                    notifyInteraction(tableItem, "Recipe completed", new Color(156, 39, 176));
                    return;
                }

                // Kalau GAGAL dirakit (resep gak cocok), AMBIL AJA PIRINGNYA
                // Biar gak stuck piringnya di meja selamanya.
                chef.setHeldItem(takeItem());
                System.out.println("⬆️ [Assembly] Mengambil Piring (Belum jadi Dish).");
                notifyInteraction(chef.getHeldItem(), "Plate retrieved", new Color(255, 193, 7));
                return;
            }

            // C. KALAU PIRING KOSONG, AMBIL AJA
            chef.setHeldItem(takeItem());
            System.out.println("⬆️ [Assembly] Mengambil Piring Kosong.");
            notifyInteraction(chef.getHeldItem(), "Plate picked", new Color(129, 212, 250));
            return;
        }

        // =============================================================
        // 2. CHEF BAWA PIRING (MAU RAKIT DI TANGAN)
        // =============================================================
        if (hand instanceof Plate && tableItem == null) {
            Plate p = (Plate) hand;
            if (!p.isEmpty()) {
                performAssembly(p); // Coba rakit di tangan
                // Gak perlu return, karena item tetep di tangan
                notifyInteraction(hand, "Check recipe", new Color(156, 39, 176));
            }
            // Kalau mau taruh piring (swap logic di bawah) akan ketahan kalau berhasil
            // rakit?
            // Kita lanjut ke logic taruh di bawah kalau mau naruh.
        }

        // =============================================================
        // 3. PLATING (GABUNG BAHAN)
        // =============================================================
        // Piring di Tangan + Bahan di Meja
        if (hand instanceof Plate && tableItem instanceof Processable) {
            performPlating((Plate) hand, (Processable) tableItem);
            this.itemOnStation = null; // Hapus bahan dari meja
            return;
        }
        // Bahan di Tangan + Piring di Meja
        if (hand instanceof Processable && tableItem instanceof Plate) {
            performPlating((Plate) tableItem, (Processable) hand);
            chef.setHeldItem(null); // Hapus bahan dari tangan
            return;
        }

        // =============================================================
        // 4. TARUH ITEM (STANDAR)
        // =============================================================
        if (hand != null && isEmpty()) {
            // Khusus Assembly, biasanya cuma boleh taruh Piring atau Bahan
            if (hand instanceof Plate || hand instanceof Processable) {
                placeItem(hand);
                chef.setHeldItem(null);
                System.out.println("⬇️ [Assembly] Menaruh " + itemOnStation.getName());
                notifyInteraction(itemOnStation, "Placed", new Color(0, 188, 212));
            } else {
                System.out.println("⚠️ [Assembly] Item ini tidak bisa ditaruh di sini.");
                notifyInteraction("Cannot place", new Color(244, 67, 54));
            }
            return;
        }
    }

    // --- HELPER LOGIC ---

    private void performPlating(Plate plate, Processable item) {
        if (!isValidForPlating(item))
            return;

        // Jangan masukin bahan kalau udah jadi Dish
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("⚠️ Piring sudah ada Hidangan Jadi.");
            notifyInteraction(plate, "Plate already finished", new Color(244, 67, 54));
            return;
        }

        plate.addIngredient(item);
        System.out.println("🥗 Menambahkan " + item.getName() + " ke piring.");
        if (item instanceof Item) {
            notifyInteraction((Item) item, "Added " + item.getName(), new Color(3, 169, 244));
        }
    }

    private boolean isValidForPlating(Processable item) {
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            System.out.println("⚠️ Gagal: Bahan harus matang atau dipotong.");
            if (item instanceof Item) {
                notifyInteraction((Item) item, "Needs prep", new Color(255, 193, 7));
            } else {
                notifyInteraction("Needs prep", new Color(255, 193, 7));
            }
            return false;
        }
        return true;
    }

    // Ubah jadi boolean biar tau sukses atau nggak
    private boolean performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();

        // Cek dulu, jangan-jangan udah jadi Dish
        if (contents.size() == 1 && contents.get(0) instanceof Dish) {
            // System.out.println("✅ Sudah jadi Dish.");
            return false; // False karena tidak ada "perubahan" baru (sudah jadi dari awal)
        }

        List<String> ingredientNames = new ArrayList<>();
        for (Processable item : contents) {
            ingredientNames.add(item.getName());
        }

        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(ingredientNames);

        if (recipeMatch != null) {
            plate.clearContents();
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), ingredientNames);
            plate.addIngredient(finalDish);
            System.out.println("🎉 SUKSES! Jadi: " + finalDish.getRecipeName());
            notifyInteraction(finalDish, "Assembled!", new Color(156, 39, 176));
            return true; // Berhasil merakit
        } else {
            // System.out.println("❌ Gagal: Resep tidak ditemukan.");
            notifyInteraction("Recipe mismatch", new Color(244, 67, 54));
            return false; // Gagal merakit
        }
    }
}