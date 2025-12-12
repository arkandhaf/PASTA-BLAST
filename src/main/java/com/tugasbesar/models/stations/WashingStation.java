package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;
import java.awt.Graphics2D;
import java.awt.Color;

public class WashingStation extends Station {

    // Tumpukan Piring
    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;

    // Logic Cuci
    private boolean isWashing = false;
    private int washProgress = 0;
    private final int WASH_SPEED = 2; // Kecepatan cuci per frame

    public WashingStation(int x, int y) {
        super(x, y, "Sink", "W");
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
    }

    @Override
    public void interact(Chef chef) {

        // 1. TARUH PIRING KOTOR (Input)
        if (chef.hasItem() && chef.getHeldItem() instanceof Plate) {
            Plate p = (Plate) chef.getHeldItem();

            // Piring dianggap kotor jika statusnya dirty ATAU masih ada isinya (sisa
            // makanan)
            if (p.isDirty() || !p.getContents().isEmpty()) {
                p.clearContents(); // Kosongkan sisa makanan sebelum ditumpuk
                dirtyPlates.push(p);
                chef.setHeldItem(null); // Piring pindah ke tumpukan, tangan kosong
                System.out.println("💧 [Washing] Menaruh piring kotor. (Antrian: " + dirtyPlates.size() + ")");
                notifyInteraction(p, "Queued for wash", new Color(244, 143, 177));
                return;
            } else {
                System.out.println("⚠️ [Washing] Piring ini sudah bersih!");
                notifyInteraction(p, "Already clean", new Color(129, 212, 250));
            }
        }

        // 2. AMBIL PIRING BERSIH (Output)
        // Syarat: Tangan kosong & Ada piring bersih
        if (!chef.hasItem() && !cleanPlates.isEmpty()) {
            chef.setHeldItem(cleanPlates.pop());
            System.out.println("✨ [Washing] Mengambil piring bersih.");
            notifyInteraction(chef.getHeldItem(), "Clean plate", new Color(129, 212, 250));
            return;
        }

        // 3. MULAI MENCUCI (Proses)
        // Syarat: Tangan kosong & Ada piring kotor & Belum mulai nyuci
        if (!chef.hasItem() && !dirtyPlates.isEmpty()) {
            this.chefAtStation = chef; // Kunci Chef di station ini
            chef.setBusy(true); // Chef tidak bisa gerak
            isWashing = true;
            System.out.println("🧼 [Washing] Mulai mencuci...");
            notifyInteraction("Washing...", new Color(3, 169, 244));
        }
    }

    @Override
    public void update() {
        // Hanya update kalau ada Chef yang sedang aktif mencuci
        if (chefAtStation == null || !isWashing)
            return;

        // Cek kalau input habis tiba-tiba
        if (dirtyPlates.isEmpty()) {
            stopWashing();
            return;
        }

        washProgress += WASH_SPEED;

        // Selesai 1 Piring (Progress 100%)
        if (washProgress >= 100) {
            Plate p = dirtyPlates.pop();
            p.clean(); // Ubah status jadi bersih (method di Plate.java)
            cleanPlates.push(p);

            washProgress = 0;
            System.out.println("✨ [Washing] 1 Piring Selesai! (Bersih: " + cleanPlates.size() + ")");
            notifyInteraction(p, "Cleaned", new Color(76, 175, 80));

            // Kalau antrian habis, stop otomatis
            if (dirtyPlates.isEmpty()) {
                stopWashing();
            }
        }
    }

    // Helper untuk menghentikan proses cuci
    private void stopWashing() {
        isWashing = false;
        washProgress = 0;
        if (chefAtStation != null) {
            chefAtStation.setBusy(false); // Lepaskan Chef biar bisa gerak lagi
            chefAtStation = null;
        }
        System.out.println("🛑 [Washing] Selesai mencuci.");
        notifyInteraction("Done", new Color(120, 144, 156));
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); // Gambar kotak dasar

        // Visualisasi Progress Bar Biru
        if (isWashing) {
            g2.setColor(Color.BLUE);
            int barWidth = (int) (48 * (washProgress / 100.0));
            g2.fillRect(posX * 48, posY * 48 - 10, barWidth, 5);
        }

        // Indikator Tumpukan Teks
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(10f)); // Font kecil
        g2.drawString("Dirty: " + dirtyPlates.size(), posX * 48 + 2, posY * 48 + 15);
        g2.drawString("Clean: " + cleanPlates.size(), posX * 48 + 2, posY * 48 + 30);
    }
}