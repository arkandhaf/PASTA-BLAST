package com.tugasbesar.models.manager;

public class Order {
    private Recipe recipe;
    private int duration; // Sisa waktu
    private int maxDuration; // Waktu total (untuk bar hijau)

    public Order(Recipe recipe, int durationInSeconds) {
        this.recipe = recipe;
        this.maxDuration = durationInSeconds * 60; // Asumsi 60 FPS
        this.duration = maxDuration;
    }

    public void update() {
        if (duration > 0) duration--;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public Recipe getRecipe() { return recipe; }
    public int getDuration() { return duration; }
    public int getMaxDuration() { return maxDuration; }
}