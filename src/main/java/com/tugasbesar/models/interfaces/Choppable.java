package com.tugasbesar.models.interfaces;

public interface Choppable extends Processable {
    boolean canBeChopped();
    void chop();
}