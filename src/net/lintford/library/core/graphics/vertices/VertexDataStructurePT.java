package net.lintford.library.core.graphics.vertices;

public class VertexDataStructurePT {

	private float[] mXYZW = new float[] { 0f, 0f, 0f, 1f };
	private float[] mUV = new float[] { 0f, 0f };

	// The number of bytes an element has (all elements are floats here)
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int textureElementCount = 2;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int textureBytesCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int textureByteOffset = positionByteOffset + positionBytesCount;

	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + textureElementCount;

	// The size of a vertex in bytes (sizeOf())
	public static final int stride = positionBytesCount + textureBytesCount;

	public void xyzw(float x, float y, float z) {
		mXYZW = new float[] { x, y, z, 1f };
	}

	public void uv(float u, float v) {
		mUV = new float[] { u, v };
	}

	public void xyzw(float x, float y, float z, float w) {
		mXYZW = new float[] { x, y, z, w };
	}

	public float[] getElements() {
		float[] out = new float[VertexDataStructurePT.elementCount];
		int i = 0;
		
		// Insert XYZW elements
		out[i++] = this.mXYZW[0];
		out[i++] = this.mXYZW[1];
		out[i++] = this.mXYZW[2];
		out[i++] = this.mXYZW[3];
		// Insert UV elements
		out[i++] = this.mUV[0];
		out[i++] = this.mUV[1];
		
		return out;
	}
	
	public float[] getXYZW() {
		return new float[] {this.mXYZW[0], this.mXYZW[1], this.mXYZW[2], this.mXYZW[3]};
	}
	
	public float[] getXYZ() {
		return new float[] {this.mXYZW[0], this.mXYZW[1], this.mXYZW[2]};
	}
	
	public float[] getST() {
		return new float[] {this.mUV[0], this.mUV[1]};
	}
	
}
