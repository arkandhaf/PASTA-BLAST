package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Preparable;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        // Asumsi "A" adalah simbol untuk Assembly Station
        super(x, y, "Assembly Station", "A"); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // --- 1. LOGIKA PLATING (GABUNG BAHAN) ---

        // Case A: Piring di tangan, Bahan di meja
        if (hand instanceof Plate && tableItem instanceof Preparable) {
            performPlating(chef, (Plate) hand, (Preparable) tableItem, true);
            return;
        }

        // Case B: Bahan di tangan, Piring di meja
        if (hand instanceof Preparable && tableItem instanceof Plate) {
            performPlating(chef, (Plate) tableItem, (Preparable) hand, false);
            return;
        }

        // --- 2. LOGIKA AMBIL / TARUH STANDAR (Menggunakan defaultInteract) ---
        
        // Aturan Khusus Assembly: Hanya Piring yang boleh ditaruh
        if (hand != null && isEmpty()) {
            if (hand instanceof Plate) {
                placeItem(hand);
                chef.setHeldItem(null);
                System.out.println("[Assembly] " + chef.getName() + " menaruh Piring.");
            } else {
                System.out.println("[!] Hanya Piring yang boleh ditaruh di sini.");
            }
            return;
        }
        
        // Ambil item dari meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Assembly] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
            return;
        }

        // Jika tidak terjadi apa-apa
        // defaultInteract(chef); // Bisa dipakai jika ingin logika swap/taruh standar
    }

    // Helper untuk validasi aturan Pasta
    private void performPlating(Chef chef, Plate plate, Preparable item, boolean isPlateInHand) {
        
        // 1. Cek kesiapan bahan
        if (!item.canBePlacedOnPlate()) {
            System.out.println("[!] Bahan belum siap disajikan (mentah/gosong)!");
            return;
        }

        // 2. Deteksi Nama (Pakai contains biar aman dari "Pasta (Cooked)")
        boolean incomingIsPasta = item.getName().toLowerCase().contains("pasta");

        // Cek isi piring saat ini
        boolean plateHasPasta = plate.getContents().stream()
                                .anyMatch(p -> p.getName().toLowerCase().contains("pasta"));

        // RULE 1: Piring kosong WAJIB diisi Pasta dulu
        if (plate.getContents().isEmpty() && !incomingIsPasta) {
            System.out.println("[!] Piring kosong harus diisi Pasta dulu sebagai dasar.");
            return;
        }

        // RULE 2: Jangan menumpuk Pasta di atas Pasta
        if (plateHasPasta && incomingIsPasta) {
            System.out.println("[!] Sudah ada Pasta di piring.");
            return;
        }

        // --- EKSEKUSI PLATING ---
        plate.addIngredient(item); // Masukkan bahan
        System.out.println("[Plating] " + item.getName() + " masuk ke Piring.");

        // Hapus item dari asalnya
        if (isPlateInHand) {
            this.itemOnStation = null; // Hapus dari meja (Chef pegang piring)
        } else {
            chef.setHeldItem(null);    // Hapus dari tangan (Chef pegang bahan)
        }
    }
}