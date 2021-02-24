package com.github.shylie.fullcircle.lang;

public interface Value {
	public Value add(Value other);
	public Value sub(Value other);
	public Value mul(Value other);
	public Value div(Value other);
	default public Value cmp(Value other)
	{
		if (equals(other)) {
			return new LongValue(0);
		}
		else {
			return new LongValue(1);
		}
	}
	public Value neg();
	public Value dup();
}