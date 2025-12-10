package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
    public final int gameOverState = 3;

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
    public AssetManager assetManager = AssetManager.getInstance();
    public StationRenderer stationRenderer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(new MouseHandler(this));
        this.setFocusable(true);
        
        // Initialize station renderer
        this.stationRenderer = new StationRenderer(this);
        
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
            drawTitleScreen(g2);
        } else if (gameState == playState || gameState == pauseState) {
            drawGameScreen(g2);
            
            if (gameState == pauseState) {
                drawPauseOverlay(g2);
            }
        } else if (gameState == gameOverState) {
            drawGameOverScreen(g2);
        }
        g2.dispose();
    }

    private void drawTitleScreen(Graphics2D g2) {
        // Try to load and draw background image
        BufferedImage startImage = assetManager.loadStartScreen();
        if (startImage != null) {
            // Draw stretched background image
            g2.drawImage(startImage, 0, 0, screenWidth, screenHeight, null);
        } else {
            // Fallback to colored background if image fails
            g2.setColor(new Color(20, 20, 40));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        // Optional: Add semi-transparent overlay for better text readability
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "PASTA NIMOS COOKED";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (screenWidth - titleWidth) / 2, 80);
        
        // Subtitle
        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String subtitle = "2-Player Cooperative Cooking Game";
        int subtitleWidth = g2.getFontMetrics().stringWidth(subtitle);
        g2.drawString(subtitle, (screenWidth - subtitleWidth) / 2, 130);
        
        // Instructions
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String start = "PRESS ENTER TO START";
        int startWidth = g2.getFontMetrics().stringWidth(start);
        g2.drawString(start, (screenWidth - startWidth) / 2, 200);
        
        // Controls help
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] controls = {
            "Controls:",
            "W/A/S/D - Move",
            "SPACE - Interact",
            "SHIFT - Dash",
            "ENTER - Swap Players",
            "P - Pause/Resume"
        };
        int startY = 280;
        for (String control : controls) {
            g2.drawString(control, (screenWidth - g2.getFontMetrics().stringWidth(control)) / 2, startY);
            startY += 25;
        }
    }

    private void drawGameScreen(Graphics2D g2) {
        // 1. Draw Floor (Background)
        if (mapParser != null) mapParser.draw(g2);

        // 2. Draw Stations with sprites
        for (int i = 0; i < station.length; i++) {
            if (station[i] != null) {
                // Draw station sprite
                int stationCol = station[i].getPosX();
                int stationRow = station[i].getPosY();
                String stationType = station[i].getClass().getSimpleName();
                stationRenderer.drawStation(g2, stationCol, stationRow, stationType);
            }
        }

        // 3. Draw Chefs
        chef1.draw(g2);
        chef2.draw(g2);
        
        // 4. Draw Active Player Indicator
        Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("▼ YOU", activeChef.x + 8, activeChef.y - 10);
        
        // 5. Draw HUD
        drawHUD(g2);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Timer
        String timeStr = formatTime(gameTime);
        g2.drawString("Time: " + timeStr, 20, 40);
        
        // Active Player
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("Active Player: P" + activePlayerID, 20, 65);
        
        // Orders
        if (orderManager != null) {
            g2.setColor(new Color(0, 255, 0)); // Green
            g2.drawString("Orders Active: " + orderManager.getActiveOrder().size(), 20, 85);
        }
        
        // Instructions (bottom)
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("W/A/S/D: Move | SPACE: Interact | SHIFT: Dash | ENTER: Swap | P: Pause", 20, screenHeight - 10);
    }

    private void drawPauseOverlay(Graphics2D g2) {
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Pause text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        String pauseText = "PAUSED";
        int pauseWidth = g2.getFontMetrics().stringWidth(pauseText);
        g2.drawString(pauseText, (screenWidth - pauseWidth) / 2, screenHeight / 2 - 50);
        
        // Resume instruction
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String resumeText = "Press P to Resume";
        int resumeWidth = g2.getFontMetrics().stringWidth(resumeText);
        g2.drawString(resumeText, (screenWidth - resumeWidth) / 2, screenHeight / 2 + 40);
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        // Try to load and draw background image
        BufferedImage gameOverImage = assetManager.loadGameOverScreen();
        if (gameOverImage != null) {
            g2.drawImage(gameOverImage, 0, 0, screenWidth, screenHeight, null);
        } else {
            // Fallback to colored background if image fails
            g2.setColor(new Color(20, 10, 10));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        // Add semi-transparent overlay for text readability
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Game Over Title
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        String gameOverText = "GAME OVER";
        int gameOverWidth = g2.getFontMetrics().stringWidth(gameOverText);
        g2.drawString(gameOverText, (screenWidth - gameOverWidth) / 2, 100);
        
        // Stats
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        
        int ordersActive = (orderManager != null) ? orderManager.getActiveOrder().size() : 0;
        String statsText = "Orders Active: " + ordersActive;
        int statsWidth = g2.getFontMetrics().stringWidth(statsText);
        g2.drawString(statsText, (screenWidth - statsWidth) / 2, 180);
        
        String timeText = "Time Used: " + formatTime(180 - gameTime);
        int timeWidth = g2.getFontMetrics().stringWidth(timeText);
        g2.drawString(timeText, (screenWidth - timeWidth) / 2, 220);
        
        // Restart instruction
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String restartText = "Press ENTER to Return to Menu";
        int restartWidth = g2.getFontMetrics().stringWidth(restartText);
        g2.drawString(restartText, (screenWidth - restartWidth) / 2, 300);
        
        // Quit instruction
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String quitText = "Press ESC to Quit";
        int quitWidth = g2.getFontMetrics().stringWidth(quitText);
        g2.drawString(quitText, (screenWidth - quitWidth) / 2, 330);
    }

    public void triggerGameOver() {
        isGameRunning = false;
        gameState = gameOverState;
    }
}