package net.lintfordlib.core.maths;

import java.io.Serializable;

public class Vector2f implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2295169699757910995L;

	private static final Vector2f _tmp = new Vector2f();

	public static final Vector2f Zero = new Vector2f();
	public static final Vector2f One = new Vector2f(1, 1);

	public static final Vector2f Up = new Vector2f(0, -1);
	public static final Vector2f Right = new Vector2f(1, 0);

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** the x-component of this vector **/
	public float x;

	/** the y-component of this vector **/
	public float y;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	/**
	 * Constructs a new vector at (0,0)
	 */
	public Vector2f() {

	}

	/**
	 * Constructs a vector with the given components
	 * 
	 * @param x The x-component
	 * @param y The y-component
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a vector from the given vector
	 * 
	 * @param v The vector
	 */
	public Vector2f(Vector2f v) {
		set(v);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * @return a copy of this vector
	 */
	public Vector2f cpy() {
		return new Vector2f(this);
	}

	/**
	 * @return The euclidian length
	 */
	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float len2() {
		return x * x + y * y;
	}

	public Vector2f div(float pX, float pY) {
		if (pX == 0 || pY == 0)
			return this;
		x /= pX;
		y /= pY;
		return this;
	}

	/**
	 * Sets this vector from the given vector
	 * 
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2f set(Vector2f v) {
		if (v == null) {
			x = 0;
			y = 0;
			return this;

		}

		x = v.x;
		y = v.y;

		return this;

	}

	/**
	 * Sets the components of this vector
	 * 
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining
	 */
	public Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Substracts the given vector from this vector.
	 * 
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2f sub(Vector2f v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/**
	 * Normalizes this vector
	 * 
	 * @return This vector for chaining
	 */
	public Vector2f nor() {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	/**
	 * Normalizes this vector
	 * 
	 * @return The magnitude of the vector before normalization
	 */
	public float norAndRetLen() {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return len;
	}

	/**
	 * Adds the given vector to this vector
	 * 
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2f add(Vector2f v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/**
	 * Adds the given components to this vector
	 * 
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining
	 */
	public Vector2f add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * 
	 * @param degrees the angle in degrees
	 */
	public Vector2f rotate(float degrees) {
		return rotateRad((float) Math.toRadians(degrees));
	}

	/**
	 * Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * 
	 * @param radians the angle in radians
	 */
	public Vector2f rotateRad(float radians) {
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	public Vector2f rotateRad(float ox, float oy, float radians) {
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		final float fx = x - ox;
		final float fy = y - oy;

		float newX = fx * cos - fy * sin;
		float newY = fx * sin + fy * cos;

		this.x = newX + ox;
		this.y = newY + oy;

		return this;
	}

	/** Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
	public Vector2f rotate90(int dir) {
		final var lx = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = lx;
		} else {
			this.x = y;
			y = -lx;
		}
		return this;
	}

	/**
	 * @param v The other vector
	 * @return The dot product between this and the other vector
	 */
	public float dot(Vector2f v) {
		return x * v.x + y * v.y;
	}

	public float dot(float vx, float vy) {
		return x * vx + y * vy;
	}

	/**
	 * Multiplies this vector by a scalar
	 * 
	 * @param scalar The scalar
	 * @return This vector for chaining
	 */
	public Vector2f mul(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Vector2f mul(Vector2f o) {
		x *= o.x;
		y *= o.y;
		return this;
	}

	/**
	 * Multiples this {@link Vector2f} by the given rotation
	 */
	public Vector2f mul(Rotation r) {
		final var xx = x;
		final var yy = y;

		x = r.c * xx - r.s * yy;
		y = r.s * xx + r.c * yy;
		return this;
	}

	/**
	 * @param v The other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(Vector2f v) {
		float xx = v.x - x;
		float yy = v.y - y;
		return (float) Math.sqrt(xx * xx + yy * yy);
	}

	/**
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(float x, float y) {
		float xx = x - this.x;
		float yy = y - this.y;
		return (float) Math.sqrt(xx * xx + yy * yy);
	}

	/**
	 * @param v The other vector
	 * @return the squared distance between this and the other vector
	 */
	public float dst2(Vector2f v) {
		float xx = v.x - x;
		float yy = v.y - y;
		return xx * xx + yy * yy;
	}

	/** Multiplies this vector by a scalar */
	public Vector2f scale(float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	/**
	 * Substracts the other vector from this vector.
	 * 
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return This vector for chaining
	 */
	public Vector2f sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * @return a temporary copy of this vector. Use with care as this is backed by a single static Vector2 instance. v1.tmp().add( v2.tmp() ) will not work!
	 */
	public Vector2f tmp() {
		return _tmp.set(this);
	}

	/**
	 * @param v the other vector
	 * @return The cross product between this and the other vector
	 */
	public float cross(final Vector2f v) {
		return this.x * v.y - v.x * this.y;
	}

	/**
	 * @return The manhattan length
	 */
	public float lenManhattan() {
		return Math.abs(this.x) + Math.abs(this.y);
	}

	public boolean vectorsEqual(Vector2f other) {
		if (other == null)
			return false;

		return x == other.x && y == other.y;
	}

	public Vector2f reflected(Vector2f normal) {
		return normal.mul(-2 * this.dot(normal)).add(this);
	}

	@Override
	public String toString() {
		return "[" + x + ":" + y + "]";
	}

	// --------------------------------------
	// Class-Methods
	// --------------------------------------

	public static float dot(Vector2f v0, Vector2f v1) {
		return v0.x * v1.x + v0.y * v1.y;
	}

	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	/** Returns the magnitude of the vector that *would* result from a 3d cross product of the given *2d* vetors (taking the Z values as implicitly 0) */
	public static float cross(float x1, float y1, float x2, float y2) {
		return x1 * y2 - y1 * x2;
	}

	public static float dst(Vector2f x, Vector2f y) {
		return dst(x.x, x.y, y.x, y.y);

	}

	public static float dst(float pX1, float pY1, float pX2, float pY2) {
		return (float) Math.sqrt(dst2(pX1, pY1, pX2, pY2));
	}

	public static float dst2(float x, float y) {
		return (x * x) + (y * y);
	}

	public static float dst2(float pX1, float pY1, float pX2, float pY2) {
		float x = pX1 - pX2;
		float y = pY1 - pY2;
		return (x * x) + (y * y);
	}

	public static Vector2f sub(Vector2f v0, Vector2f v1) {
		float x = v0.x - v1.x;
		float y = v0.y - v1.y;
		return new Vector2f(x, y);
	}

	public static Vector2f cpy(Vector2f x) {
		return new Vector2f(x);
	}

	/**
	 * Multiples this {@link Vector2f} by the given rotation
	 */
	public static void mul(Vector2f out, Vector2f pos, Rotation r) {
		out.set(r.c * pos.x - r.s, r.s * pos.y + r.c);
	}

	public static Vector2f scale(Vector2f v, float scalar) {
		float x = v.x * scalar;
		float y = v.y * scalar;
		return new Vector2f(x, y);
	}
}
