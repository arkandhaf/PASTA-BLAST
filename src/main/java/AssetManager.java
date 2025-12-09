import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static final String ASSETS_PATH = "src/resources/assets/";
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            File file = new File(ASSETS_PATH + path);
            BufferedImage image = ImageIO.read(file);
            imageCache.put(path, image);
            return image;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getTile(char tileType) {
        switch (tileType) {
            case 'X':
                return loadImage("tiles/wall.png");
            case '.':
            case 'A':
            case 'I':
            case 'P':
            case 'S':
            case 'R':
            case 'W':
            case 'V':
            case 'C':
            case 'T':
                return loadImage("tiles/floor.png");
            default:
                return null;
        }
    }

    public static void clearCache() {
        imageCache.clear();
    }
}
