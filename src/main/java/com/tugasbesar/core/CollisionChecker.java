package com.tugasbesar.core;

import com.tugasbesar.models.abstracts.Entity;
import java.awt.Rectangle; // [PENTING] Tambahin ini biar gak error pas bikin new Rectangle

public class CollisionChecker {
    
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // --- 1. CEK BATAS LAYAR (Kodingan Lama) ---
    public void checkWindowBoundary(Entity entity) {
        // Mentok Kiri
        if (entity.x < 0) {
            entity.x = 0;
        }
        // Mentok Atas
        if (entity.y < 0) {
            entity.y = 0;
        }
        // Mentok Kanan (Lebar Layar - Lebar Entity)
        if (entity.x > gp.screenWidth - gp.tileSize) {
            entity.x = gp.screenWidth - gp.tileSize;
        }
        // Mentok Bawah (Tinggi Layar - Tinggi Entity)
        if (entity.y > gp.screenHeight - gp.tileSize) {
            entity.y = gp.screenHeight - gp.tileSize;
        }
    }

    // --- 2. CEK TABRAKAN DENGAN STATION/OBJEK  ---
    public int checkObject(Entity entity, boolean player) {
        int index = 999; // 999 artinya tidak nabrak apa-apa

        for (int i = 0; i < gp.station.length; i++) {
            if (gp.station[i] != null) {

                // A. Ambil posisi hitbox Entity (Chef) saat ini
                entity.solidArea.x = entity.x + entity.solidArea.x;
                entity.solidArea.y = entity.y + entity.solidArea.y;

                // B. Ambil posisi hitbox Station (Target)
                // Kita hitung posisi station di pixel
                int stationSolidX = gp.station[i].getPosX() * gp.tileSize;
                int stationSolidY = gp.station[i].getPosY() * gp.tileSize;
                
                // Bikin area kotak imajiner station (Full 1 kotak 48x48)
                Rectangle stationArea = new Rectangle(stationSolidX, stationSolidY, gp.tileSize, gp.tileSize);

                // C. Prediksi Gerakan: Kalau gerak ke sana, nabrak gak?
                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed; // Prediksi kalau gerak ke atas
                        if (entity.solidArea.intersects(stationArea)) {
                            entity.collisionOn = true; // NABRAK!
                            index = i;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(stationArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(stationArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(stationArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                }
                
                // D. Reset posisi solidArea Entity ke default
                // (Penting: Biar koordinatnya gak nambah terus menerus tiap loop)
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
            }
        }
        return index;
    }
}