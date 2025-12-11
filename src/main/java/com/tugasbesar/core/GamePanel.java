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

    // --- SCREEN SETTINGS (RESET KE STANDARD) ---
    final int originalTileSize = 16;
    final int scale = 3; // 48px (Ukuran Paling Aman & Stabil)
    public final int tileSize = originalTileSize * scale;

    // UKURAN MAP ASLI (14x10)
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;

    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // --- SYSTEM ---
    int FPS = 60;
    Thread gameThread;

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;
    public final int gameOverState = 3;

    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    public int gameTime = 180;
    public boolean isGameRunning = true;

    public String message = "";
    public boolean messageOn = false;
    public int messageCounter = 0;

    public int activePlayerID = 1;
    public Chef chef1;
    public Chef chef2;
    public Station station[] = new Station[200];

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

        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2);

        // [FIX SPAWN POINT]
        // P1 di Kiri (2, 2)
        // P2 di Kanan (10, 2) -> Koordinat 10 pasti aman di dalam map 14 kolom
        chef1.setDefaultValues(2, 2);
        chef2.setDefaultValues(10, 2);

        gameState = playState;
    }

    public void setupGame() {
        mapParser.loadMap("map-type-b.txt");
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
        messageCounter = 0;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        new Thread(() -> {
            while (isGameRunning) {
                try {
                    Thread.sleep(1000);
                    if (gameState == playState && gameTime > 0)
                        gameTime--;
                    else if (gameTime <= 0)
                        gameState = gameOverState;
                } catch (Exception e) {
                }
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
        if (gameState == playState) {
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
                if (station[i] != null)
                    station[i].update();
            }
            if (orderManager != null)
                orderManager.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            drawCenteredText(g2, "PASTA NIMONS COOKED", 30, -30);
            drawCenteredText(g2, "PRESS ENTER TO START", 20, 30);
        } else {
            // 1. Map
            if (mapParser != null)
                mapParser.draw(g2);
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null)
                    station[i].draw(g2);
            }

            // 2. Chef
            chef1.draw(g2);
            chef2.draw(g2);

            // 3. UI (Sederhana di pojok kiri atas)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("TIME: " + gameTime, 20, 30);

            // Order List (Digambar di bawah Timer)
            if (orderManager != null) {
                orderManager.draw(g2, 20, 50);
            }

            // Message
            if (messageOn) {
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.setColor(Color.YELLOW);
                drawCenteredText(g2, message, 30, 0);
                messageCounter++;
                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }

            // Indicator
            Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString("▼", activeChef.x + 20, activeChef.y - 5);

            if (gameState == pauseState)
                drawOverlay(g2, "PAUSED");
            if (gameState == gameOverState)
                drawOverlay(g2, "GAME OVER");
        }
        g2.dispose();
    }

    private void drawOverlay(Graphics2D g2, String text) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setColor(Color.WHITE);
        drawCenteredText(g2, text, 40, 0);
    }

    private void drawCenteredText(Graphics2D g2, String text, int size, int yOffset) {
        g2.setFont(new Font("Arial", Font.BOLD, size));
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = screenWidth / 2 - length / 2;
        int y = screenHeight / 2 + yOffset;
        g2.drawString(text, x, y);
    }
}