package com.tugasbesar.models.interfaces;

public interface Cookable extends Processable {
    boolean canBeCooked();
    void cook();
}