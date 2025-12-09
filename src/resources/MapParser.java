
import java.nio.file.Files;
import java.util.List;

public class MapParser {
    public List<List<Character>> map;
    

    /*
    load/parse map dari file tertentu
    */
    public void loadMap(){
        try {
            String mapContent = Files.readString(null); // tergantung mapnya mau taruh dimana
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
