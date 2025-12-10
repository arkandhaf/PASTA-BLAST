package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.stations.Station;
import com.tugasbesar.models.manager.MapParser; 
import com.tugasbesar.models.manager.OrderManager; 

public class GamePanel extends JPanel implements Runnable {

    // --- Pengaturan Layar ---
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48 pixel
    
    // [PENTING] Ukuran Layar Sesuai Map B (14x10)
    public final int maxScreenCol = 14; 
    public final int maxScreenRow = 10;
    
    public final int screenWidth = tileSize * maxScreenCol; 
    public final int screenHeight = tileSize * maxScreenRow; 

    // --- System Utama ---
    int FPS = 60;
    Thread gameThread;
    
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
    public Station station[] = new Station[200]; 

    // --- MANAGERS ---
    public MapParser mapParser = new MapParser(this); 
    public OrderManager orderManager = OrderManager.getInstance();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2); 
        
        // Posisi Awal (Diatur biar gak nyangkut)
        chef1.setDefaultValues(2, 2); 
        chef2.setDefaultValues(3, 2);

        gameState = playState; 
    }

    public void setupGame() {
        // [FIX FINAL] CUKUP NAMA FILE SAJA.
        // Jangan pakai "maps/...", karena MapParser sudah otomatis nambahin folder itu.
        mapParser.loadMap("map-type-b.txt"); 
        
        System.out.println("✅ GamePanel: Setup selesai!");
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        
        // Timer Thread
        new Thread(() -> {
            while (isGameRunning && gameTime > 0) {
                try {
                    Thread.sleep(1000); 
                    if (gameState == playState) gameTime--;
                } catch (Exception e) {}
            }
        }).start();
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
            if (keyH.turnSwapPressed) {
                activePlayerID = (activePlayerID == 1) ? 2 : 1;
                keyH.turnSwapPressed = false;
            }
            
            if (activePlayerID == 1) {
                chef1.update(keyH); 
                chef2.update(null); 
            } else {
                chef1.update(null); 
                chef2.update(keyH); 
            }
            
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) station[i].update();
            }
            
            if (orderManager != null) orderManager.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            g2.setColor(Color.WHITE);
            g2.drawString("PRESS ENTER TO START", 100, 100);
        } else {
            // 1. Gambar Lantai (Background)
            if (mapParser != null) mapParser.draw(g2); 

            // 2. Gambar Stations
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) station[i].draw(g2);
            }

            // 3. Gambar Chefs
            chef1.draw(g2);
            chef2.draw(g2);
            
            // 4. UI
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(30F)); 
            g2.drawString("Time: " + gameTime, 20, 40); 
            
            Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 12)); 
            g2.drawString("▼ YOU", activeChef.x + 8, activeChef.y - 10);
            
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