package com.github.shylie.fullcircle.lang;

public enum Direction {
	RIGHT,
	UP,
	LEFT,
	DOWN;

	public static Direction RotateLeft(Direction direction) {
		switch (direction) {
			case UP:
				return Direction.RIGHT;

			case LEFT:
				return Direction.UP;

			case DOWN:
				return Direction.LEFT;

			default:
				return Direction.DOWN;
		}
	}

	public static Direction RotateRight(Direction direction) {
		switch (direction) {
			case UP:
				return Direction.LEFT;

			case LEFT:
				return Direction.DOWN;

			case DOWN:
				return Direction.RIGHT;

			default:
				return Direction.UP;
		}
	}
}