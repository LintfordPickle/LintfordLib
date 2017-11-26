package net.lintford.library.core.graphics.vertices;

public class VertexDataStructurePCT {

	private float[] mXYZW = new float[] { 0f, 0f, 0f, 1f };
	private float[] mRGBA = new float[] { 1f, 0f, 1f, 1f };
	private float[] mUV = new float[] { 0f, 0f };

	// The number of bytes an element has (all elements are floats here)
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorBytesCount = colorElementCount * elementBytes;
	public static final int textureBytesCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorBytesCount;

	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

	// The size of a vertex in bytes (sizeOf())
	public static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;

	public void xyzw(float x, float y, float z) {
		mXYZW = new float[] { x, y, z, 1f };
	}

	public void rgba(float r, float g, float b) {
		mRGBA = new float[] { r, g, b, 1f };
	}

	public void uv(float u, float v) {
		mUV = new float[] { u, v };
	}

	public void xyzw(float x, float y, float z, float w) {
		mXYZW = new float[] { x, y, z, w };
	}

	public void rgba(float r, float g, float b, float a) {
		mRGBA = new float[] { r, g, b, a };
	}

	public float[] getElements() {
		float[] out = new float[VertexDataStructurePCT.elementCount];
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
	
	public float[] getRGBA() {
		return new float[] {this.mRGBA[0], this.mRGBA[1], this.mRGBA[2], this.mRGBA[3]};
	}
	
	public float[] getRGB() {
		return new float[] {this.mRGBA[0], this.mRGBA[1], this.mRGBA[2]};
	}
	
	public float[] getST() {
		return new float[] {this.mUV[0], this.mUV[1]};
	}
	
}
