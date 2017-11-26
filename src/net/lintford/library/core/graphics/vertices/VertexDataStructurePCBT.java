package net.lintford.library.core.graphics.vertices;

import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.core.maths.Vector4f;

// (P)osition, (C)olor, (T)exture and (B)oneID
public class VertexDataStructurePCBT {

	private float[] mXYZW = new float[] { 0f, 0f, 0f, 1f };
	private float[] mRGBA = new float[] { 1f, 0f, 1f, 1f };
	private float[] mUV = new float[] { 0f, 0f };
	private int mB;

	// The number of bytes an element has (all elements are floats here)
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;
	public static final int boneElementCount = 1;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorBytesCount = colorElementCount * elementBytes;
	public static final int textureBytesCount = textureElementCount * elementBytes;
	public static final int boneBytesCount = boneElementCount * elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorBytesCount;
	public static final int boneByteOffset = 0;

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
	
	public void rgb(Vector3f pColor) {
		mRGBA = new float[] { pColor.x, pColor.y, pColor.z, 1f };
	}
	
	public void rgba(Vector4f pColor) {
		mRGBA = new float[] { pColor.x, pColor.y, pColor.z, pColor.w };
	}

	public void b(int b){
		mB = b;
	}
	
	public float[] getElements() {
		float[] out = new float[VertexDataStructurePCBT.elementCount];
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
		// Inert Bone ID Element
		//out[i++] = this.mB;
		
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
	
	public int b(){
		return mB;
	}
}
