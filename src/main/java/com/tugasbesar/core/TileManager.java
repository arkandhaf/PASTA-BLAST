package com.tugasbesar.core;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TileManager - Manages tile rendering with asset caching
 * Handles floor and wall tiles efficiently
 */
public class TileManager {

    private GamePanel gp;
    private AssetManager assetManager;
    private Map<String, BufferedImage> tileSprites;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        this.assetManager = AssetManager.getInstance();
        this.tileSprites = new HashMap<>();
        loadTiles();
    }

    /**
     * Load all tile sprites from assets
     */
    private void loadTiles() {
        Map<String, BufferedImage> rawTiles = assetManager.loadAllTiles();
        tileSprites.clear();

        for (Map.Entry<String, BufferedImage> entry : rawTiles.entrySet()) {
            BufferedImage value = entry.getValue();
            if (value != null) {
                tileSprites.put(entry.getKey(), createScaledTile(value));
            }
        }

        System.out.println("✅ TileManager: Loaded " + tileSprites.size() + " tile types");
    }

    /**
     * Draw a tile at the given grid position
     */
    public void drawTile(Graphics2D g2, int col, int row, char tileType) {
        int x = col * gp.tileSize;
        int y = row * gp.tileSize;

        BufferedImage tileImage = getTileImage(tileType);

        if (tileImage != null) {
            // Draw sprite
            g2.drawImage(tileImage, x, y, null);
        } else {
            // Fallback to colored rectangle
            switch (tileType) {
                case '.': // Floor
                    g2.setColor(new java.awt.Color(200, 180, 160));
                    break;
                case '0': // Floor (legacy)
                    g2.setColor(new java.awt.Color(200, 180, 160));
                    break;
                case '1': // Wall (legacy)
                    g2.setColor(new java.awt.Color(100, 100, 100));
                    break;
                case 'X': // Wall
                    g2.setColor(new java.awt.Color(100, 100, 100));
                    break;
                default:
                    // Station tiles - use floor
                    g2.setColor(new java.awt.Color(200, 180, 160));
            }
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }
    }

    /**
     * Get tile image based on tile type character
     */
    private BufferedImage getTileImage(char tileType) {
        switch (tileType) {
            case '.': // Floor
                return tileSprites.get("floor");
            case '0': // Floor (legacy)
                return tileSprites.get("floor");
            case '1': // Wall (legacy)
                return tileSprites.get("wall");
            case 'X': // Wall
                return tileSprites.get("wall");
            default:
                // All station positions should show floor tiles
                return tileSprites.get("floor");
        }
    }

    /**
     * Draw entire map layout to the panel
     */
    public void drawMap(Graphics2D g2, List<List<Character>> mapLayout) {
        int rows = gp.maxScreenRow;
        int cols = gp.maxScreenCol;

        // Draw base floor tile across the entire active area to avoid gaps
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                drawTile(g2, col, row, '.');
            }
        }

        if (mapLayout == null) {
            return;
        }

        // Overlay special tiles (walls) on top of the floor
        for (int row = 0; row < Math.min(rows, mapLayout.size()); row++) {
            List<Character> mapRow = mapLayout.get(row);
            for (int col = 0; col < Math.min(cols, mapRow.size()); col++) {
                char tileType = mapRow.get(col);
                if (tileType == 'X' || tileType == '1') {
                    drawTile(g2, col, row, 'X');
                }
            }
        }
    }

    /**
     * Get specific tile sprite by name
     */
    public BufferedImage getTile(String tileName) {
        return tileSprites.get(tileName);
    }

    /**
     * Reload tiles (useful if assets change)
     */
    public void reloadTiles() {
        loadTiles();
    }

    private BufferedImage createScaledTile(BufferedImage original) {
        BufferedImage scaled = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        // Expand slightly to hide source transparency borders without solid fill
        g2.drawImage(original, -1, -1, gp.tileSize + 2, gp.tileSize + 2, null);
        g2.dispose();
        return scaled;
    }
}
