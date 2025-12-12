package com.tugasbesar.core;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.stations.Station;
import com.tugasbesar.models.manager.MapParser;
import com.tugasbesar.models.manager.OrderManager;

public class GamePanel extends JPanel implements Runnable {

    // --- SCREEN SETTINGS ---
    final int originalTileSize = 16;
    final int scale = 3; // 48px (Ukuran Stabil)
    public final int tileSize = originalTileSize * scale;

    // UKURAN MAP ASLI (14x10) + Sidebar Area (Total 20 Kolom)
    public final int maxMapCol = 14; 
    public final int maxMapRow = 10;
    public final int maxScreenCol = 20; // 14 Map + 6 Sidebar/Space
    public final int maxScreenRow = 10;

    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // --- SYSTEM ---
    int FPS = 60;
    Thread gameThread;

    // --- STATES ---
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int resultState = 3;      // [MENU] Result Screen
    public final int stageSelectState = 4; // [MENU] Stage Select
    public final int howToPlayState = 5;   // [MENU] How to Play

    // --- INPUT & COLLISION ---
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    // --- GAME DATA ---
    public int gameTime = 180;
    public boolean isGameRunning = true;
    public String message = "";
    public boolean messageOn = false;
    public int messageCounter = 0;

    public int activePlayerID = 1;
    public Chef chef1;
    public Chef chef2;
    public Station station[] = new Station[200];

    // --- MANAGERS & RENDERERS ---
    public MapParser mapParser = new MapParser(this);
    public OrderManager orderManager = OrderManager.getInstance();
    public TileManager tileManager;         // Visual Lantai (Punya Teman)
    public StationRenderer stationRenderer; // Visual Station (Punya Teman)
    
    // [MENU] UI MANAGER (Punya Kamu)
    public UI ui = new UI(this);

    // --- STAGE DATA ---
    public String[][] stageData = {
        {"map-type-b.txt", "100"}, // Stage 1
        {"map-type-b.txt", "250"}, // Stage 2
        {"map-type-b.txt", "400"}  // Stage 3
    };
    public boolean[] stageCleared = {false, false, false};
    public int currentStageIdx = 0;

    // --- VISUAL EXTRAS (Popups & Overlays) ---
    private HeldItemNotification[] heldItemPopups;
    private static final int HELD_ITEM_POPUP_DURATION = 150;
    private final List<TilePopup> tilePopups;
    private final BufferedImage pauseOverlayImage;
    // gameOverOverlayImage dihapus karena kita pakai resultState UI

    // DEKLARAIS MOUSE 
    public MouseHandler mouseH = new MouseHandler();


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        // Listener Mouse di Constructor
        this.addMouseListener(mouseH); // [BARU] Detect Klik
        this.addMouseMotionListener(mouseH); // [BARU] Detect Gerakan Cursor
        
        this.setFocusable(true);

        // Init Renderers
        tileManager = new TileManager(this);
        stationRenderer = new StationRenderer(this);

        chef1 = new Chef(this, keyH, "P1", 1);
        chef2 = new Chef(this, keyH, "P2", 2);

        // Spawn Points Aman (Di Lantai)
        chef1.setDefaultValues(2, 2);
        chef2.setDefaultValues(10, 2);

        // Init Popups
        heldItemPopups = new HeldItemNotification[] { new HeldItemNotification(), new HeldItemNotification() };
        tilePopups = new ArrayList<>();

        // Init Assets Overlay
        AssetManager assetManager = AssetManager.getInstance();
        BufferedImage pauseImage = assetManager.loadUIImage("paused");
        pauseOverlayImage = (pauseImage != null) ? pauseImage : assetManager.loadUIImage("pause");
        
        gameState = titleState; // Mulai dari Main Menu

