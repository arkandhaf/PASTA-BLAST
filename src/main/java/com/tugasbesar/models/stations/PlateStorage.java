package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;
import java.awt.Color;
import java.awt.Graphics2D;

public class PlateStorage extends Station {

    private Stack<Plate> plateStack;

    public PlateStorage(int x, int y) {
        super(x, y, "Plate Storage", "P");
        this.plateStack = new Stack<>();
        for (int i = 0; i < 5; i++) plateStack.push(new Plate());
    }

    // --- [FIX] GANTI NAMA METHOD ---
    @Override
    public void interactGrab(Chef chef) {
        if (chef.hasItem()) {
            notifyInteraction("No drop here", new Color(255, 193, 7));
            return;
        }

        if (!plateStack.isEmpty()) {
            Plate topPlate = plateStack.peek();
            if (topPlate.isDirty()) {
                notifyInteraction("Dirty stack", new Color(244, 67, 54));
                return;
            } else {
                chef.setHeldItem(plateStack.pop());
                notifyInteraction(chef.getHeldItem(), "Clean plate", new Color(129, 212, 250));
            }
        } else {
            notifyInteraction("No plates", new Color(255, 193, 7));
        }
    }

    // --- [FIX] METHOD USE KOSONG ---
    @Override
    public void interactUse(Chef chef) {
        // Gak ada interaksi E di sini
    }

    public void addDirtyPlateFromServing(Plate p) {
        plateStack.push(p);
        notifyInteraction(p, "Dirty plate returned", new Color(244, 143, 177));
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); 
        if (!plateStack.isEmpty()) {
            int count = Math.min(plateStack.size(), 3);
            for (int i = 0; i < count; i++) {
                g2.setColor(Color.WHITE);
                g2.fillOval(posX * 48 + 10, posY * 48 + 10 - (i * 2), 28, 28);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(posX * 48 + 10, posY * 48 + 10 - (i * 2), 28, 28);
            }
            g2.setColor(Color.BLACK);
            g2.drawString("" + plateStack.size(), posX * 48 + 20, posY * 48 + 30);
        }
    }
}