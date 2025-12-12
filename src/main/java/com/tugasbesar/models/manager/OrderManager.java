package com.tugasbesar.models.manager;

import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Dish;

import com.tugasbesar.core.AssetManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderManager {

    private static OrderManager instance;
    private List<Recipe> recipes;
    private List<Order> activeOrders;
    private final List<OrderNotification> notifications;

    private int spawnTimer = 0;
    private int score = 0;
    private static final int BASE_SCORE = 20;
    private static final int COMBO_STEP = 3;
    private static final int MAX_COMBO_MULTIPLIER = 4;

    private int streak = 0;
    private int comboMultiplier = 1;

    private OrderManager() {
        recipes = new ArrayList<>();
        activeOrders = new ArrayList<>();
        notifications = new ArrayList<>();
        initRecipes();
    }

    public static OrderManager getInstance() {
        if (instance == null)
            instance = new OrderManager();
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
        switch (name) {
            case "Pasta":
                ing = IngredientFactory.createPasta();
                break;
            case "Tomato":
                ing = IngredientFactory.createTomato();
                break;
            case "Beef":
                ing = IngredientFactory.createBeef();
                break;
            case "Shrimp":
                ing = IngredientFactory.createShrimp();
                break;
            case "Fish":
                ing = IngredientFactory.createFish();
                break;
        }
        if (ing != null) {
            ing.setState(state);
            list.add(ing);
        }
    }

    public Recipe findMatchingRecipe(List<String> ingredientNamesInput) {
        for (Recipe recipe : recipes) {
            List<String> recipeIngredients = recipe.getIngredientNames();
            if (ingredientNamesInput.size() != recipeIngredients.size())
                continue;
            boolean match = true;
            List<String> checklist = new ArrayList<>(recipeIngredients);
            for (String inputName : ingredientNamesInput) {
                boolean found = false;
                for (int i = 0; i < checklist.size(); i++) {
                    if (checklist.get(i).equalsIgnoreCase(inputName)) {
                        checklist.remove(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    match = false;
                    break;
                }
            }
            if (match)
                return recipe;
        }
        return null;
    }

    public void update() {
        spawnTimer++;
        if (spawnTimer >= 600 && activeOrders.size() < 3) { // Maksimal 3 order
            spawnRandomOrder();
            spawnTimer = 0;
        }
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).update();
            if (activeOrders.get(i).isExpired()) {
                pushNotification(activeOrders.get(i).getRecipe(), NotificationType.EXPIRED);
                activeOrders.remove(i);
                score -= 10;
                resetCombo();
                i--;
            }
        }

        for (int i = notifications.size() - 1; i >= 0; i--) {
            OrderNotification notif = notifications.get(i);
            notif.tick();
            if (notif.isExpired()) {
                notifications.remove(i);
            }
        }
    }

    private void spawnRandomOrder() {
        if (recipes.isEmpty())
            return;
        Random rand = new Random();
        Recipe randomRecipe = recipes.get(rand.nextInt(recipes.size()));
        activeOrders.add(new Order(randomRecipe, 60));
        pushNotification(randomRecipe, NotificationType.SPAWNED);
    }

    public ScoreEvent checkDish(Plate plate) {
        if (plate == null || plate.getContents().isEmpty())
            return null;
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);
            if (order.getRecipe().matches(plate)) {
                Recipe recipe = order.getRecipe();
                activeOrders.remove(i);
                streak++;
                comboMultiplier = calculateMultiplier(streak);
                int awarded = BASE_SCORE * comboMultiplier;
                score += awarded;
                return new ScoreEvent(recipe, awarded, streak, comboMultiplier);
            }
        }
        return null;
    }

    public void registerServeFailure() {
        resetCombo();
    }

    // --- [FIX] METHOD RESET SCORE YANG HILANG ---
    public void resetScore() {
        this.score = 0;
        this.activeOrders.clear(); // Hapus semua order lama
        this.spawnTimer = 0;       // Reset timer spawn
        this.streak = 0;
        this.comboMultiplier = 1;
        this.notifications.clear();
        System.out.println("🔄 Score & Orders Reset!");
    }
    // ------------------------------------------

    public int getScore() {
        return score;
    }

    public int getStreak() {
        return streak;
    }

    public int getComboMultiplier() {
        return comboMultiplier;
    }

    private int calculateMultiplier(int currentStreak) {
        if (currentStreak <= 0) {
            return 1;
        }
        int step = currentStreak / COMBO_STEP;
        step = Math.min(step, MAX_COMBO_MULTIPLIER - 1);
        return Math.max(1, 1 + step);
    }

    private void resetCombo() {
        streak = 0;
        comboMultiplier = 1;
    }

    // --- VISUALISASI UI ---
    public void draw(Graphics2D g2, int x, int y) {

        Color originalColor = g2.getColor();
        Font originalFont = g2.getFont();

        int comboOffset = 0;
        if (streak >= 2) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Combo x" + comboMultiplier + " (Streak " + streak + ")", x, y);
            comboOffset = 18;
        }

        int notificationsY = y + comboOffset;
        drawNotifications(g2, x, notificationsY);
        int orderOffsetY = notificationsY + (notifications.size() * 70) + 10;

        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);
            int yPos = orderOffsetY + (i * 60);

            // 1. Kotak Background
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(x, yPos, 200, 50, 10, 10);

            // 2. Timer Bar
            float pct = (float) order.getDuration() / order.getMaxDuration();
            g2.setColor(pct > 0.5 ? Color.GREEN : Color.RED);
            g2.fillRect(x + 10, yPos + 40, (int) (180 * pct), 5);

            // 3. Nama Resep
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(order.getRecipe().getRecipeName(), x + 10, yPos + 18);

            // 4. List Bahan (Kuning Kecil)
            g2.setFont(new Font("Arial", Font.ITALIC, 11));
            g2.setColor(Color.YELLOW);

            String ingredientsText = "";
            List<String> rawIngs = order.getRecipe().getIngredientNames();

            for (int j = 0; j < rawIngs.size(); j++) {
                ingredientsText += rawIngs.get(j);
                if (j < rawIngs.size() - 1)
                    ingredientsText += " + ";
            }

            g2.drawString(ingredientsText, x + 10, yPos + 32);
        }
        g2.setFont(originalFont);
        g2.setColor(originalColor);
    }

    private void drawNotifications(Graphics2D g2, int x, int y) {
        int offset = 0;
        for (OrderNotification notif : notifications) {
            int cardX = x;
            int cardY = y + offset;
            notif.draw(g2, cardX, cardY);
            offset += 70;
        }
    }

    private void pushNotification(Recipe recipe, NotificationType type) {
        notifications.add(new OrderNotification(recipe, type));
    }

    public static class ScoreEvent {
        private final Recipe recipe;
        private final int pointsAwarded;
        private final int streak;
        private final int multiplier;
        private final Dish dishItem;

        private ScoreEvent(Recipe recipe, int pointsAwarded, int streak, int multiplier) {
            this.recipe = recipe;
            this.pointsAwarded = pointsAwarded;
            this.streak = streak;
            this.multiplier = multiplier;
            this.dishItem = new Dish(recipe.getRecipeName());
        }

        public int getPointsAwarded() { return pointsAwarded; }
        public int getStreak() { return streak; }
        public int getMultiplier() { return multiplier; }
        public Dish getDishItem() { return dishItem; }
        public String getRecipeName() { return recipe.getRecipeName(); }

        public String getToastMessage() {
            String message = "+" + pointsAwarded + " pts";
            if (multiplier > 1) {
                message += " x" + multiplier;
            } else if (streak > 1) {
                message += " (Streak " + streak + ")";
            }
            return message;
        }

        public String getComboDetail() {
            if (multiplier <= 1) {
                return "Streak " + streak;
            }
            return "Combo x" + multiplier + " | Streak " + streak;
        }
    }

    private enum NotificationType {
        SPAWNED(Color.GREEN, "New Order"),
        EXPIRED(Color.RED, "Order Failed");

        private final Color accent;
        private final String label;

        NotificationType(Color accent, String label) {
            this.accent = accent;
            this.label = label;
        }
    }

    private static class OrderNotification {
        private static final int LIFETIME = 180;
        private final Recipe recipe;
        private final NotificationType type;
        private int timer;
        private final BufferedImage icon;

        OrderNotification(Recipe recipe, NotificationType type) {
            this.recipe = recipe;
            this.type = type;
            this.timer = LIFETIME;
            this.icon = AssetManager.getInstance().getItemIcon(new Dish(recipe.getRecipeName()));
        }

        void tick() {
            if (timer > 0) timer--;
        }

        boolean isExpired() {
            return timer <= 0;
        }

        void draw(Graphics2D g2, int x, int y) {
            float alpha = Math.min(1f, timer / (float) LIFETIME);
            Color accent = type.accent;

            java.awt.Composite originalComposite = g2.getComposite();
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));

            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRoundRect(x, y, 220, 60, 12, 12);

            g2.setColor(accent);
            g2.drawRoundRect(x, y, 220, 60, 12, 12);
            g2.fillRoundRect(x + 6, y + 6, 6, 48, 6, 6);

            float pct = timer / (float) LIFETIME;
            int barWidth = 180;
            int barHeight = 6;
            int barX = x + 30;
            int barY = y + 46;
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
            g2.setColor(accent);
            g2.fillRoundRect(barX, barY, Math.max(0, (int) (barWidth * pct)), barHeight, 4, 4);

            int iconSize = 40;
            if (icon != null) {
                g2.drawImage(icon, x + 20, y + 10, iconSize, iconSize, null);
            } else {
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(x + 20, y + 12, iconSize - 4, iconSize - 4);
                g2.setColor(Color.WHITE);
                g2.drawString("?", x + 36, y + 38);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(type.label, x + 70, y + 20);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(recipe.getRecipeName(), x + 70, y + 38);

            g2.setComposite(originalComposite);
        }
    }
}