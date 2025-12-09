package com.tugasbesar.models.manager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapParser {
    public List<List<Character>> map;
    

    /** 
     * Load and parse map content from a file
     * @param filepath the path to the map file (.txt)
     * @throws IOException if the file cant be read
     * @return List<List<Character>> the map object
    */
    public List<List<Character>> loadMap(String filepath) throws IOException {
        String mapContent = Files.readString(Paths.get(filepath)); // tergantung mapnya mau taruh dimana
        map = new ArrayList<>();

        // split by line
        String[] lines = mapContent.split("\\r?\\n");  // Handles both \n and \r\n
        for (String line:lines){
            List<Character> row = new ArrayList<>();
            for (char c : line.toCharArray()){
                row.add(c);
            }
            map.add(row);
        }
        return map;
    }
}
