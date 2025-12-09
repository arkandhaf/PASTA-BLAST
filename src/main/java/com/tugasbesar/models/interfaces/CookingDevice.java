// File: com.tugasbesar.models.interfaces.CookingDevice.java

package com.tugasbesar.models.interfaces;

public interface CookingDevice {

    boolean isPortable();
    int capacity();
    

    boolean canAccept(Cookable ingredient);
    void addIngredient(Cookable ingredient);
    void startCooking();

    
    void processCookingTick(); 
    
    boolean isBurned();
    boolean isCooked();
    int getCookingPercentage();
}