package com.github.shylie.fullcircle.lang;

public class LongValue implements Value {
    public final long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public Value add(Value other) {
        if (other instanceof LongValue) {
            return new LongValue(value + ((LongValue)other).value);
        }
        else if (other instanceof DoubleValue) {
            return new DoubleValue(value + ((DoubleValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value sub(Value other) {
        if (other instanceof LongValue) {
            return new LongValue(value - ((LongValue)other).value);
        }
        else if (other instanceof DoubleValue) {
            return new DoubleValue(value - ((DoubleValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value mul(Value other) {
        if (other instanceof LongValue) {
            return new LongValue(value * ((LongValue)other).value);
        }
        else if (other instanceof DoubleValue) {
            return new DoubleValue(value * ((DoubleValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value div(Value other) {
        if (other instanceof LongValue) {
            return new LongValue(value / ((LongValue)other).value);
        }
        else if (other instanceof DoubleValue) {
            return new DoubleValue(value / ((DoubleValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value neg() {
        return new LongValue(-value);
    }

    @Override
    public Value dup() {
        return new LongValue(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof LongValue) {
            return value == ((LongValue)obj).value;
        }
        else if (obj instanceof DoubleValue) {
            return value == ((DoubleValue)obj).value;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}