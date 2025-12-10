package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font; // [BARU] Import Font buat ngerapihin tulisan YOU
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.tugasbesar.models.actors.Chef;

// Import Station punya Depa
import com.tugasbesar.models.stations.Station;
import com.tugasbesar.models.stations.CookingStation;
import com.tugasbesar.models.stations.CuttingStation;

public class GamePanel extends JPanel implements Runnable {

    // --- Pengaturan Layar ---
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 pixel
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; 
    public final int screenHeight = tileSize * maxScreenRow; 

    // --- System Utama ---
    int FPS = 60;
    Thread gameThread;
    
    // State Game
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;

    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    
    public int gameTime = 180;
    public boolean isGameRunning = true;
    
    // --- MULTIPLAYER ---
    public int activePlayerID = 1; 
    public Chef chef1;
    public Chef chef2;
    
    // Wadah Station
    public Station station[] = new Station[20]; 

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // Inisialisasi Player
        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2); 
        
        // Posisi awal Chef (Manual call method baru di Chef)
        chef1.setDefaultValues(5, 5); 
        chef2.setDefaultValues(10, 5);

        gameState = playState; 
    }

    public void setupGame() {
        try {
            // Setup Manual (Hardcode)
            // Kompor di (6,6)
            station[0] = new CookingStation(6, 6); 
            
            // Talenan di (9,6) (Uncomment kalau file CuttingStation sudah ada)
            // station[1] = new CuttingStation(9, 6); 
            
            System.out.println("✅ Setup Manual Berhasil! Station siap.");
            
        } catch (Exception e) {
            System.out.println("⚠️ Error Setup Station! Cek file CookingStation.");
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        startGameTimer();
    }
    
    public void startGameTimer() {
        Thread timerThread = new Thread(() -> {
            while (isGameRunning && gameTime > 0) {
                try {
                    Thread.sleep(1000); 
                    if (gameState == playState) gameTime--;
                } catch (InterruptedException e) { e.printStackTrace(); }
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
        if (gameState == playState && isGameRunning) {
            
            // Logic Ganti Pemain
            if (keyH.turnSwapPressed) {
                activePlayerID = (activePlayerID == 1) ? 2 : 1;
                keyH.turnSwapPressed = false;
            }
            
            // Update Chef Aktif
            if (activePlayerID == 1) {
                chef1.update(keyH); 
                chef2.update(null); 
            } else {
                chef1.update(null); 
                chef2.update(keyH); 
            }
            
            // Update Station
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) {
                    station[i].update();
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            g2.setColor(Color.WHITE);
            g2.drawString("PRESS ENTER TO START", 100, 100);
        } else {
            // 1. Gambar Station DULUAN (Layer Bawah)
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) {
                    station[i].draw(g2);
                }
            }

            // 2. Gambar Chef (Layer Atas)
            chef1.draw(g2);
            chef2.draw(g2);
            
            // 3. UI Status
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(30F)); 
            g2.drawString("Time: " + gameTime, 20, 40); 
            
            // --- [UPDATE] INDIKATOR GILIRAN YANG RAPI ---
            Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
            
            g2.setColor(Color.YELLOW);
            // Pake Font Arial Bold ukuran 12 (Kecil Rapi)
            g2.setFont(new Font("Arial", Font.BOLD, 12)); 
            
            // Posisi pas di tengah atas kepala
            g2.drawString("▼ YOU", activeChef.x + 8, activeChef.y - 5);
            // ---------------------------------------------
            
            // Pause Overlay
            if(gameState == pauseState) {
                g2.setColor(new Color(0,0,0,150));
                g2.fillRect(0, 0, screenWidth, screenHeight);
                g2.setColor(Color.WHITE);
                g2.setFont(g2.getFont().deriveFont(50F));
                g2.drawString("PAUSED", screenWidth/2 - 100, screenHeight/2);
            }
        }
        g2.dispose();
    }
}