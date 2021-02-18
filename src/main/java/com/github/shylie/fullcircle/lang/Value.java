package com.github.shylie.fullcircle.lang;

public interface Value {
	public Value add(Value other);
	public Value sub(Value other);
	public Value mul(Value other);
	public Value div(Value other);
	public Value cmp(Value other);
	public Value neg();
	public Value dup();
}