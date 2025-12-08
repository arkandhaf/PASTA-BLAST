package com.tugasbesar.models.abstracts;

import com.tugasbesar.models.interfaces.Preparable;
import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {

    // PAKAI LIST: Biar urut dan bisa duplikasi (misal 2 daging)
    protected List<Preparable> contents;

    public KitchenUtensil(String name) {
        super(name);
        this.contents = new ArrayList<>();
    }
    
    // --- METHOD MILIK BERSAMA (Plate & Panci) ---

    public List<Preparable> getContents() {
        return contents;
    }

    public void addIngredient(Preparable item) {
        this.contents.add(item);
    }
    
    public void clearContents() {
        this.contents.clear();
    }
    
    public boolean isEmpty() {
        return contents.isEmpty();
    }
    
    // Helper buat ngintip item paling atas/terakhir (mirip Stack)
    public Preparable peek() {
        if (isEmpty()) return null;
        return contents.get(contents.size() - 1);
    }

    @Override
    public String toString() {
        if (isEmpty()) return getName() + " (Empty)";
        return getName() + " [" + contents.size() + " items]";
    }
}