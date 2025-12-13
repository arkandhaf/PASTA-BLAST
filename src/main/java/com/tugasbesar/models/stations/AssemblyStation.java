package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.stream.Collectors;

public class AssemblyStation extends Station {

    private final OrderManager orderManager;

    public AssemblyStation(int x, int y, OrderManager orderManager) {
        super(x, y, "Assembly Station", "A");
        // Asumsi OrderManager diinject via constructor atau diambil via Singleton
        this.orderManager = orderManager;
    }

    // Asumsi: Jika OrderManager tidak di-inject, gunakan Singleton (sesuaikan
    // dengan struktur proyek Anda)
    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A");
        this.orderManager = OrderManager.getInstance();
    }

    // =========================================================================
    // INTERACT GRAB (SPACE) - HANYA AMBIL/TARUH UMUM & TUANG DARI PANCI
    // =========================================================================
    @Override
    public void interactGrab(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // --- 1. KASUS KHUSUS: TUANG DARI UTENSIL (Tangan) KE PIRING (Meja) ---
        if (hand instanceof BaseCookingDevice utensilInHand && tableItem instanceof Plate plateOnTable) {

            if (!utensilInHand.isEmpty() && !plateOnTable.isDirty()) {
                Processable ingredient = utensilInHand.getContents().get(0);

                // Cek validasi plating (harus Cooked/Chopped)
                if (isValidForPlating(ingredient) && plateOnTable.canAccept(ingredient)) {

                    // Pindahkan item dari Utensil ke Plate (tanpa menggunakan takeItem() stasiun)
                    utensilInHand.getContents().clear(); // Kosongkan panci/wajan di tangan
                    plateOnTable.addIngredient(ingredient);

                    System.out.println("🥣 Menuang " + ingredient.getName() + " ke Piring.");
                    notifyInteraction("Poured " + ingredient.getName(), Color.CYAN);
                    return;
                } else {
                    notifyInteraction("Invalid item for plating or plate full.", Color.RED);
                    return;
                }
            }
        }

        // --- 2. DEFAULT AMBIL / TARUH (Menangani Utensil, Bahan, Piring, dll.) ---

        // Kasus: Mengambil Item Apapun dari Meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            notifyInteraction("Picked Up " + chef.getHeldItem().getName(), Color.WHITE);
            return;
        }

        // Kasus: Menaruh Item Apapun ke Meja
        if (hand != null && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            notifyInteraction("Placed " + itemOnStation.getName(), Color.WHITE);
            return;
        }

        // Jika tangan penuh dan meja penuh, atau tangan kosong dan meja kosong
        defaultInteract(chef); // Ini akan memanggil notifikasi (Blocked / Nothing here)
    }

    // =========================================================================
    // INTERACT USE (E) - PLATING DAN ASSEMBLY
    // =========================================================================
    @Override
    public void interactUse(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // --- 1. Plating (Menambahkan item di tangan ke Plate di meja) ---
        if (hand instanceof Processable ingredient && tableItem instanceof Plate plate) {
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                performPlating(plate, ingredient); // Tambah ke Plate di meja
                chef.setHeldItem(null); // Kosongkan tangan chef
                Item notifiedItem = (ingredient instanceof Item) ? (Item) ingredient : null;
                notifyInteraction(notifiedItem, "Plated (Manual)", new Color(76, 175, 80));
                return;
            }
        }

        // --- 2. Plating (Menambahkan item di meja ke Plate di tangan) ---
        // Chef memegang piring, dan di meja ada item yang mau diplating
        if (tableItem instanceof Processable ingredient && hand instanceof Plate plate) {
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                Item takenItem = takeItem(); // Ambil dari meja
                performPlating(plate, (Processable) takenItem); // Tambah ke Plate di tangan
                Item notifiedItem = (ingredient instanceof Item) ? (Item) ingredient : null;
                notifyInteraction(notifiedItem, "Plated (Auto)", new Color(76, 175, 80));
                return;
            }
        }

        // --- 3. Assembly (Mencocokkan resep pada Plate di meja) ---
        if (tableItem instanceof Plate plateOnTable) {
            // Pastikan piring tidak kotor, punya isi, dan isinya belum jadi Dish
            if (!plateOnTable.isDirty() && plateOnTable.getContents().size() > 0
                    && !plateOnTable.getContents().get(0).equals("Dish")) {
                if (performAssembly(plateOnTable)) {
                    return;
                }
            }
        }

        // --- 4. Assembly (Mencocokkan resep pada Plate di tangan) ---
        if (hand instanceof Plate plateInHand) {
            // Pastikan piring tidak kotor, punya isi, dan isinya belum jadi Dish
            if (!plateInHand.isDirty() && plateInHand.getContents().size() > 0
                    && !plateInHand.getContents().get(0).equals("Dish")) {
                if (performAssembly(plateInHand)) {
                    return;
                }
            }
        }

        notifyInteraction("Cannot Assemble/Plate here.", Color.GRAY);
    }

    // =========================================================================
    // HELPER METHODS (Pastikan ini sesuai dengan logic Recipe/OrderManager Anda)
    // =========================================================================

    private boolean isValidForPlating(Processable item) {
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            notifyInteraction("Needs Prep or is Burnt", Color.RED);
            return false;
        }
        return true;
    }

    private void performPlating(Plate plate, Processable item) {
        // Cek apakah piring sudah ada Dish, jika ya, plating ditolak
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            notifyInteraction("Plate already contains a Final Dish", Color.RED);
            return;
        }
        plate.addIngredient(item);
        System.out.println("✅ [Plating] Menambahkan " + item.getName() + " ke piring.");
    }

    private boolean performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        if (contents.isEmpty())
            return false;

        List<String> contentsInStringFormat = contents.stream()
                .map(Processable::toString)
                .collect(Collectors.toList());

        Recipe recipeMatch = orderManager.findMatchingRecipe(contentsInStringFormat);

        if (recipeMatch != null) {
            plate.clearContents();
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), contentsInStringFormat);
            plate.addIngredient(finalDish);

            notifyInteraction(finalDish, "Recipe Complete!", new Color(0, 150, 136));
            return true;
        } else {
            notifyInteraction("Recipe Mismatch!", Color.RED);
            return false;
        }
    }
}