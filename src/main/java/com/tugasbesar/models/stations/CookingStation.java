package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;

public class CookingStation extends Station {

    public CookingStation(int x, int y) {
        super(x, y, "Stove", "K");
    }

    @Override
    public void interactGrab(Chef chef) {
        Item hand = chef.getHeldItem();
        Item stationItem = getItemOnStation();

        // 1. TARUH ALAT
        if (hand instanceof BaseCookingDevice && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            notifyInteraction(hand, "Placed", new Color(0, 188, 212));
            return;
        }

        // 2. MASUKKAN BAHAN (Ke Alat)
        if (hand instanceof Ingredient && stationItem instanceof BaseCookingDevice) {
            BaseCookingDevice device = (BaseCookingDevice) stationItem;
            Ingredient ing = (Ingredient) hand;

            if (device.canAccept(ing)) {
                if (device.isEmpty()) {
                    device.addIngredient(ing);
                    chef.setHeldItem(null);
                    notifyInteraction("Press E to Cook", Color.YELLOW);
                } else {
                    notifyInteraction("Full!", Color.RED);
                }
            } else {
                notifyInteraction("Wrong Tool/Ingredient", Color.RED);
            }
            return;
        }

        // 3. PLATING (Piring Ambil Isi)
        if (hand instanceof Plate && stationItem instanceof BaseCookingDevice) {
            BaseCookingDevice device = (BaseCookingDevice) stationItem;
            Plate plate = (Plate) hand;

            if (!device.isEmpty()) {
                Ingredient foodInside = (Ingredient) device.getContents().get(0);
                if (plate.canAccept(foodInside)) {
                    device.getContents().clear();
                    plate.addIngredient(foodInside);
                    notifyInteraction(foodInside, "Plated", new Color(50, 150, 255));
                } else {
                    notifyInteraction("Not Ready/Dirty", Color.RED);
                }
            }
            return;
        }

        // 4. AMBIL ALAT (PORTABLE)
        if (hand == null && !isEmpty()) {
            Item takenItem = takeItem();
            chef.setHeldItem(takenItem);
            notifyInteraction("Picked Up", Color.WHITE);
            return;
        }
    }

    @Override
    public void interactUse(Chef chef) {
        if (itemOnStation instanceof BaseCookingDevice) {
            BaseCookingDevice device = (BaseCookingDevice) itemOnStation;
            if (!device.isEmpty() && !device.isBurned() && !device.isCooked()) {
                if (!device.isCooking()) {
                    device.startCooking();
                    notifyInteraction("Cooking Started!", Color.ORANGE);
                }
            } else if (device.isEmpty()) {
                notifyInteraction("Empty!", Color.GRAY);
            }
        }
    }

    @Override
    public void update() {
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick(); 
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        
        if (itemOnStation instanceof BaseCookingDevice) {
            BaseCookingDevice device = (BaseCookingDevice) itemOnStation;
            int progress = device.getCookingPercentage(); 
            boolean isBurned = device.isBurned();
            boolean isCooked = device.isCooked();
            boolean isCooking = device.isCooking();

            if (!device.isEmpty()) {
                String statusText = "";
                Color statusColor = Color.WHITE;

                // LOGIC STATUS TEXT
                if (isBurned) {
                    statusText = "XXX BURNED XXX";
                    statusColor = Color.RED;
                } else if (isCooked) {
                    statusText = "!!! COOKED !!!";
                    statusColor = Color.GREEN;
                } else if (isCooking) {
                    statusText = progress + "%";
                    statusColor = Color.ORANGE;
                } else {
                    statusText = "PRESS E";
                    statusColor = Color.YELLOW;
                }
                
                // Gambar Teks di Tengah Atas
                g2.setColor(statusColor);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                int textLen = (int) g2.getFontMetrics().getStringBounds(statusText, g2).getWidth();
                g2.drawString(statusText, (posX * 48) + (24 - textLen/2), posY * 48 - 8);

                // Gambar Progress Bar
                g2.setColor(Color.BLACK);
                g2.drawRect(posX * 48 + 4, posY * 48 - 5, 40, 6);
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(posX * 48 + 5, posY * 48 - 4, 39, 5);

                int fillWidth = (int) ((progress / 100.0) * 39);
                g2.setColor(statusColor);
                g2.fillRect(posX * 48 + 5, posY * 48 - 4, fillWidth, 5);
            }
        }
    }
}