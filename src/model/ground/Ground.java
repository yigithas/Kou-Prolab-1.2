package model.ground;

import engine.Grid;
import model.robot.Robot;

public abstract class Ground {

	private final char symbol;

	public Ground(char symbol) {
		this.symbol = symbol;
	}

	public char getSymbol() {
		return symbol;
	}

	public boolean canLandOn() {
		return true;
	}

	public boolean isDeliveryRamp() {
		return false;
	}

	public boolean isSlippery() {
		return false;
	}

	public abstract boolean stepOn(Robot robot, Grid grid);

}
