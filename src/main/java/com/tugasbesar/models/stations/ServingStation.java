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

    @Override
    public void interact(Chef chef) {
        Item handItem = chef.getHeldItem();

        // --- 1. MENYAJIKAN MAKANAN ---
        if (handItem != null && handItem instanceof Plate) {
            Plate plate = (Plate) handItem;

            // Debugging Kasar: Cek isi piring di console
            System.out.println("\n--- 🛑 SERVING DEBUG START 🛑 ---");
            System.out.println("👉 Isi Piring Kamu: " + plate.getContentsString());

            if (plate.getContents().isEmpty()) {
                System.out.println("❌ GAGAL: Piring terdeteksi KOSONG secara data.");
                if (gp != null)
                    gp.showMessage("Piring Kosong!");
                notifyInteraction(plate, "Empty plate", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
                return;
            }
            if (plate.isDirty()) {
                System.out.println("❌ GAGAL: Piring KOTOR.");
                if (gp != null)
                    gp.showMessage("Piring Kotor!");
                notifyInteraction(plate, "Dirty plate", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
                return;
            }

            // Cek Resep
            ScoreEvent serveEvent = OrderManager.getInstance().checkDish(plate);

            if (serveEvent != null) {
                if (gp != null)
                    gp.showMessage("✅ BENAR! (+" + serveEvent.getPointsAwarded() + ")");
                System.out.println("✅ SUKSES: Order ditemukan dan cocok! +" + serveEvent.getPointsAwarded()
                        + " (Combo x" + serveEvent.getMultiplier() + ", Streak " + serveEvent.getStreak() + ")");
                notifyInteraction(plate, "Served!", new Color(76, 175, 80));
                chef.setHeldItem(null);
                eatingTimers.add(300);
                if (gp != null) {
                    gp.pushTilePopup(getPosX(), getPosY(), serveEvent.getDishItem(), getSymbol(),
                            serveEvent.getToastMessage(), serveEvent.getComboDetail(), new Color(255, 215, 0));
                }
            } else {
                if (gp != null)
                    gp.showMessage("❌ SALAH RESEP!");
                System.out.println("❌ GAGAL: Tidak ada Order yang cocok dengan isi piringmu.");
                notifyInteraction(plate, "Wrong order", new Color(244, 67, 54));
                OrderManager.getInstance().registerServeFailure();
            }
            System.out.println("--- 🛑 SERVING DEBUG END 🛑 ---\n");
            return;
        }

        // --- 2. AMBIL PIRING KOTOR ---
        if (handItem == null) {
            if (!dirtyPlateReturn.isEmpty()) {
                chef.setHeldItem(dirtyPlateReturn.pop());
                System.out.println("🤢 Mengambil Piring Kotor.");
                notifyInteraction(chef.getHeldItem(), "Dirty plate", new Color(244, 143, 177));
            } else {
                System.out.println("⚠️ Belum ada piring kotor.");
                notifyInteraction("No dirty plates", new Color(255, 193, 7));
            }
            return;
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
                    System.out.println("🛎️ Pelanggan selesai makan (Piring kotor muncul).");
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