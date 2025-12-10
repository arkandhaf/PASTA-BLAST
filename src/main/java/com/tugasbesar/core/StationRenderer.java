package com.tugasbesar.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * StationRenderer - Manages station sprite rendering
 * Maps station types to their asset sprites
 */
public class StationRenderer {
    
    private GamePanel gp;
    private AssetManager assetManager;
    private Map<String, BufferedImage> stationSprites;
    
    public StationRenderer(GamePanel gp) {
        this.gp = gp;
        this.assetManager = AssetManager.getInstance();
        this.stationSprites = new HashMap<>();
        loadStations();
    }
    
    /**
     * Load all station sprites from assets
     */
    private void loadStations() {
        stationSprites = assetManager.loadAllStations();
        System.out.println("✅ StationRenderer: Loaded " + stationSprites.size() + " station types");
    }
    
    /**
     * Draw a station sprite at the given position
     */
    public void drawStation(Graphics2D g2, int col, int row, String stationType) {
        int x = col * gp.tileSize;
        int y = row * gp.tileSize;
        
        drawStationAt(g2, x, y, stationType);
    }
    
    /**
     * Draw station at pixel coordinates
     */
    public void drawStationAt(Graphics2D g2, int x, int y, String stationType) {
        BufferedImage stationImage = getStationImage(stationType);
        
        if (stationImage != null) {
            g2.drawImage(stationImage, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            // Fallback rendering
            drawFallbackStation(g2, x, y, stationType);
        }
    }
    
    /**
     * Get station sprite by type
     */
    private BufferedImage getStationImage(String stationType) {
        if (stationType == null) {
            return null;
        }
        
        String typeKey = stationType.toLowerCase();
        
        // Try exact match first
        if (stationSprites.containsKey(typeKey)) {
            return stationSprites.get(typeKey);
        }
        
        // Try to match by station class name patterns
        if (typeKey.contains("assembly")) {
            return stationSprites.get("assembly");
        } else if (typeKey.contains("cutting")) {
            return stationSprites.get("cutting");
        } else if (typeKey.contains("cooking")) {
            return stationSprites.get("cooking");
        } else if (typeKey.contains("ingredient")) {
            return stationSprites.get("ingredient");
        } else if (typeKey.contains("plate")) {
            return stationSprites.get("plate");
        } else if (typeKey.contains("serving")) {
            return stationSprites.get("serving");
        } else if (typeKey.contains("trash")) {
            return stationSprites.get("trash");
        } else if (typeKey.contains("washing")) {
            return stationSprites.get("washing");
        }
        
        return null;
    }
    
    /**
     * Fallback rendering if sprite not found
     */
    private void drawFallbackStation(Graphics2D g2, int x, int y, String stationType) {
        java.awt.Color color = getStationColor(stationType);
        g2.setColor(color);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        
        // Draw border
        g2.setColor(java.awt.Color.BLACK);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRect(x, y, gp.tileSize, gp.tileSize);
    }
    
    /**
     * Get color for station type (fallback)
     */
    private java.awt.Color getStationColor(String stationType) {
        if (stationType == null) {
            return java.awt.Color.GRAY;
        }
        
        String type = stationType.toLowerCase();
        
        if (type.contains("assembly")) return new java.awt.Color(200, 100, 100);
        if (type.contains("cutting")) return new java.awt.Color(100, 200, 100);
        if (type.contains("cooking")) return new java.awt.Color(255, 150, 0);
        if (type.contains("ingredient")) return new java.awt.Color(100, 150, 200);
        if (type.contains("plate")) return new java.awt.Color(200, 200, 100);
        if (type.contains("serving")) return new java.awt.Color(200, 100, 200);
        if (type.contains("trash")) return new java.awt.Color(50, 50, 50);
        if (type.contains("washing")) return new java.awt.Color(100, 200, 200);
        
        return java.awt.Color.GRAY;
    }
    
    /**
     * Reload stations (useful if assets change)
     */
    public void reloadStations() {
        loadStations();
    }
    
    /**
     * Get specific station sprite
     */
    public BufferedImage getStation(String stationName) {
        return stationSprites.get(stationName);
    }
}
