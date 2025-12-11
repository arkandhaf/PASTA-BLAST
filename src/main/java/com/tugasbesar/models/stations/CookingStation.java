package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.enums.IngredientState;
import java.awt.Color;
import java.awt.Graphics2D;

public class CookingStation extends Station {

    private boolean isCooking = false;
    private int cookProgress = 0;
    private final int MAX_PROGRESS = 200;
    private final int COOK_SPEED = 1;
    private int burnCounter = 0;
    private final int BURN_WARNING_THRESHOLD = 180;
    private final int BURN_CONSUME_THRESHOLD = 260;
    private boolean cookedAlertShown = false;
    private boolean burnAlertShown = false;

    public CookingStation(int x, int y) {
        super(x, y, "Stove", "K");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();

        // 1. TARUH ITEM
        if (chef.hasItem() && isEmpty()) {
            // Transit Alat/Piring
            if (hand instanceof Plate || hand instanceof BaseCookingDevice) {
                placeItem(hand);
                chef.setHeldItem(null);
                return;
            }

            // Masak Bahan
            if (hand instanceof Ingredient) {
                Ingredient ing = (Ingredient) hand;

                // Cek Logic canBeCooked() yang baru di Ingredient.java
                if (ing.canBeCooked()) {
                    placeItem(hand);
                    chef.setHeldItem(null);

                    isCooking = true;
                    cookProgress = 0;
                    burnCounter = 0;
                    cookedAlertShown = false;
                    burnAlertShown = false;
                    System.out.println("🔥 [Stove] Mulai memasak " + ing.getName());
                } else {
                    System.out.println("⚠️ [Stove] Bahan ini belum siap dimasak (Mungkin harus dipotong dulu?)");
                }
            }
            return;
        }

        // 2. AMBIL ITEM
        if (!chef.hasItem() && !isEmpty()) {
            if (isCooking) {
                System.out.println("⚠️ [Stove] Sedang memasak...");
                return;
            }
            chef.setHeldItem(takeItem());
            cookProgress = 0;
            burnCounter = 0;
            cookedAlertShown = false;
            burnAlertShown = false;
            System.out.println("⬆️ [Stove] Mengambil masakan.");
        }

        // 3. PLATING (Piring Ambil Makanan)
        if (hand instanceof Plate && !isEmpty()) {
            if (isCooking) {
                System.out.println("⚠️ Masih dimasak!");
                return;
            }

            Plate plate = (Plate) hand;

            // Cek apakah yang di meja itu Ingredient?
            if (itemOnStation instanceof Ingredient) {
                Ingredient food = (Ingredient) itemOnStation;

                // Cek apakah piring mau terima? (Harus COOKED)
                if (plate.canAccept(food)) {
                    // AMBIL DARI MEJA
                    Item takenItem = takeItem();

                    // MASUKKAN KE PIRING
                    plate.addIngredient((Ingredient) takenItem);

                    cookProgress = 0;
                    burnCounter = 0;
                    cookedAlertShown = false;
                    burnAlertShown = false;
                    System.out.println("🍽️ [Stove] Berhasil memindahkan " + takenItem.getName() + " ke Piring.");
                } else {
                    System.out.println("⚠️ [Stove] Piring menolak item ini (Mungkin belum COOKED?)");
                }
            }
            return;
        }
    }

    @Override
    public void update() {
        if (isCooking && itemOnStation != null) {
            cookProgress += COOK_SPEED;
            if (cookProgress >= MAX_PROGRESS) {
                finishCooking();
            }
        } else if (itemOnStation instanceof Ingredient) {
            Ingredient ing = (Ingredient) itemOnStation;
            IngredientState state = ing.getState();
            if (state == IngredientState.COOKED) {
                burnCounter++;
                if (!burnAlertShown && burnCounter >= BURN_WARNING_THRESHOLD) {
                    triggerAlert("Burning!", new Color(244, 67, 54), ing);
                    burnAlertShown = true;
                }
                if (burnCounter >= BURN_CONSUME_THRESHOLD) {
                    ing.burn();
                    burnCounter = 0;
                }
            } else {
                burnCounter = 0;
            }
        }
    }

    private void finishCooking() {
        isCooking = false;
        burnCounter = 0;
        if (itemOnStation instanceof Ingredient) {
            ((Ingredient) itemOnStation).cook();
            System.out.println("✅ [Stove] Matang! (" + itemOnStation.getName() + " COOKED)");
            if (!cookedAlertShown) {
                triggerAlert("Cooked!", new Color(76, 175, 80), (Ingredient) itemOnStation);
                cookedAlertShown = true;
                burnAlertShown = false;
            }
        }
    }

    private void triggerAlert(String message, Color accent, Ingredient ingredient) {
        if (gamePanel != null) {
            gamePanel.pushCookingAlert(posX, posY, ingredient, message, accent);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        if (cookProgress > 0) {
            int width = (int) ((double) cookProgress / MAX_PROGRESS * 40);
            g2.setColor(isCooking ? Color.RED : Color.GREEN);
            g2.fillRect(posX * 48 + 4, posY * 48 - 10, width, 6);
        }

        // Visual Text kalau sudah matang
        if (!isEmpty() && !isCooking && itemOnStation instanceof Ingredient) {
            g2.setColor(Color.GREEN);
            g2.setFont(g2.getFont().deriveFont(9F));
            g2.drawString("COOKED", posX * 48 + 5, posY * 48 + 20);
        }
    }
}