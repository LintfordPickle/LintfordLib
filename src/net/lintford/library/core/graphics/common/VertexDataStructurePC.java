package net.lintford.library.core.graphics.common;

public class VertexDataStructurePC {
	public static final int elementBytes = 4;

	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;

	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorBytesCount = colorElementCount * elementBytes;

	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;

	public static final int elementCount = positionElementCount + colorElementCount;

	public static final int stride = positionBytesCount + colorBytesCount;
}
