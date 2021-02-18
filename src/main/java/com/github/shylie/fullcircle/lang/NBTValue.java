package com.github.shylie.fullcircle.lang;

import net.minecraft.nbt.CompoundNBT;

public class NBTValue implements Value {
	public final CompoundNBT value;

	public NBTValue(CompoundNBT value) {
		this.value = value.copy(); // init with copy to prevent duplicate references
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
		return new NBTValue(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (obj instanceof NBTValue) {
			return value.equals(((NBTValue)obj).value);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
