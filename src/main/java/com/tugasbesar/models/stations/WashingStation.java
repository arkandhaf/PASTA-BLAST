package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;
import java.awt.Graphics2D;
import java.awt.Color;

public class WashingStation extends Station {

    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;

    private boolean isWashing = false;
    private int washProgress = 0;
    private final int WASH_SPEED = 2; 

    public WashingStation(int x, int y) {
        super(x, y, "Sink", "W");
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
    }

    // --- [FIX] GRAB: TARUH/AMBIL ---
    @Override
    public void interactGrab(Chef chef) {
        
        // 1. TARUH PIRING KOTOR
        if (chef.hasItem() && chef.getHeldItem() instanceof Plate) {
            Plate p = (Plate) chef.getHeldItem();
            if (p.isDirty() || !p.getContents().isEmpty()) {
                p.clearContents(); 
                dirtyPlates.push(p);
                chef.setHeldItem(null); 
                notifyInteraction(p, "Queued for wash", new Color(244, 143, 177));
                return;
            } else {
                notifyInteraction(p, "Already clean", new Color(129, 212, 250));
            }
        }

        // 2. AMBIL PIRING BERSIH
        if (!chef.hasItem() && !cleanPlates.isEmpty()) {
            chef.setHeldItem(cleanPlates.pop());
            notifyInteraction(chef.getHeldItem(), "Clean plate", new Color(129, 212, 250));
            return;
        }
    }

    // --- [FIX] USE: PROSES CUCI (E) ---
    @Override
    public void interactUse(Chef chef) {
        // Syarat: Tangan kosong & Ada piring kotor & Belum mulai nyuci
        if (!chef.hasItem() && !dirtyPlates.isEmpty() && !isWashing) {
            this.chefAtStation = chef; 
            chef.setBusy(true); 
            isWashing = true;
            notifyInteraction("Washing...", new Color(3, 169, 244));
        }
    }

    @Override
    public void update() {
        if (chefAtStation == null || !isWashing) return;

        if (dirtyPlates.isEmpty()) {
            stopWashing();
            return;
        }

        washProgress += WASH_SPEED;

        // Selesai 1 Piring
        if (washProgress >= 100) {
            Plate p = dirtyPlates.pop();
            p.clean(); 
            cleanPlates.push(p);

            washProgress = 0;
            notifyInteraction(p, "Cleaned", new Color(76, 175, 80));

            // Stop otomatis kalau antrian habis
            if (dirtyPlates.isEmpty()) {
                stopWashing();
            }
        }
    }

    private void stopWashing() {
        isWashing = false;
        washProgress = 0;
        if (chefAtStation != null) {
            chefAtStation.setBusy(false); 
            chefAtStation = null;
        }
        notifyInteraction("Done", new Color(120, 144, 156));
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); 

        if (isWashing) {
            g2.setColor(Color.BLUE);
            int barWidth = (int) (48 * (washProgress / 100.0));
            g2.fillRect(posX * 48, posY * 48 - 10, barWidth, 5);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(10f)); 
        g2.drawString("Dirty: " + dirtyPlates.size(), posX * 48 + 2, posY * 48 + 15);
        g2.drawString("Clean: " + cleanPlates.size(), posX * 48 + 2, posY * 48 + 30);
    }
}