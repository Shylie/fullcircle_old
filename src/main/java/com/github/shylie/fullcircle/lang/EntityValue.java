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
    public Value cmp(Value other) {
        if (equals(other)) {
            return new LongValue(0);
        }
        else {
            return new LongValue(1);
        }
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
    public boolean equals(Object obj) {
        if (obj instanceof EntityValue) {
            return entityID == ((EntityValue)obj).entityID;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "e: " + Integer.toString(entityID);
    }
}