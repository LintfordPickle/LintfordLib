package net.ld.library.core.graphics;

import net.ld.library.core.maths.Vector3f;
import net.ld.library.core.maths.Vector4f;

public class VertexDataStructurePC {

	private float[] mXYZW = new float[] { 0f, 0f, 0f, 1f };
	private float[] mRGBA = new float[] { 1f, 0f, 1f, 1f };

	// The number of bytes an element has (all elements are floats here)
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorBytesCount = colorElementCount * elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;

	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount;

	// The size of a vertex in bytes (sizeOf())
	public static final int stride = positionBytesCount + colorBytesCount;

	public void xyz(Vector3f pPosition) {
		mXYZW = new float[] { pPosition.x, pPosition.y, pPosition.z, 1f };
	}

	public void xyz(float x, float y, float z) {
		mXYZW = new float[] { x, y, z, 1f };
	}

	public void rgb(Vector3f pCol) {
		mRGBA = new float[] { pCol.x, pCol.y, pCol.z, 1f };
	}

	public void rgb(float r, float g, float b) {
		mRGBA = new float[] { r, g, b, 1f };
	}

	public void xyzw(float x, float y, float z, float w) {
		mXYZW = new float[] { x, y, z, w };
	}

	public void xyzw(Vector4f pPosition) {
		mXYZW = new float[] { pPosition.x, pPosition.y, pPosition.z, pPosition.w };
	}

	public void rgba(float r, float g, float b, float a) {
		mRGBA = new float[] { r, g, b, a };
	}

	public void rgba(Vector4f pCol) {
		mRGBA = new float[] { pCol.x, pCol.y, pCol.z, pCol.w };
	}

	public float[] getElements() {
		float[] out = new float[VertexDataStructurePC.elementCount];
		int i = 0;

		// Insert XYZW elements
		out[i++] = this.mXYZW[0];
		out[i++] = this.mXYZW[1];
		out[i++] = this.mXYZW[2];
		out[i++] = this.mXYZW[3];
		// Insert RGBA elements
		out[i++] = this.mRGBA[0];
		out[i++] = this.mRGBA[1];
		out[i++] = this.mRGBA[2];
		out[i++] = this.mRGBA[3];

		return out;
	}

	public float[] getXYZW() {
		return new float[] { this.mXYZW[0], this.mXYZW[1], this.mXYZW[2], this.mXYZW[3] };
	}

	public float[] getXYZ() {
		return new float[] { this.mXYZW[0], this.mXYZW[1], this.mXYZW[2] };
	}

	public float[] getRGBA() {
		return new float[] { this.mRGBA[0], this.mRGBA[1], this.mRGBA[2], this.mRGBA[3] };
	}

	public float[] getRGB() {
		return new float[] { this.mRGBA[0], this.mRGBA[1], this.mRGBA[2] };
	}

}