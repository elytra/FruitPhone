/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 William Thompson (unascribed)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
	
	public boolean isCorner() {
		switch (this) {
			case CENTER:
			case EAST:
			case NORTH:
			case WEST:
			case SOUTH:
				return false;
			case NORTH_EAST:
			case NORTH_WEST:
			case SOUTH_EAST:
			case SOUTH_WEST:
				return true;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity getHorizontalCenter() {
		switch (this) {
			case CENTER: return CENTER;
			case EAST: return CENTER;
			case NORTH: return NORTH;
			case NORTH_EAST: return NORTH;
			case NORTH_WEST: return NORTH;
			case SOUTH: return SOUTH;
			case SOUTH_EAST: return SOUTH;
			case SOUTH_WEST: return SOUTH;
			case WEST: return CENTER;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public boolean isHorizontalCenter() {
		return getHorizontalCenter() == this;
	}
	
	public Gravity getVerticalCenter() {
		switch (this) {
			case CENTER: return CENTER;
			case EAST: return EAST;
			case NORTH: return CENTER;
			case NORTH_EAST: return EAST;
			case NORTH_WEST: return WEST;
			case SOUTH: return CENTER;
			case SOUTH_EAST: return EAST;
			case SOUTH_WEST: return WEST;
			case WEST: return WEST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public boolean isVerticalCenter() {
		return getVerticalCenter() == this;
	}
	
	public Gravity westmost() {
		switch (this) {
			case CENTER: return WEST;
			case EAST: return WEST;
			case NORTH: return NORTH_WEST;
			case NORTH_EAST: return NORTH_WEST;
			case NORTH_WEST: return NORTH_WEST;
			case SOUTH: return SOUTH_WEST;
			case SOUTH_EAST: return SOUTH_WEST;
			case SOUTH_WEST: return SOUTH_WEST;
			case WEST: return WEST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity eastmost() {
		switch (this) {
			case CENTER: return EAST;
			case EAST: return EAST;
			case NORTH: return NORTH_EAST;
			case NORTH_EAST: return NORTH_EAST;
			case NORTH_WEST: return NORTH_EAST;
			case SOUTH: return SOUTH_EAST;
			case SOUTH_EAST: return SOUTH_EAST;
			case SOUTH_WEST: return SOUTH_EAST;
			case WEST: return EAST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity northmost() {
		switch (this) {
			case CENTER: return NORTH;
			case EAST: return NORTH_EAST;
			case NORTH: return NORTH;
			case NORTH_EAST: return NORTH_EAST;
			case NORTH_WEST: return NORTH_WEST;
			case SOUTH: return NORTH_WEST;
			case SOUTH_EAST: return NORTH_WEST;
			case SOUTH_WEST: return NORTH_WEST;
			case WEST: return NORTH_WEST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity southmost() {
		switch (this) {
			case CENTER: return SOUTH;
			case EAST: return SOUTH_EAST;
			case NORTH: return SOUTH_EAST;
			case NORTH_EAST: return SOUTH_EAST;
			case NORTH_WEST: return SOUTH_EAST;
			case SOUTH: return SOUTH_EAST;
			case SOUTH_EAST: return SOUTH_EAST;
			case SOUTH_WEST: return SOUTH_EAST;
			case WEST: return SOUTH_WEST;
		}
		throw new AssertionError("Missing case for "+this);
	}
	
	public Gravity opposite() {
		return flipHorizontal().flipVertical();
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
