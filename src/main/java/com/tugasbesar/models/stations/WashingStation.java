package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import java.util.Stack;

public class WashingStation extends Station {

    // Dua Bagian: Input (Kotor) dan Output (Bersih/Rak)
    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;

    // Logic Cuci
    private boolean isWashing = false;
    private int washProgress = 0; // 0 - 100
    private final int WASH_SPEED = 20; // 5 Tick selesai

    public WashingStation(int x, int y) {
        // x, y, Nama, Symbol "W"
        super(x, y, "Washing Station", "W");
        
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
    }

    @Override
    public void interact(Chef chef) {
        
        // ==========================================
        // 1. CHEF BAWA PIRING KOTOR (TARUH)
        // ==========================================
        if (chef.hasItem() && chef.getHeldItem() instanceof Plate) {
            Plate p = (Plate) chef.getHeldItem();
            
            // Cek apakah piring ada isinya (kotor)
            if (!p.getContents().isEmpty()) {
                dirtyPlates.push(p);
                chef.setHeldItem(null); // Tangan kosong
                System.out.println("[Washing] " + chef.getName() + " menumpuk piring kotor. (Total: " + dirtyPlates.size() + ")");
            } else {
                System.out.println("[!] Piring ini sudah kosong/bersih. Taruh di Plate Storage.");
            }
            return;
        }

        // ==========================================
        // 2. CHEF TANGAN KOSONG
        // ==========================================
        if (!chef.hasItem()) {
            
            // PRIORITAS A: Ambil Piring Bersih (Kalau ada)
            // Syarat: Ada piring bersih DAN (Tidak sedang mencuci ATAU stack kotor kosong)
            // Logic: Kalau ada piring bersih, ambil dulu.
            if (!cleanPlates.isEmpty()) {
                chef.setHeldItem(cleanPlates.pop());
                System.out.println("[Washing] " + chef.getName() + " mengambil piring bersih dari rak.");
                return;
            }

            // PRIORITAS B: Mulai / Lanjut Mencuci
            if (!dirtyPlates.isEmpty()) {
                isWashing = true; // Set status washing
                System.out.println("[Washing] " + chef.getName() + " mulai menggosok piring...");
            } else {
                System.out.println("[Washing] Tidak ada piring kotor untuk dicuci.");
            }
        }
    }

    @Override
    public void update() {
        // 1. SYARAT PAUSE: Chef harus ada di Station
        if (chefAtStation == null) {
            // Kalau Chef pergi, kita tidak ubah 'isWashing' jadi false,
            // tapi kita return (Pause Progress).
            return; 
        }

        // 2. SYARAT CUCI: Status Washing True & Ada Piring Kotor
        if (isWashing && !dirtyPlates.isEmpty()) {
            
            washProgress += WASH_SPEED;

            // Visualisasi Bar
            String chefName = chefAtStation.getName();
            System.out.println("   [" + chefName + "] " + getProgressBar(20) + " Mencuci...");

            // 3. SELESAI CUCI 1 PIRING
            if (washProgress >= 100) {
                // Ambil dari kotor
                Plate donePlate = dirtyPlates.pop();
                
                // Bersihkan
                donePlate.getContents().clear(); 
                
                // Masukkan ke rak bersih
                cleanPlates.push(donePlate);
                
                // Reset Progress
                washProgress = 0;
                System.out.println(">>> [SELESAI] 1 Piring bersih! Masuk ke rak. (Sisa kotor: " + dirtyPlates.size() + ")");

                // Cek apakah masih ada kotoran?
                if (dirtyPlates.isEmpty()) {
                    isWashing = false; // Berhenti otomatis kalau habis
                    System.out.println("[Washing] Semua piring sudah dicuci.");
                }
            }
        }
    }

    // Helper Visual (Progress Bar Air)
    public String getProgressBar(int width) {
        int percent = Math.min(100, washProgress);
        int filled = (int) Math.round((percent / 100.0) * width);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < width; i++) sb.append(i < filled ? '~' : ' ');
        sb.append("] ").append(percent).append('%');
        return sb.toString();
    }
    
    // Getter untuk Debugging
    public int getDirtyCount() { return dirtyPlates.size(); }
    public int getCleanCount() { return cleanPlates.size(); }
}