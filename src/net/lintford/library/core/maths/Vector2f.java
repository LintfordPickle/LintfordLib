package net.lintford.library.core.maths;

import java.io.Serializable;

/**
 * Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself
 * 
 * @author badlogicgames@gmail.com
 * 
 */
public class Vector2f implements Serializable {

	private static final long serialVersionUID = -2295169699757910995L;

	private final static Vector2f tmp = new Vector2f();

	public final static Vector2f Zero = new Vector2f();
	public final static Vector2f One = new Vector2f();

	/** the x-component of this vector **/
	public float x;

	/** the y-component of this vector **/
	public float y;

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

	/**
	 * @return a copy of this vector
	 */
	public Vector2f cpy() {
		return new Vector2f(this);
	}

	public static Vector2f cpy(Vector2f x) {
		return new Vector2f(x);
	}

	/**
	 * @return The euclidian length
	 */
	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public static float len(Vector2f x, Vector2f y) {
		return (float) Math.sqrt((y.y - x.y) * (y.y - x.y) + (y.x - x.x) * (y.x - x.x));
	}

	public static float len(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public static Vector2f CatmullRom(Vector2f value1, Vector2f value2, Vector2f value3, Vector2f value4, float amount, Vector2f returnVector) {
		float num = amount * amount;
		float num2 = amount * num;
		returnVector.x = 0.5f * ((((2f * value2.x) + ((-value1.x + value3.x) * amount)) + (((((2f * value1.x) - (5f * value2.x)) + (4f * value3.x)) - value4.x) * num))
				+ ((((-value1.x + (3f * value2.x)) - (3f * value3.x)) + value4.x) * num2));
		returnVector.y = 0.5f * ((((2f * value2.y) + ((-value1.y + value3.y) * amount)) + (((((2f * value1.y) - (5f * value2.y)) + (4f * value3.y)) - value4.y) * num))
				+ ((((-value1.y + (3f * value2.y)) - (3f * value3.y)) + value4.y) * num2));
		return returnVector;
	}

	public static float distance(Vector2f value1, Vector2f value2) {
		float num2 = value1.x - value2.x;
		float num = value1.y - value2.y;
		float num3 = (num2 * num2) + (num * num);
		return (float) Math.sqrt(num3);
	}

	public static float distance2(float pX1, float pY1, float pX2, float pY2) {
		float num2 = pX1 - pX2;
		float num = pY1 - pY2;
		return (num2 * num2) + (num * num);
	}

	public static float distance(float pX1, float pY1, float pX2, float pY2) {
		return (float) Math.sqrt(distance2(pX1, pY1, pX2, pY2));

	}

	/**
	 * @return The squared euclidian length
	 */
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

	public static Vector2f sub(Vector2f v0, Vector2f v1) {
		float x = v0.x - v1.x;
		float y = v0.y - v1.y;
		return new Vector2f(x, y);
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

	public static Vector2f add(Vector2f v0, Vector2f v1) {
		float x = v0.x + v1.x;
		float y = v0.y + v1.y;
		return new Vector2f(x, y);
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

	/** Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
	public Vector2f rotate90(int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
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

	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
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

	/**
	 * @param v The other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(Vector2f v) {
		float x_d = v.x - x;
		float y_d = v.y - y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(float x, float y) {
		float x_d = x - this.x;
		float y_d = y - this.y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param v The other vector
	 * @return the squared distance between this and the other vector
	 */
	public float dst2(Vector2f v) {
		float x_d = v.x - x;
		float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	public Vector2f scale(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public static Vector2f scale(Vector2f v, float scalar) {
		float x = v.x * scalar;
		float y = v.y * scalar;
		return new Vector2f(x, y);
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
		return tmp.set(this);
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

	@Override
	public boolean equals(Object obj) {
		//
		if (obj instanceof Vector2f) {
			Vector2f other = (Vector2f) obj;
			return x == other.x && y == other.y && super.equals(obj);
		}

		return false;
	}

	public final Vector2f reflected(Vector2f normal) {
		return normal.mul(-2 * this.dot(normal)).add(this);
	}

	@Override
	public String toString() {
		return "[" + x + ":" + y + "]";
	}

}
