package com.tugasbesar.models.manager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tugasbesar.core.GamePanel;
import com.tugasbesar.models.stations.*; 
import com.tugasbesar.models.actors.Chef; 

public class MapParser {
    
    GamePanel gp;
    public List<List<Character>> mapLayout; 

    public MapParser(GamePanel gp) {
        this.gp = gp;
        this.mapLayout = new ArrayList<>();
    }

    public void loadMap(String filename) {
        try {
            String projectPath = System.getProperty("user.dir");
            String fullPath = projectPath + "/src/resources/assets/maps/" + filename;
            
            System.out.println("🔍 Mencari file di: " + fullPath);
            File file = new File(fullPath);

            if (!file.exists()) {
                System.err.println("❌ ERROR: File tidak ditemukan!");
                return; 
            }

            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            int stationIndex = 0;
            mapLayout.clear(); 

            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                List<Character> mapRow = new ArrayList<>();
                char[] chars = line.toCharArray();
                for (int col = 0; col < chars.length; col++) {
                    char c = chars[col];
                    mapRow.add(c);
                    if (stationIndex < gp.station.length) {
                        createStation(c, col, row, stationIndex);
                        if (gp.station[stationIndex] != null) stationIndex++;
                    }
                }
                mapLayout.add(mapRow);
                row++;
            }
            br.close();
            System.out.println("✅ SUKSES! Map terbaca.");

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createStation(char c, int x, int y, int index) {
        switch (c) {
            // --- ALAT ---
            case 'R': // R = STOVE (Request User)
            case 'K': // K = Kompor (Backup)
                gp.station[index] = new CookingStation(x, y); 
                break;
            
            case 'C': gp.station[index] = new CuttingStation(x, y); break;
            case 'W': gp.station[index] = new WashingStation(x, y); break;
            
            // --- LAYANAN ---
            case 'S': 
            case 'V': gp.station[index] = new ServingStation(x, y, gp); break;
            
            case 'P': 
            case 'A': gp.station[index] = new PlateStorage(x, y); break;
            
            case 'T': gp.station[index] = new TrashStation(x, y); break;

            // --- BAHAN MAKANAN (Sesuai Factory) ---
            case 'I': 
                gp.station[index] = new IngredientStorage(x, y, "Tomato"); 
                break;
            case 'J': 
                gp.station[index] = new IngredientStorage(x, y, "Pasta"); 
                break;
            case 'B': 
                gp.station[index] = new IngredientStorage(x, y, "Beef"); 
                break;
            case 'L': // L = Lettuce diganti Shrimp atau Fish
            case 'F': 
                gp.station[index] = new IngredientStorage(x, y, "Fish"); 
                break;
            case 'H': // H = Shrimp (Udang)
                gp.station[index] = new IngredientStorage(x, y, "Shrimp"); 
                break;

            // --- TEMBOK ---
            case 'X': 
                gp.station[index] = new Station(x, y, "Wall", "X") {
                    @Override public void interact(Chef c) {} 
                };
                break;
            
            default: gp.station[index] = null; break;
        }
    }

    public void draw(Graphics2D g2) {
        if (mapLayout == null) return;
        int tileSize = gp.tileSize;
        for (int row = 0; row < mapLayout.size(); row++) {
            for (int col = 0; col < mapLayout.get(row).size(); col++) {
                if ((col + row) % 2 == 0) g2.setColor(new Color(100, 100, 100)); 
                else g2.setColor(new Color(80, 80, 80)); 
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                g2.setColor(Color.BLACK);
                g2.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }
}