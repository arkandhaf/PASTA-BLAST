package com.tugasbesar.models.manager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tugasbesar.core.GamePanel;
import com.tugasbesar.models.stations.*; // Import semua Station

public class MapParser {
    
    GamePanel gp;
    public List<List<Character>> mapLayout; // Menyimpan denah huruf

    public MapParser(GamePanel gp) {
        this.gp = gp;
        this.mapLayout = new ArrayList<>();
    }

    /** * Load map, baca file, dan spawn station ke GamePanel
     */
    public void loadMap(String filePath) {
        try {
            // 1. BACA FILE (Pake InputStream biar aman di dalam folder resources)
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.out.println("❌ ERROR: Map file not found at " + filePath);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            int stationIndex = 0;

            while ((line = br.readLine()) != null) {
                List<Character> mapRow = new ArrayList<>();
                char[] chars = line.toCharArray();
                
                for (int col = 0; col < chars.length; col++) {
                    char c = chars[col];
                    mapRow.add(c);

                    // 2. SPAWN OBJECT BERDASARKAN HURUF
                    // Pastikan array station di GamePanel cukup besar!
                    if (stationIndex < gp.station.length) {
                        createStation(c, col, row, stationIndex);
                        
                        // Kalau berhasil buat station (bukan lantai doang), index nambah
                        if (gp.station[stationIndex] != null) {
                            stationIndex++;
                        }
                    }
                }
                mapLayout.add(mapRow);
                row++;
            }
            br.close();
            System.out.println("✅ Map Loaded! Total Stations: " + stationIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Factory Method: Ubah Huruf jadi Object
    private void createStation(char c, int x, int y, int index) {
        switch (c) {
            // S = Stove (CookingStation)
            case 'S':
                gp.station[index] = new CookingStation(x, y);
                break;
            
            // C = CuttingStation
            case 'C':
                gp.station[index] = new CuttingStation(x, y);
                break;
            
            // D = Dispenser (Contoh: Ambil Bahan)
            // case 'D':
            //    gp.station[index] = new Dispenser(x, y, "Tomato"); 
            //    break;

            // A = Assembly / Plate
            case 'A':
                // Pastikan ada class AssemblyStation
                gp.station[index] = new AssemblyStation(x, y); 
                break;
            
            // V = Serving Station
            case 'V':
                // Kita panggil constructor sederhana tanpa storage
                gp.station[index] = new ServingStation(x, y); 
                break;

            // X = Counter Kosong (Meja biasa)
            // case 'X':
            //    gp.station[index] = new CounterStation(x, y);
            //    break;
                
            // ' ' (Spasi) atau '.' = Lantai Kosong (Jangan bikin station)
            default:
                gp.station[index] = null;
                break;
        }
    }

    // Method Gambar Lantai (Background)
    public void draw(Graphics2D g2) {
        if (mapLayout == null) return;

        int tileSize = gp.tileSize;

        for (int row = 0; row < mapLayout.size(); row++) {
            for (int col = 0; col < mapLayout.get(row).size(); col++) {
                
                // Gambar Lantai (Catur Sederhana biar kelihatan gridnya)
                if ((col + row) % 2 == 0) {
                    g2.setColor(new Color(200, 200, 200)); // Abu terang
                } else {
                    g2.setColor(new Color(180, 180, 180)); // Abu gelap
                }
                
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }
}