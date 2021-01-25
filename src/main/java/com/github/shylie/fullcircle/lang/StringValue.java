package com.github.shylie.fullcircle.lang;

public class StringValue implements Value {
    public final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public Value add(Value other) {
        if (other instanceof StringValue) {
            return new StringValue(value + ((StringValue)other).value);
        }
        else {
            return null;
        }
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
        return new StringValue(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof StringValue) { return this.value.equals(((StringValue)obj).value); }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }
}
