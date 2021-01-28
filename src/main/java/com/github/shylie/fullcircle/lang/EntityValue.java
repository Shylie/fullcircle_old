package com.github.shylie.fullcircle.lang;

public class EntityValue implements Value {

    public final int entityID;

    public EntityValue(int entityID) {
        this.entityID = entityID;
    }

    @Override
    public Value add(Value other) {
        return null;
    }

    @Override
    public Value sub(Value other) {
        return null;
    }

    @Override
    public Value mul(Value other) {
        return null;
    }

    @Override
    public Value div(Value other) {
        return null;
    }

    @Override
    public Value neg() {
        return null;
    }

    @Override
    public Value dup() {
        return new EntityValue(entityID);
    }

    @Override
    public String toString() {
        return "e: " + Integer.toString(entityID);
    }
}