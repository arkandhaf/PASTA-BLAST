package com.tugasbesar.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
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
        tileSprites = assetManager.loadAllTiles();
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
            g2.drawImage(tileImage, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            // Fallback to colored rectangle
            switch(tileType) {
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
        switch(tileType) {
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
}
