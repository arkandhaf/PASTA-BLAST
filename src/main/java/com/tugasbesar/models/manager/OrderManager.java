package com.tugasbesar.models.manager;

import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.enums.IngredientState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderManager {
    
    private static OrderManager instance;
    private List<Recipe> recipes; 
    private List<Order> activeOrders; 
    
    private int spawnTimer = 0;
    private int score = 0;

    private OrderManager() {
        recipes = new ArrayList<>();
        activeOrders = new ArrayList<>();
        initRecipes();
    }

    public static OrderManager getInstance() {
        if (instance == null) instance = new OrderManager();
        return instance;
    }

    private void initRecipes() {
        // 1. Pasta Marinara
        List<Ingredient> marinaraReq = new ArrayList<>();
        addReq(marinaraReq, "Pasta", IngredientState.COOKED);
        addReq(marinaraReq, "Tomato", IngredientState.COOKED);
        recipes.add(new Recipe("Pasta Marinara", marinaraReq));

        // 2. Pasta Bolognese
        List<Ingredient> bologneseReq = new ArrayList<>();
        addReq(bologneseReq, "Pasta", IngredientState.COOKED);
        addReq(bologneseReq, "Beef", IngredientState.COOKED);
        recipes.add(new Recipe("Pasta Bolognese", bologneseReq));

        // 3. Frutti di Mare
        List<Ingredient> fruttiReq = new ArrayList<>();
        addReq(fruttiReq, "Pasta", IngredientState.COOKED);
        addReq(fruttiReq, "Shrimp", IngredientState.COOKED);
        addReq(fruttiReq, "Fish", IngredientState.COOKED);
        recipes.add(new Recipe("Frutti di Mare", fruttiReq));
    }

    private void addReq(List<Ingredient> list, String name, IngredientState state) {
        Ingredient ing = null;
        switch(name) {
            case "Pasta": ing = IngredientFactory.createPasta(); break;
            case "Tomato": ing = IngredientFactory.createTomato(); break;
            case "Beef": ing = IngredientFactory.createBeef(); break;
            case "Shrimp": ing = IngredientFactory.createShrimp(); break;
            case "Fish": ing = IngredientFactory.createFish(); break;
        }
        if(ing != null) { ing.setState(state); list.add(ing); }
    }

    public Recipe findMatchingRecipe(List<String> ingredientNamesInput) {
        for (Recipe recipe : recipes) {
            List<String> recipeIngredients = recipe.getIngredientNames();
            if (ingredientNamesInput.size() != recipeIngredients.size()) continue;
            boolean match = true;
            List<String> checklist = new ArrayList<>(recipeIngredients);
            for(String inputName : ingredientNamesInput) {
                boolean found = false;
                for(int i=0; i<checklist.size(); i++) {
                    if(checklist.get(i).equalsIgnoreCase(inputName)) {
                        checklist.remove(i);
                        found = true;
                        break;
                    }
                }
                if(!found) { match = false; break; }
            }
            if (match) return recipe;
        }
        return null;
    }

    public void update() {
        spawnTimer++;
        if (spawnTimer >= 600 && activeOrders.size() < 3) { // Maksimal 3 order biar gak penuh layarnya
            spawnRandomOrder();
            spawnTimer = 0;
        }
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).update();
            if (activeOrders.get(i).isExpired()) {
                activeOrders.remove(i);
                score -= 10; 
                i--;
            }
        }
    }

    private void spawnRandomOrder() {
        if (recipes.isEmpty()) return;
        Random rand = new Random();
        Recipe randomRecipe = recipes.get(rand.nextInt(recipes.size()));
        activeOrders.add(new Order(randomRecipe, 60)); 
    }

    public boolean checkDish(Plate plate) {
        if (plate == null || plate.getContents().isEmpty()) return false;
        for (int i = 0; i < activeOrders.size(); i++) {
            if (activeOrders.get(i).getRecipe().matches(plate)) {
                activeOrders.remove(i);
                score += 20; 
                return true;
            }
        }
        return false;
    }

    // --- [UPDATE] VISUALISASI BAHAN DI DALAM ORDER ---
    public void draw(Graphics2D g2, int x, int y) {
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("SCORE: " + score, x, y);
        
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);
            int yPos = y + 20 + (i * 60); // Jarak antar kotak agak lebaran dikit

            // 1. Kotak Background (Hitam Transparan)
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(x, yPos, 200, 50, 10, 10);

            // 2. Timer Bar
            float pct = (float) order.getDuration() / order.getMaxDuration();
            g2.setColor(pct > 0.5 ? Color.GREEN : Color.RED);
            g2.fillRect(x + 10, yPos + 40, (int)(180 * pct), 5);

            // 3. Nama Resep (Besar)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(order.getRecipe().getRecipeName(), x + 10, yPos + 18);
            
            // 4. [BARU] List Bahan (Kecil di bawah nama)
            g2.setFont(new Font("Arial", Font.ITALIC, 11)); // Font miring kecil
            g2.setColor(Color.YELLOW); // Warna kuning biar jelas
            
            String ingredientsText = "";
            List<String> rawIngs = order.getRecipe().getIngredientNames();
            
            // Gabungkan nama bahan jadi satu string "Pasta + Beef"
            for(int j=0; j<rawIngs.size(); j++) {
                ingredientsText += rawIngs.get(j);
                if(j < rawIngs.size()-1) ingredientsText += " + ";
            }
            
            g2.drawString(ingredientsText, x + 10, yPos + 32);
        }
    }
}