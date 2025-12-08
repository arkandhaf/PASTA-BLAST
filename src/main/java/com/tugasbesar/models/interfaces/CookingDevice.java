package com.tugasbesar.models.interfaces;

public interface CookingDevice {

    // --- Syarat dari Teman/Soal ---
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
    void startCooking();

    // --- TAMBAHAN WAJIB (Biar Game Jalan) ---
    
    // 1. Biar CookingStation bisa jalanin timer
    void processCookingTick(); 
    
    // 2. Biar UI/Logika bisa tau status makanan
    boolean isBurned();
    boolean isCooked();
    int getCookingPercentage();
}