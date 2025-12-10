package com.tugasbesar.models.abstracts;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage; // [PENTING] Buat nyimpen gambar

public abstract class Entity {
    
    // --- 1. POSISI & ATRIBUT FISIK ---
    public int x, y;
    public int speed;
    public String direction = "down"; // Default arah
    
    // --- 2. SISTEM ANIMASI (SPRITE) ---
    // Ini wadah buat nyimpen gambar Chef pas madep atas, bawah, kiri, kanan
    // Angka 1 dan 2 maksudnya kaki kiri & kaki kanan (biar ada efek jalan)
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    
    // Counter biar animasinya gak kecepetan (kayak kesetrum)
    public int spriteCounter = 0;
    public int spriteNum = 1; 
    
    // --- 3. HITBOX (AREA TABRAKAN) ---
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    // --- CONSTRUCTOR ---
    public Entity() {
        // Set default hitbox biar gak NullPointer kalau lupa di-set di anak
        // Default: Kotak penuh 48x48
        solidArea = new Rectangle(0, 0, 48, 48); 
        solidAreaDefaultX = 0;
        solidAreaDefaultY = 0;
    }

    // --- METHOD UTAMA ---
    public void update() {
        // Override di class anak (Chef/NPC)
    }
    
    public void draw(Graphics2D g2) {
        // Override di class anak
    }
}