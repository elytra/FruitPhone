package com.elytradev.fruitphone;

import java.util.Locale;

public enum Gravity {
	NORTH_WEST(
			0f, 1f, 0f,
			0f, 1f, 0f
		),
	NORTH(
			0.5f, 0f, -0.5f,
			0f, 1f, 0f
		),
	NORTH_EAST(
			1f, -1f, -1f,
			0f, 1f, 0f
		),
	WEST(
			0f, 1f, 0f,
			0.5f, 0f, -0.5f
		),
	CENTER(
			0.5f, 0f, -0.5f,
			0.5f, 0f, -0.5f
		),
	EAST(
			1f, -1f, -1f,
			0.5f, 0f, -0.5f
		),
	SOUTH_WEST(
			0f, 1f, 0f,
			1f, -1f, -1f
		),
	SOUTH(
			0.5f, 0f, -0.5f,
			1f, -1f, -1f
		),
	SOUTH_EAST(
			1f, -1f, -1f,
			1f, -1f, -1f
		),
	;
	
	
	private final String lower;
	
	private final float xBase;
	private final float xMult;
	private final float xObjMult;
	private final float yBase;
	private final float yMult;
	private final float yObjMult;
	
	private Gravity(float xBase, float xMult, float xObjMult, float yBase, float yMult, float yObjMult) {
		this.lower = name().toLowerCase(Locale.ROOT);
		
		this.xBase = xBase;
		this.xMult = xMult;
		this.xObjMult = xObjMult;
		
		this.yBase = yBase;
		this.yMult = yMult;
		this.yObjMult = yObjMult;
	}
	
	public Gravity opposite() {
		switch (this) {
			case CENTER: return CENTER;
			case EAST: return WEST;
			case NORTH: return SOUTH;
			case NORTH_EAST: return SOUTH_WEST;
			case NORTH_WEST: return SOUTH_EAST;
			case SOUTH: return NORTH;
			case SOUTH_EAST: return NORTH_WEST;
			case SOUTH_WEST: return NORTH_EAST;
			case WEST: return EAST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity flipHorizontal() {
		switch (this) {
			case CENTER: return CENTER;
			case EAST: return WEST;
			case NORTH: return NORTH;
			case NORTH_EAST: return NORTH_WEST;
			case NORTH_WEST: return NORTH_EAST;
			case SOUTH: return SOUTH;
			case SOUTH_EAST: return SOUTH_WEST;
			case SOUTH_WEST: return SOUTH_EAST;
			case WEST: return EAST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity flipVertical() {
		switch (this) {
			case CENTER: return CENTER;
			case EAST: return EAST;
			case NORTH: return SOUTH;
			case NORTH_EAST: return SOUTH_EAST;
			case NORTH_WEST: return SOUTH_WEST;
			case SOUTH: return NORTH;
			case SOUTH_EAST: return NORTH_EAST;
			case SOUTH_WEST: return NORTH_WEST;
			case WEST: return WEST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public int resolveX(int xofs, int width, int objwidth) {
		return (int)(((width*xBase)+(xofs*xMult))+(objwidth*xObjMult));
	}
	
	public int resolveY(int yofs, int height, int objheight) {
		return (int)(((height*yBase)+(yofs*yMult))+(objheight*yObjMult));
	}
	
	@Override
	public String toString() {
		return lower;
	}
}
