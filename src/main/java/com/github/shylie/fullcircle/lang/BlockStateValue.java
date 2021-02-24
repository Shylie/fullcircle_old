package com.github.shylie.fullcircle.lang;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

public class BlockStateValue implements Value {
	private BlockState state;

	public BlockStateValue(BlockState state) {
		this.state = state;
	}

	public BlockState getState() {
		return state;
	}

	public Optional<Long> getPropertyAsLong(String propertyName) {
		return getPropertyAsString(propertyName).map(
			(string) -> {
				try {
					return Long.valueOf(string);
				}
				catch (NumberFormatException nfe) {
					return null;
				}
			}
		);
	}

	public Optional<String> getPropertyAsString(String propertyName) {
		Property<?> property = getProperty(propertyName);
		if (property == null) {
			return Optional.empty();
		}
		else {
			return Optional.of(state.get(property).toString());
		}
	}

	public boolean modifyProperty(String propertyName, String newValue) {
		return modifyProperty(getProperty(propertyName), newValue);
	}

	private <T extends Comparable<T>> boolean modifyProperty(Property<T> property, String newValue) {
		if (property == null) { return false; }

		Optional<T> optional = property.parseValue(newValue);
		if (optional.isPresent()) {
			state = state.with(property, optional.get());
			return true;
		}
		else {
			return false;
		}
	}

	private Property<?> getProperty(String propertyName) {
		for (Property<?> temp : state.getProperties()) {
			if (temp.getName().equals(propertyName)) {
				return temp;
			}
		}

		return null;
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
		return new BlockStateValue(state);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (obj instanceof BlockStateValue) {
			return state.equals(((BlockStateValue)obj).state);
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return state.toString();
	}
}
