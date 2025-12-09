package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.tugasbesar.models.actors.Chef;

public class GamePanel extends JPanel implements Runnable {

    // --- Pengaturan Layar ---
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 pixel
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixel
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixel

    // --- System Utama ---
    int FPS = 60;
    Thread gameThread;
    KeyHandler keyH = new KeyHandler();

    // --- SYSTEM V2.0 ---
    public CollisionChecker cChecker = new CollisionChecker(this); // Polisi Tabrakan
    public int gameTime = 180; // Waktu 3 Menit (180 detik)
    public boolean isGameRunning = true;
    
    // --- VARIABEL BARU UNTUK GILIRAN (TURN) ---
    // Chef yang saat ini BISA bergerak. Nilai: 1 atau 2. Default: P1.
    public int activePlayerID = 1; 

    // --- ENTITY (Mendukung 2 Pemain) ---
    public Chef chef1; // Chef Player 1
    public Chef chef2; // Chef Player 2

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // INISIALISASI DUA CHEF BARU dengan nama dan ID
        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2); 
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start(); // Jalankan Game Loop
        
        startGameTimer(); // Jalankan Waktu Mundur
    }
    
    // --- THREAD KHUSUS WAKTU ---
    public void startGameTimer() {
        Thread timerThread = new Thread(() -> {
            while (isGameRunning && gameTime > 0) {
                try {
                    Thread.sleep(1000); // Tunggu 1 detik
                    gameTime--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (gameTime <= 0) {
                System.out.println("⏳ WAKTU HABIS! GAME OVER");
                isGameRunning = false;
            }
        });
        timerThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (isGameRunning) {
            
            // --- LOGIC GANTI GILIRAN (TURN SWAP) ---
            if (keyH.turnSwapPressed) {
                if (activePlayerID == 1) {
                    activePlayerID = 2; // Ganti ke P2
                    System.out.println("🔄 GILIRAN: Chef P2 Aktif");
                } else {
                    activePlayerID = 1; // Ganti ke P1
                    System.out.println("🔄 GILIRAN: Chef P1 Aktif");
                }
                keyH.turnSwapPressed = false; // Matikan tombol agar tidak spamming
            }
            
            // --- HANYA UPDATE CHEF YANG AKTIF ---
            // Kita pass KeyHandler HANYA kepada Chef yang aktif
            if (activePlayerID == 1) {
                chef1.update(keyH); 
                chef2.update(null); // P2 TIDAK menerima KeyHandler, sehingga tidak bergerak
            } else { // activePlayerID == 2
                chef1.update(null);  // P1 TIDAK menerima KeyHandler
                chef2.update(keyH); 
            }
            
            // --- TODO: Lakukan update untuk semua Station di sini jika ada ---
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- Gambar kedua Chef ---
        chef1.draw(g2);
        chef2.draw(g2);
        
        // --- HUD (Tampilan Waktu & Giliran) ---
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(30F)); 
        g2.drawString("Time: " + gameTime, 20, 40); 
        
        // Tampilkan giliran
        g2.setColor(activePlayerID == 1 ? Color.RED : Color.BLUE);
        g2.drawString("Turn: P" + activePlayerID, screenWidth - 150, 40);

        g2.dispose();
    }
}