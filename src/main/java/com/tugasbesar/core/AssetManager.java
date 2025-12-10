package com.tugasbesar.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * AssetManager - Loads and caches all game assets (images, sprites, etc.)
 * Uses lazy loading to improve performance
 */
public class AssetManager {
    
    private static AssetManager instance;
    private Map<String, BufferedImage> imageCache;
    private String assetBasePath;
    
    private AssetManager() {
        this.imageCache = new HashMap<>();
        // Get project root directory
        this.assetBasePath = System.getProperty("user.dir") + "/src/resources/assets/";
    }
    
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    /**
     * Load an image from the assets folder
     */
    public BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            String fullPath = assetBasePath + path;
            File file = new File(fullPath);
            
            if (!file.exists()) {
                System.err.println("❌ Asset not found: " + fullPath);
                return null;
            }
            
            BufferedImage image = ImageIO.read(file);
            imageCache.put(path, image);
            System.out.println("✅ Loaded asset: " + path);
            return image;
        } catch (Exception e) {
            System.err.println("❌ Failed to load asset: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load chef standing sprites
     */
    public Map<String, BufferedImage> loadChefStanding() {
        Map<String, BufferedImage> sprites = new HashMap<>();
        sprites.put("down", loadImage("chef/standing_down.png"));
        sprites.put("up", loadImage("chef/standing_up.png"));
        sprites.put("left", loadImage("chef/standing_left.png"));
        sprites.put("right", loadImage("chef/standing_right.png"));
        return sprites;
    }
    
    /**
     * Load chef walking sprites for given direction
     */
    public Map<String, BufferedImage> loadChefWalking(String direction) {
        Map<String, BufferedImage> sprites = new HashMap<>();
        sprites.put("left", loadImage("chef/walking_" + direction + "_left.png"));
        sprites.put("right", loadImage("chef/walking_" + direction + "_right.png"));
        return sprites;
    }
    
    /**
     * Load tile sprites
     */
    public BufferedImage loadTile(String tileType) {
        if ("floor".equals(tileType)) {
            return loadImage("tiles/floor.png");
        } else if ("wall".equals(tileType)) {
            return loadImage("tiles/wall.png");
        }
        return null;
    }
    
    /**
     * Load all tile sprites
     */
    public Map<String, BufferedImage> loadAllTiles() {
        Map<String, BufferedImage> tiles = new HashMap<>();
        tiles.put("floor", loadImage("tiles/floor.png"));
        tiles.put("wall", loadImage("tiles/wall.png"));
        return tiles;
    }
    
    /**
     * Load UI sprites
     */
    public BufferedImage loadUIImage(String imageName) {
        return loadImage("ui/" + imageName + ".png");
    }
    
    /**
     * Load station sprite by type
     */
    public BufferedImage loadStation(String stationType) {
        String filename = null;
        switch(stationType.toLowerCase()) {
            case "assembly":
                filename = "a_assembly_station.png";
                break;
            case "cutting":
                filename = "c_cutting_station.png";
                break;
            case "cooking":
                filename = "r_cooking_station.png";
                break;
            case "ingredient":
                filename = "i_ingredient_storage.png";
                break;
            case "plate":
                filename = "p_plate_storage.png";
                break;
            case "serving":
                filename = "s_serving_counter.png";
                break;
            case "trash":
                filename = "t_trash_station.png";
                break;
            case "washing":
                filename = "w_washing_station.png";
                break;
            default:
                return null;
        }
        return loadImage("stations/" + filename);
    }
    
    /**
     * Load all station sprites
     */
    public Map<String, BufferedImage> loadAllStations() {
        Map<String, BufferedImage> stations = new HashMap<>();
        stations.put("assembly", loadImage("stations/a_assembly_station.png"));
        stations.put("cutting", loadImage("stations/c_cutting_station.png"));
        stations.put("cooking", loadImage("stations/r_cooking_station.png"));
        stations.put("ingredient", loadImage("stations/i_ingredient_storage.png"));
        stations.put("plate", loadImage("stations/p_plate_storage.png"));
        stations.put("serving", loadImage("stations/s_serving_counter.png"));
        stations.put("trash", loadImage("stations/t_trash_station.png"));
        stations.put("washing", loadImage("stations/w_washing_station.png"));
        return stations;
    }
    
    /**
     * Load start screen image
     */
    public BufferedImage loadStartScreen() {
        return loadImage("ui/start.png");
    }
    
    /**
     * Load game over screen image
     */
    public BufferedImage loadGameOverScreen() {
        return loadImage("ui/oops.png");
    }
    
    /**
     * Load congratulations screen image
     */
    public BufferedImage loadCongratualtions() {
        return loadImage("ui/congratulations.png");
    }
    
    /**
     * Load quit screen image
     */
    public BufferedImage loadQuitScreen() {
        return loadImage("ui/quit.png");
    }
    
    /**
     * Clear all cached assets
     */
    public void clearCache() {
        imageCache.clear();
        System.out.println("✅ Asset cache cleared");
    }
    
    /**
     * Get cache statistics
     */
    public int getCacheSize() {
        return imageCache.size();
    }
}
