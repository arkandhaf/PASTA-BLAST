package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.stations.Station;

// --- [UBAH] IMPORT MAP PARSER & ORDER MANAGER ---
import com.tugasbesar.models.manager.MapParser; // Ganti TileManager jadi MapParser
import com.tugasbesar.models.manager.OrderManager; 

public class GamePanel extends JPanel implements Runnable {

    // --- Pengaturan Layar ---
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 pixel
    
    // Ukuran Map (Sesuaikan dengan file .txt nanti)
    public final int maxScreenCol = 20; 
    public final int maxScreenRow = 15;
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
    
    // Wadah Station (Array besar biar muat 1 map)
    public Station station[] = new Station[100]; 

    // --- [UBAH] INISIALISASI MANAGER ---
    // Ganti nama variabel dari tileM jadi mapParser biar jelas
    public MapParser mapParser = new MapParser(this); 
    
    public OrderManager orderManager = OrderManager.getInstance();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // Inisialisasi Player
        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2); 
        
        // Posisi awal Chef
        chef1.setDefaultValues(5, 5); 
        chef2.setDefaultValues(6, 5);

        gameState = playState; 
    }

    public void setupGame() {
        // --- [PENTING] LOAD MAP DARI FILE ---
        // Panggil MapParser untuk baca file dan isi array station[]
        // Pastikan path filenya benar!
        mapParser.loadMap("/maps/map01.txt"); 
        
        System.out.println("✅ Map & Station Loaded Successfully via MapParser!");
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
            
            // Update Station (Logic masak, potong, dll)
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) {
                    station[i].update();
                }
            }
            
            // Update Order
            if (orderManager != null) {
                orderManager.update();
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
            // --- URUTAN GAMBAR (LAYERING) ---
            
            // 1. BACKGROUND (Lantai) - Digambar oleh MapParser
            if (mapParser != null) {
                mapParser.draw(g2); 
            }

            // 2. STATIONS (Objek di atas lantai)
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) {
                    station[i].draw(g2);
                }
            }

            // 3. PLAYERS (Chef)
            chef1.draw(g2);
            chef2.draw(g2);
            
            // 4. UI / HUD
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(30F)); 
            g2.drawString("Time: " + gameTime, 20, 40); 
            
            // Indikator "YOU"
            Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 12)); 
            g2.drawString("▼ YOU", activeChef.x + 8, activeChef.y - 10);
            
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