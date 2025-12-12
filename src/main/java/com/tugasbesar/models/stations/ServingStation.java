package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.core.GamePanel;
import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.OrderManager.ScoreEvent;
import com.tugasbesar.models.abstracts.Item;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class ServingStation extends Station {

    private GamePanel gp;
    private List<Integer> eatingTimers;
    private Stack<Plate> dirtyPlateReturn;

    public ServingStation(int x, int y, GamePanel gp) {
        super(x, y, "Serving Counter", "S");
        this.gp = gp;
        this.eatingTimers = new ArrayList<>();
        this.dirtyPlateReturn = new Stack<>();
    }

    // --- [FIX] GRAB: AMBIL PIRING KOTOR ---
    @Override
    public void interactGrab(Chef chef) {
        if (!chef.hasItem()) {
            if (!dirtyPlateReturn.isEmpty()) {
                chef.setHeldItem(dirtyPlateReturn.pop());
                notifyInteraction(chef.getHeldItem(), "Dirty plate", new Color(244, 143, 177));
            } else {
                notifyInteraction("No dirty plates", new Color(255, 193, 7));
            }
        }
    }

    // --- [FIX] USE: SAJIKAN MAKANAN (E) ---
    @Override
    public void interactUse(Chef chef) {
        Item handItem = chef.getHeldItem();

        if (handItem != null && handItem instanceof Plate) {
            Plate plate = (Plate) handItem;

            if (plate.getContents().isEmpty()) {
                if (gp != null) gp.showMessage("Piring Kosong!");
                notifyInteraction(plate, "Empty plate", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
                return;
            }
            if (plate.isDirty()) {
                if (gp != null) gp.showMessage("Piring Kotor!");
                notifyInteraction(plate, "Dirty plate", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
                return;
            }

            ScoreEvent serveEvent = OrderManager.getInstance().checkDish(plate);

            if (serveEvent != null) {
                if (gp != null) gp.showMessage("✅ BENAR! (+" + serveEvent.getPointsAwarded() + ")");
                notifyInteraction(plate, "Served!", new Color(76, 175, 80));
                chef.setHeldItem(null);
                eatingTimers.add(300);
                if (gp != null) {
                    gp.pushTilePopup(getPosX(), getPosY(), serveEvent.getDishItem(), getSymbol(),
                            serveEvent.getToastMessage(), serveEvent.getComboDetail(), new Color(255, 215, 0));
                }
            } else {
                if (gp != null) gp.showMessage("❌ SALAH RESEP!");
                notifyInteraction(plate, "Wrong order", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
            }
        }
    }

    @Override
    public void update() {
        if (!eatingTimers.isEmpty()) {
            for (int i = 0; i < eatingTimers.size(); i++) {
                eatingTimers.set(i, eatingTimers.get(i) - 1);
            }
            Iterator<Integer> iter = eatingTimers.iterator();
            while (iter.hasNext()) {
                if (iter.next() <= 0) {
                    iter.remove();
                    Plate p = new Plate();
                    p.markDirty();
                    dirtyPlateReturn.push(p);
                    notifyInteraction(p, "Dirty plate", new Color(244, 143, 177));
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        if (!dirtyPlateReturn.isEmpty()) {
            int count = Math.min(dirtyPlateReturn.size(), 3);
            for (int i = 0; i < count; i++) {
                g2.setColor(new Color(139, 69, 19));
                g2.fillOval(posX * 48 + 10, posY * 48 + 10 - (i * 3), 28, 28);
                g2.setColor(Color.BLACK);
                g2.drawOval(posX * 48 + 10, posY * 48 + 10 - (i * 3), 28, 28);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(10F));
            g2.drawString("DIRTY", posX * 48 + 8, posY * 48 + 40);
        } else if (!eatingTimers.isEmpty()) {
            g2.setColor(Color.YELLOW);
            g2.setFont(g2.getFont().deriveFont(9F));
            g2.drawString("EATING..", posX * 48 + 5, posY * 48 + 25);
        }
    }
}