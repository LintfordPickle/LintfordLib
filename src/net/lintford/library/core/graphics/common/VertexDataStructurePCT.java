package net.lintford.library.core.graphics.common;

public abstract class VertexDataStructurePCT {
	public static final int elementBytes = 4;

	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;

	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorBytesCount = colorElementCount * elementBytes;
	public static final int textureBytesCount = textureElementCount * elementBytes;

	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorBytesCount;

	public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

	public static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;

}
