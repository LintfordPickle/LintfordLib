package net.lintfordlib.core.graphics.common;

public class VertexDataStructurePT {
	public static final int elementBytes = 4;

	public static final int positionElementCount = 4;
	public static final int textureElementCount = 2;

	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int textureBytesCount = textureElementCount * elementBytes;

	public static final int positionByteOffset = 0;
	public static final int textureByteOffset = positionByteOffset + positionBytesCount;

	public static final int elementCount = positionElementCount + textureElementCount;

	public static final int stride = positionBytesCount + textureBytesCount;
}