        // Listener Mouse di Constructor
        this.addMouseListener(mouseH);

    }

    // --- GAME FLOW METHODS ---

    public void setupGame() {
        // Load map berdasarkan stage yang dipilih
        String mapFile = stageData[currentStageIdx][0];
        mapParser.loadMap(mapFile);
    }

    public void retryGame() {
        gameTime = 180;
        
        // Pastikan OrderManager punya method resetScore()
        if(orderManager != null) orderManager.resetScore();
        
        chef1.setDefaultValues(2, 2);
        chef2.setDefaultValues(10, 2);
        
        setupGame();
        gameState = playState;
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
                    else if (gameState == playState && gameTime <= 0)
                        gameState = resultState; // Waktu habis -> Result Screen
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
        if (gameState == playState) {
            // Swap Logic
            if (keyH.turnSwapPressed) {
                activePlayerID = (activePlayerID == 1) ? 2 : 1;
                keyH.turnSwapPressed = false;
            }

            // Chef Update (Hanya yang aktif)
            if (activePlayerID == 1) {
                chef1.update(keyH);
                chef2.update(null);
            } else {
                chef1.update(null);
                chef2.update(keyH);
            }

            // Station Update
            for (int i = 0; i < station.length; i++) {
                if (station[i] != null) station[i].update();
            }

            // Order Manager Update
            if (orderManager != null) orderManager.update();

            // Popups Update
            updateHeldItemPopup(chef1, heldItemPopups[0]);
            updateHeldItemPopup(chef2, heldItemPopups[1]);
            updateTilePopups();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Rendering Optimization settings
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        // --- STATE ROUTING ---
        
        // 1. MENU STATES (Digambar oleh UI.java)
        if (gameState == titleState) {
            ui.draw(g2);
        } 
        else if (gameState == stageSelectState) {
            ui.draw(g2);
        }
        else if (gameState == howToPlayState) {
            ui.draw(g2);
        }
        // 2. RESULT STATE (Game di background + Overlay Menu)
        else if (gameState == resultState) {
            drawGameplay(g2);
            ui.draw(g2);
        }
        // 3. GAMEPLAY & PAUSE
        else {
            drawGameplay(g2);
            
            if (gameState == pauseState) {
                drawOverlay(g2, "PAUSED", pauseOverlayImage);
            }
        }
        
        g2.dispose();
    }

    // --- HELPER: MENGGAMBAR ELEMEN GAMEPLAY ---
    private void drawGameplay(Graphics2D g2) {
        // 1. Map & Tiles (Pakai TileManager temanmu)
        if (tileManager != null && mapParser != null)
            tileManager.drawMap(g2, mapParser.mapLayout);

        drawStations(g2);

        // 2. Chefs
        chef1.draw(g2);
        chef2.draw(g2);

        // 3. HUD (Timer & Score)
        int hudBottom = drawTopHud(g2, 20, 30);

        // 4. Order List
        if (orderManager != null) {
            int orderStartY = Math.max(hudBottom + 10, 60);
            orderManager.draw(g2, 20, orderStartY);
        }

        // 5. Message Tengah Layar
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

        // 6. Visual Effects (Popups)
        drawTilePopups(g2);

        // 7. Indicators
        Chef activeChef = (activePlayerID == 1) ? chef1 : chef2;
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("▼", activeChef.x + 20, activeChef.y - 5);

        drawHeldItemPopup(g2, heldItemPopups[0], 20, screenHeight - 90, "P1");
        drawHeldItemPopup(g2, heldItemPopups[1], screenWidth - 200, screenHeight - 90, "P2");
    }

    // --- METHODS BAWAAN VISUAL (DARI KODE TEMANMU) ---
    // (Render Helper, Popups, etc)

    private void drawOverlay(Graphics2D g2, String fallbackText, BufferedImage overlayImage) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        if (!drawOverlayImage(g2, overlayImage)) {
            g2.setColor(Color.WHITE);
            drawCenteredText(g2, fallbackText, 40, 0);
        }
    }

    private boolean drawOverlayImage(Graphics2D g2, BufferedImage image) {
        if (image == null) return false;
        double scale = Math.max(screenWidth / (double) image.getWidth(), screenHeight / (double) image.getHeight());
        int drawWidth = (int) Math.round(image.getWidth() * scale);
        int drawHeight = (int) Math.round(image.getHeight() * scale);
        int drawX = (screenWidth - drawWidth) / 2;
        int drawY = (screenHeight - drawHeight) / 2;
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        return true;
    }

    private int drawTopHud(Graphics2D g2, int x, int baselineY) {
        String timeText = "TIME: " + gameTime;
        String scoreText = (orderManager != null) ? "SCORE: " + orderManager.getScore() : "SCORE: 0";
        Font originalFont = g2.getFont();
        Color originalColor = g2.getColor();
        Font font = new Font("Arial", Font.BOLD, 20);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getAscent() + fm.getDescent();
        int gap = 4;
        int paddingX = 18;
        int paddingY = 12;
        int width = Math.max(fm.stringWidth(timeText), fm.stringWidth(scoreText));
        int rectX = x - paddingX / 2;
        int rectY = baselineY - fm.getAscent() - (paddingY / 2);
        int rectWidth = width + paddingX;
        int rectHeight = (lineHeight * 2) + gap + paddingY;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawString(timeText, x, baselineY);
        g2.drawString(scoreText, x, baselineY + lineHeight + gap);
        g2.setFont(originalFont);
        g2.setColor(originalColor);
        return rectY + rectHeight;
    }

    private void drawCenteredText(Graphics2D g2, String text, int size, int yOffset) {
        g2.setFont(new Font("Arial", Font.BOLD, size));
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = screenWidth / 2 - length / 2;
        int y = screenHeight / 2 + yOffset;
        g2.drawString(text, x, y);
    }

    private void drawStations(Graphics2D g2) {
        if (stationRenderer == null) return;
        for (Station value : station) {
            if (value != null) {
                if ("X".equalsIgnoreCase(value.getSymbol())) continue;
                stationRenderer.drawStation(g2, value);
            }
        }
    }

    public boolean isChefCollision(Chef mover, int nextX, int nextY) {
        Chef other = (mover == chef1) ? chef2 : chef1;
        if (other == null) return false;
        Rectangle moverArea = new Rectangle(nextX + mover.solidAreaDefaultX, nextY + mover.solidAreaDefaultY, mover.solidArea.width, mover.solidArea.height);
        Rectangle otherArea = new Rectangle(other.x + other.solidAreaDefaultX, other.y + other.solidAreaDefaultY, other.solidArea.width, other.solidArea.height);
        return moverArea.intersects(otherArea);
    }

    private void updateHeldItemPopup(Chef chef, HeldItemNotification popup) {
        if (chef == null || popup == null) return;
        Item current = chef.getHeldItem();
        if (current != null) {
            if (popup.lastItem != current) popup.lastItem = current;
            popup.holding = true;
            popup.timer = HELD_ITEM_POPUP_DURATION;
        } else {
            if (popup.holding) popup.holding = false;
            if (popup.lastItem != null) {
                if (popup.timer > 0) popup.timer--;
                else popup.lastItem = null;
            }
        }
    }

    private void drawHeldItemPopup(Graphics2D g2, HeldItemNotification popup, int x, int y, String label) {
        if (popup == null || popup.lastItem == null) return;
        float alpha = popup.holding ? 1f : Math.min(1f, popup.timer / (float) HELD_ITEM_POPUP_DURATION);
        if (alpha <= 0f) return;
        BufferedImage icon = AssetManager.getInstance().getItemIcon(popup.lastItem);
        String itemName = getDisplayName(popup.lastItem);
        int width = 180;
        int height = 70;
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(x, y, width, height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 12, 12);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(label, x + 12, y + 18);
        if (icon != null) g2.drawImage(icon, x + 12, y + 24, 40, 40, null);
        else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillOval(x + 12, y + 26, 36, 36);
            g2.setColor(Color.WHITE);
            g2.drawString("?", x + 26, y + 50);
        }
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(itemName, x + 58, y + 48);
        g2.setComposite(original);
    }

    public void pushTilePopup(int tileX, int tileY, Item item, String stationSymbol, String message, String detail, Color accent) {
        pushTilePopup(tileX, tileY, item, stationSymbol, message, detail, accent, TilePopup.DEFAULT_DURATION);
    }

    public void pushStationFeedback(Station station, Item item, String message, Color accent) {
        if (station == null) return;
        String detail = (item != null) ? getDisplayName(item) : station.getName();
        pushTilePopup(station.getPosX(), station.getPosY(), item, station.getSymbol(), message, detail, accent);
    }

    public void pushTilePopup(int tileX, int tileY, Item item, String stationSymbol, String message, String detail, Color accent, int duration) {
        if (accent == null) accent = Color.ORANGE;
        if (duration <= 0) duration = TilePopup.DEFAULT_DURATION;
        for (TilePopup popup : tilePopups) {
            if (popup.matches(tileX, tileY, message)) {
                popup.refresh(item, stationSymbol, detail, accent, duration);
                return;
            }
        }
        tilePopups.add(new TilePopup(tileX, tileY, item, stationSymbol, message, detail, accent, duration));
    }

    private void updateTilePopups() {
        Iterator<TilePopup> iterator = tilePopups.iterator();
        while (iterator.hasNext()) {
            TilePopup popup = iterator.next();
            popup.tick();
            if (popup.isExpired()) iterator.remove();
        }
    }

    private void drawTilePopups(Graphics2D g2) {
        java.util.Map<Long, Integer> stacks = new java.util.HashMap<>();
        for (TilePopup popup : tilePopups) {
            long key = (((long) popup.tileX) << 32) | (popup.tileY & 0xffffffffL);
            int stackIndex = stacks.getOrDefault(key, 0);
            popup.draw(g2, tileSize, stackIndex);
            stacks.put(key, stackIndex + 1);
        }
    }

    private String getDisplayName(Item item) {
        if (item == null) return "";
        if (item instanceof com.tugasbesar.models.item.Ingredient) {
            com.tugasbesar.models.item.Ingredient ing = (com.tugasbesar.models.item.Ingredient) item;
            return ing.getName() + " (" + ing.getState() + ")";
        }
        if (item instanceof com.tugasbesar.models.item.kitchen_utensil.Plate) {
            com.tugasbesar.models.item.kitchen_utensil.Plate plate = (com.tugasbesar.models.item.kitchen_utensil.Plate) item;
            if (!plate.getContents().isEmpty()) {
                com.tugasbesar.models.interfaces.Processable top = plate.getContents().get(0);
                return "Plate - " + top.getName();
            }
            return "Plate (Empty)";
        }
        return item.getName();
    }

    private static class HeldItemNotification {
        private Item lastItem;
        private int timer;
        private boolean holding;
    }

    private class TilePopup {
        private static final int DEFAULT_DURATION = 150;
        private final int tileX;
        private final int tileY;
        private final String message;
        private Item item;
        private String stationSymbol;
        private String detail;
        private Color accent;
        private int timer;
        private int duration;

        TilePopup(int tileX, int tileY, Item item, String stationSymbol, String message, String detail, Color accent, int duration) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.message = message;
            this.item = item;
            this.stationSymbol = stationSymbol;
            this.detail = computeDetail(detail, item, stationSymbol);
            this.accent = accent;
            this.duration = duration;
            this.timer = duration;
        }

        boolean matches(int tileX, int tileY, String message) {
            return this.tileX == tileX && this.tileY == tileY && this.message.equals(message);
        }

        void refresh(Item item, String stationSymbol, String detail, Color accent, int duration) {
            this.item = item;
            if (stationSymbol != null) this.stationSymbol = stationSymbol;
            this.detail = computeDetail(detail, item, this.stationSymbol);
            this.accent = accent;
            if (duration <= 0) duration = DEFAULT_DURATION;
            this.duration = duration;
            this.timer = duration;
        }

        private String computeDetail(String detail, Item item, String stationSymbol) {
            if (detail != null && !detail.isBlank()) return detail;
            if (item != null) return getDisplayName(item);
            if (stationSymbol != null) return "";
            return "";
        }

        void tick() {
            if (timer > 0) timer--;
        }

        boolean isExpired() {
            return timer <= 0;
        }

        private BufferedImage resolveIcon() {
            if (item != null) return AssetManager.getInstance().getItemIcon(item);
            if (stationSymbol != null) return AssetManager.getInstance().getStationIcon(stationSymbol);
            return null;
        }

        void draw(Graphics2D g2, int tileSize, int stackIndex) {
            float alpha = Math.min(1f, timer / (float) duration);
            int width = 160;
            int height = 52;
            int screenX = tileX * tileSize + (tileSize - width) / 2;
            int screenY = tileY * tileSize - height - 8 - (stackIndex * (height + 6));
            if (screenY < 0) screenY = tileY * tileSize + tileSize + 8 + (stackIndex * (height + 6));
            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(new Color(0, 0, 0, 210));
            g2.fillRoundRect(screenX, screenY, width, height, 12, 12);
            g2.setColor(accent);
            g2.drawRoundRect(screenX, screenY, width, height, 12, 12);
            BufferedImage icon = resolveIcon();
            if (icon != null) g2.drawImage(icon, screenX + 10, screenY + 10, 32, 32, null);
            else {
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(screenX + 12, screenY + 12, 28, 28);
                g2.setColor(Color.WHITE);
                g2.drawString("?", screenX + 24, screenY + 31);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(message, screenX + 52, screenY + 24);
            if (detail != null && !detail.isBlank()) {
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.drawString(detail, screenX + 52, screenY + 40);
            }
            g2.setComposite(original);
        }
    }
}