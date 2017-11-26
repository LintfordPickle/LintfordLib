package net.lintford.library.core.maths;

import java.io.Serializable;

/**
 * Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself
 * 
 * @author badlogicgames@gmail.com
 * 
 */
public final class Vector2f implements Serializable {

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
	 * @param x
	 *            The x-component
	 * @param y
	 *            The y-component
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a vector from the given vector
	 * 
	 * @param v
	 *            The vector
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
		return (float) Math.sqrt(x.x * y.x + x.y * y.y);
	}

	public static Vector2f CatmullRom(Vector2f value1, Vector2f value2, Vector2f value3, Vector2f value4, float amount, Vector2f returnVector) {
		float num = amount * amount;
		float num2 = amount * num;
		returnVector.x = 0.5f * ((((2f * value2.x) + ((-value1.x + value3.x) * amount)) + (((((2f * value1.x) - (5f * value2.x)) + (4f * value3.x)) - value4.x) * num)) + ((((-value1.x + (3f * value2.x)) - (3f * value3.x)) + value4.x) * num2));
		returnVector.y = 0.5f * ((((2f * value2.y) + ((-value1.y + value3.y) * amount)) + (((((2f * value1.y) - (5f * value2.y)) + (4f * value3.y)) - value4.y) * num)) + ((((-value1.y + (3f * value2.y)) - (3f * value3.y)) + value4.y) * num2));
		return returnVector;
	}

	public static float Distance(Vector2f value1, Vector2f value2) {
		float num2 = value1.x - value2.x;
		float num = value1.y - value2.y;
		float num3 = (num2 * num2) + (num * num);
		return (float) Math.sqrt(num3);
	}

	/**
	 * @return The squared euclidian length
	 */
	public float len2() {
		return x * x + y * y;
	}

	/**
	 * Sets this vector from the given vector
	 * 
	 * @param v
	 *            The vector
	 * @return This vector for chaining
	 */
	public Vector2f set(Vector2f v) {
		x = v.x;
		y = v.y;
		return this;
	}

	/**
	 * Sets the components of this vector
	 * 
	 * @param x
	 *            The x-component
	 * @param y
	 *            The y-component
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
	 * @param v
	 *            The vector
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
	 * Adds the given vector to this vector
	 * 
	 * @param v
	 *            The vector
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
	 * @param x
	 *            The x-component
	 * @param y
	 *            The y-component
	 * @return This vector for chaining
	 */
	public Vector2f add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * @param v
	 *            The other vector
	 * @return The dot product between this and the other vector
	 */
	public float dot(Vector2f v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Multiplies this vector by a scalar
	 * 
	 * @param scalar
	 *            The scalar
	 * @return This vector for chaining
	 */
	public Vector2f mul(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/**
	 * @param v
	 *            The other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(Vector2f v) {
		float x_d = v.x - x;
		float y_d = v.y - y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
	 * @return the distance between this and the other vector
	 */
	public float dst(float x, float y) {
		float x_d = x - this.x;
		float y_d = y - this.y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param v
	 *            The other vector
	 * @return the squared distance between this and the other vector
	 */
	public float dst2(Vector2f v) {
		float x_d = v.x - x;
		float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public String toString() {
		return "[" + x + ":" + y + "]";
	}

	/**
	 * Substracts the other vector from this vector.
	 * 
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
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
	 * @param v
	 *            the other vector
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
}
