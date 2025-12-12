package com.tugasbesar.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.abstracts.KitchenUtensil;

/**
 * AssetManager - Loads and caches all game assets (images, sprites, etc.)
 * Uses lazy loading to improve performance
 */
public class AssetManager {

    private static AssetManager instance;
    private Map<String, BufferedImage> imageCache;
    private final Path assetBasePath;

    private static final Map<String, String> INGREDIENT_ICON_MAP;
    private static final Map<String, String> DISH_ICON_MAP;
    private static final Map<String, String> UTENSIL_ICON_MAP;
    private static final Map<String, String> STATION_ICON_MAP;

    private AssetManager() {
        this.imageCache = new HashMap<>();
        // Resolve asset folder relative to workspace root using platform-safe paths
        this.assetBasePath = Paths.get(System.getProperty("user.dir"), "src", "resources", "assets");
    }

    static {
        Map<String, String> ingredients = new HashMap<>();
        ingredients.put("Tomato", "tomato");
        ingredients.put("Pasta", "pasta");
        ingredients.put("Beef", "meat");
        ingredients.put("Fish", "fish");
        ingredients.put("Shrimp", "shrimp");
        INGREDIENT_ICON_MAP = Map.copyOf(ingredients);

        Map<String, String> dishes = new HashMap<>();
        dishes.put("Pasta Marinara", "pasta_marinara");
        dishes.put("Pasta Bolognese", "pasta_bolognese");
        dishes.put("Frutti di Mare", "pasta_frutti_di_mare");
        DISH_ICON_MAP = Map.copyOf(dishes);

        Map<String, String> utensils = new HashMap<>();
        utensils.put("Boiling Pot", "boiling_pot");
        utensils.put("Frying Pan", "frying_pan");
        utensils.put("Plate", "empty_plate");
        UTENSIL_ICON_MAP = Map.copyOf(utensils);

        Map<String, String> stations = new HashMap<>();
        stations.put("A", "assembly");
        stations.put("C", "cutting");
        stations.put("K", "cooking");
        stations.put("R", "cooking");
        stations.put("I", "ingredient");
        stations.put("J", "ingredient");
        stations.put("B", "ingredient");
        stations.put("F", "ingredient");
        stations.put("L", "ingredient");
        stations.put("P", "plate");
        stations.put("S", "serving");
        stations.put("V", "serving");
        stations.put("T", "trash");
        stations.put("W", "washing");
        STATION_ICON_MAP = Map.copyOf(stations);
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
            Path imagePath = assetBasePath.resolve(Paths.get(path));

            if (!Files.exists(imagePath)) {
                System.err.println("❌ Asset not found: " + imagePath);
                return null;
            }

            BufferedImage image = ImageIO.read(imagePath.toFile());
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
            return loadImage("tiles/another-floor.png");
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
        tiles.put("floor", loadImage("tiles/another-floor.png"));
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
        switch (stationType.toLowerCase()) {
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

    /**
     * Load an icon representing the supplied item if an asset exists.
     */
    public BufferedImage getItemIcon(Item item) {
        if (item == null) {
            return null;
        }

        String relativePath = resolveIconPath(item);
        if (relativePath == null) {
            return null;
        }

        return loadImage(relativePath);
    }

    public BufferedImage getStationIcon(String symbol) {
        if (symbol == null) {
            return null;
        }
        String key = STATION_ICON_MAP.get(symbol.toUpperCase());
        if (key == null) {
            return null;
        }
        return loadStation(key);
    }

    private String resolveIconPath(Item item) {
        if (item instanceof Ingredient) {
            return resolveIngredientIcon((Ingredient) item);
        }

        if (item instanceof Plate) {
            return resolvePlateIcon((Plate) item);
        }

        if (item instanceof Dish) {
            return resolveDishIcon(((Dish) item).getName());
        }

        if (item instanceof KitchenUtensil) {
            return resolveUtensilIcon(item.getName());
        }

        return null;
    }

    private String resolveIngredientIcon(Ingredient ingredient) {
        String base = INGREDIENT_ICON_MAP.getOrDefault(ingredient.getName(), ingredient.getName().toLowerCase());
        String suffix = "raw";
        IngredientState state = ingredient.getState();
        if (state == IngredientState.CHOPPED) {
            suffix = "chopped";
        } else if (state == IngredientState.COOKED || state == IngredientState.SERVED) {
            suffix = "cooked";
        } else if (state == IngredientState.BURNED) {
            suffix = "cooked"; // Fallback icon
        }
        return "ingredients/" + base.toLowerCase() + "_" + suffix + ".png";
    }

    private String resolvePlateIcon(Plate plate) {
        List<Processable> contents = plate.getContents();
        if (!contents.isEmpty()) {
            Processable top = contents.get(0);
            if (top instanceof Dish) {
                return resolveDishIcon(((Dish) top).getName());
            }
            if (top instanceof Ingredient) {
                return resolveIngredientIcon((Ingredient) top);
            }
        }
        return "utensils/empty_plate.png";
    }

    private String resolveDishIcon(String dishName) {
        if (dishName == null) {
            return null;
        }
        String slug = DISH_ICON_MAP.get(dishName);
        if (slug == null) {
            slug = normalizeName(dishName);
        }
        return "meals/" + slug + ".png";
    }

    private String resolveUtensilIcon(String utensilName) {
        if (utensilName == null) {
            return null;
        }
        String slug = UTENSIL_ICON_MAP.get(utensilName);
        if (slug == null) {
            slug = normalizeName(utensilName);
        }
        return "utensils/" + slug + ".png";
    }

    private String normalizeName(String value) {
        String lower = value.toLowerCase();
        StringBuilder builder = new StringBuilder();
        for (char c : lower.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                builder.append(c);
            } else if (c == ' ' || c == '-' || c == '\t') {
                builder.append('_');
            }
        }
        String normalized = builder.toString().replaceAll("_+", "_");
        normalized = normalized.replaceAll("^_", "");
        normalized = normalized.replaceAll("_$", "");
        return normalized;
    }
}
