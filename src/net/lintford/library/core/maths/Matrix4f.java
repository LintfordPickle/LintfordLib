package net.lintford.library.core.maths;

import java.io.Serializable;

/** A column-major (down-then across) Matrix4f class */
public class Matrix4f implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6105927998405250625L;

	public static final Matrix4f IDENTITY = new Matrix4f();

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static Vector3f TEMP_VECTOR = new Vector3f();

	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Matrix4f() {
		setIdentity();

		initialize();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public final void setIdentity() {

		m00 = 1f;
		m01 = 0f;
		m02 = 0f;
		m03 = 0f;

		m10 = 0f;
		m11 = 1f;
		m12 = 0f;
		m13 = 0f;

		m20 = 0f;
		m21 = 0f;
		m22 = 1f;
		m23 = 0f;

		m30 = 0f;
		m31 = 0f;
		m32 = 0f;
		m33 = 1f;

	}

	public void multiply(Matrix4f left) {

		float l00 = left.m00 * this.m00 + left.m10 * this.m01 + left.m20 * this.m02 + left.m30 * this.m03;
		float l01 = left.m01 * this.m00 + left.m11 * this.m01 + left.m21 * this.m02 + left.m31 * this.m03;
		float l02 = left.m02 * this.m00 + left.m12 * this.m01 + left.m22 * this.m02 + left.m32 * this.m03;
		float l03 = left.m03 * this.m00 + left.m13 * this.m01 + left.m23 * this.m02 + left.m33 * this.m03;
		float l10 = left.m00 * this.m10 + left.m10 * this.m11 + left.m20 * this.m12 + left.m30 * this.m13;
		float l11 = left.m01 * this.m10 + left.m11 * this.m11 + left.m21 * this.m12 + left.m31 * this.m13;
		float l12 = left.m02 * this.m10 + left.m12 * this.m11 + left.m22 * this.m12 + left.m32 * this.m13;
		float l13 = left.m03 * this.m10 + left.m13 * this.m11 + left.m23 * this.m12 + left.m33 * this.m13;
		float l20 = left.m00 * this.m20 + left.m10 * this.m21 + left.m20 * this.m22 + left.m30 * this.m23;
		float l21 = left.m01 * this.m20 + left.m11 * this.m21 + left.m21 * this.m22 + left.m31 * this.m23;
		float l22 = left.m02 * this.m20 + left.m12 * this.m21 + left.m22 * this.m22 + left.m32 * this.m23;
		float l23 = left.m03 * this.m20 + left.m13 * this.m21 + left.m23 * this.m22 + left.m33 * this.m23;
		float l30 = left.m00 * this.m30 + left.m10 * this.m31 + left.m20 * this.m32 + left.m30 * this.m33;
		float l31 = left.m01 * this.m30 + left.m11 * this.m31 + left.m21 * this.m32 + left.m31 * this.m33;
		float l32 = left.m02 * this.m30 + left.m12 * this.m31 + left.m22 * this.m32 + left.m32 * this.m33;
		float l33 = left.m03 * this.m30 + left.m13 * this.m31 + left.m23 * this.m32 + left.m33 * this.m33;

		this.m00 = l00;
		this.m01 = l01;
		this.m02 = l02;
		this.m03 = l03;
		this.m10 = l10;
		this.m11 = l11;
		this.m12 = l12;
		this.m13 = l13;
		this.m20 = l20;
		this.m21 = l21;
		this.m22 = l22;
		this.m23 = l23;
		this.m30 = l30;
		this.m31 = l31;
		this.m32 = l32;
		this.m33 = l33;

	}

	public static Matrix4f mul(Matrix4f left, Matrix4f right, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
		float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
		float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
		float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
		float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
		float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
		float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
		float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}

	public void createOrtho(float pLeft, float pRight, float pBottom, float pTop, float pNear, float pFar) {
		m00 = 2f / (pRight - pLeft);
		m11 = 2f / (pTop - pBottom);
		m22 = -(2f / (pFar - pNear));

		m03 = -(pRight + pLeft) / (pRight - pLeft);
		m13 = -(pTop + pBottom) / (pTop - pBottom);
		m23 = -(pFar + pNear) / (pFar - pNear);

		m33 = 1f;

	}

	public void translate(Vector3f pPosition) {
		translate(pPosition.x, pPosition.y, pPosition.z);
	}

	public void translate(float pX, float pY, float pZ) {
		m03 += pX;
		m13 += pY;
		m23 += pZ;

	}

	public void scale(Vector3f mScale) {
		scale(mScale.x, mScale.y, mScale.z);
	}

	public void scale(float x, float y, float z) {
		m00 = x;
		m11 = y;
		m22 = z;
	}

	public void rotate(float pAngle, Vector3f pRotationAxis) {
		rotate(pAngle, pRotationAxis.x, pRotationAxis.y, pRotationAxis.z);
	}

	/** Rotates the matrice around the given axes by the specified number of degrees */
	public void rotate(float angle, float x, float y, float z) {
		float c = (float) Math.cos(Math.toRadians(angle));
		float s = (float) Math.sin(Math.toRadians(angle));

		TEMP_VECTOR.x = x;
		TEMP_VECTOR.y = y;
		TEMP_VECTOR.z = z;
		if (TEMP_VECTOR.length() != 1f) {
			TEMP_VECTOR = TEMP_VECTOR.normalize();
			x = TEMP_VECTOR.x;
			y = TEMP_VECTOR.y;
			z = TEMP_VECTOR.z;
		}

		m00 = x * x * (1f - c) + c;
		m10 = y * x * (1f - c) + z * s;
		m20 = x * z * (1f - c) - y * s;
		m01 = x * y * (1f - c) - z * s;
		m11 = y * y * (1f - c) + c;
		m21 = y * z * (1f - c) + x * s;
		m02 = x * z * (1f - c) + y * s;
		m12 = y * z * (1f - c) - x * s;
		m22 = z * z * (1f - c) + c;

	}

	// --------------------------------------
	// Static-Methods
	// --------------------------------------

	public static Matrix4f transpose(Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();

		float m00 = src.m00;
		float m01 = src.m10;
		float m02 = src.m20;
		float m03 = src.m30;
		float m10 = src.m01;
		float m11 = src.m11;
		float m12 = src.m21;
		float m13 = src.m31;
		float m20 = src.m02;
		float m21 = src.m12;
		float m22 = src.m22;
		float m23 = src.m32;
		float m30 = src.m03;
		float m31 = src.m13;
		float m32 = src.m23;
		float m33 = src.m33;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}

	/**
	 * Transform a Vector by a matrix and return the result in a destination vector.
	 * 
	 * @param left
	 *            The left matrix
	 * @param right
	 *            The right vector
	 * @param dest
	 *            The destination vector, or null if a new one is to be created
	 * @return the destination vector
	 */
	public static Vector4f transform(Matrix4f left, Vector4f right, Vector4f dest) {
		if (dest == null)
			dest = new Vector4f();

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
		float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;

		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;

		return dest;
	}

	public void copy(Matrix4f pCopyFrom) {

		m00 = pCopyFrom.m00;
		m01 = pCopyFrom.m01;
		m02 = pCopyFrom.m02;

		m10 = pCopyFrom.m10;
		m11 = pCopyFrom.m11;
		m12 = pCopyFrom.m12;

		m20 = pCopyFrom.m20;
		m21 = pCopyFrom.m21;
		m22 = pCopyFrom.m22;

		m30 = pCopyFrom.m30;
		m31 = pCopyFrom.m31;
		m32 = pCopyFrom.m32;

	}
}
