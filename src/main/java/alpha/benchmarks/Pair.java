package alpha.benchmarks;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class Pair<Left, Right> {
	Left left;
	Right right;

	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}

	public Left getKey() {
		return left;
	}

	public Right getValue() {
		return right;
	}
}
