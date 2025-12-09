package com.tugasbesar.models.abstracts;

import com.tugasbesar.models.interfaces.Processable; 

import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {

    protected List<Processable> contents; 

    public KitchenUtensil(String name) {
        super(name);
        this.contents = new ArrayList<>();
    }
    
    public List<Processable> getContents() {
        return contents;
    }

    public void addIngredient(Processable item) {
        this.contents.add(item);
    }
    
    public void clearContents() {
        this.contents.clear();
    }
    
    public boolean isEmpty() {
        return contents.isEmpty();
    }
    

    public Item takeItem() {
        if (contents.isEmpty()) {
            System.out.println("[Utensil] Wadah " + getName() + " kosong, tidak ada yang bisa diambil.");
            return null;
        }
        
  
        Item taken = (Item) contents.remove(0); 
        return taken;
    }

    @Override
    public String toString() {
        if (isEmpty()) return getName() + " (Empty)";
        return getName() + " [" + contents.size() + " items]";
    }
    
    
    public abstract boolean canAccept(Processable item);
}