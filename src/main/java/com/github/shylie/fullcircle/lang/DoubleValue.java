package com.github.shylie.fullcircle.lang;

public class DoubleValue implements Value {
    public final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public Value add(Value other) {
        if (other instanceof DoubleValue) {
            return new DoubleValue(value + ((DoubleValue)other).value);
        }
        else if (other instanceof LongValue) {
            return new DoubleValue(value + ((LongValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value sub(Value other) {
        if (other instanceof DoubleValue) {
            return new DoubleValue(value - ((DoubleValue)other).value);
        }
        else if (other instanceof LongValue) {
            return new DoubleValue(value - ((LongValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value mul(Value other) {
        if (other instanceof DoubleValue) {
            return new DoubleValue(value * ((DoubleValue)other).value);
        }
        else if (other instanceof LongValue) {
            return new DoubleValue(value * ((LongValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value div(Value other) {
        if (other instanceof DoubleValue) {
            return new DoubleValue(value / ((DoubleValue)other).value);
        }
        else if (other instanceof LongValue) {
            return new DoubleValue(value / ((LongValue)other).value);
        }
        else {
            return null;
        }
    }

    @Override
    public Value neg() {
        return new DoubleValue(-value);
    }

    @Override
    public Value dup() {
        return new DoubleValue(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof DoubleValue) {
            return value == ((DoubleValue)obj).value;
        }
        else if (obj instanceof LongValue) {
            return value == ((LongValue)obj).value;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}