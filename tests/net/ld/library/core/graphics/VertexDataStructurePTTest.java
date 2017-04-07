package net.ld.library.core.graphics;

import org.junit.Test;

public class VertexDataStructurePTTest {

	/**
	 * Tests the validity of the internal consistency of elements returned by
	 * VertexDataStructurePT.
	 */
	@Test
	public void testInternalConsistency() {
		// Arrange
		VertexDataStructurePT vertex = new VertexDataStructurePT();

		// Act
		vertex.xyzw(2, 4, 8, 1);
		vertex.uv(0.5f, -0.5f);

		float[] lElements = vertex.getElements();

		// Assert
		assert (vertex.getXYZW()[0] == 2) : "XYZW getter returns incorrect X component";
		assert (vertex.getXYZW()[1] == 4) : "XYZW getter returns incorrect Y component";
		assert (vertex.getXYZW()[2] == 8) : "XYZW getter returns incorrect Z component";
		assert (vertex.getXYZW()[3] == 1) : "XYZW getter returns incorrect W component";

		assert (vertex.getXYZ()[0] == 2) : "XYZ getter returns incorrect X component";
		assert (vertex.getXYZ()[1] == 4) : "XYZ getter array returns incorrect Y component";
		assert (vertex.getXYZ()[2] == 8) : "XYZ getter array returns incorrect Z component";

		assert (vertex.getUV()[0] == 0.5f) : "UV getter array returns incorrect U component";
		assert (vertex.getUV()[1] == -0.5f) : "UV getter array returns incorrect V component";

		assert (lElements.length == 6) : "The number of elements in vertex.elements in not correct.";
		assert (lElements.length == VertexDataStructurePT.elementCount) : "The number of elements in vertex.elements in not correct.";

	}

	/**
	 * Tests the validity of the internal sizes of the class variables
	 * associated with VertexDataStructurePT.
	 */
	@Test
	public void testInternalSizes() {
		// Arrange
		VertexDataStructurePT vertex = new VertexDataStructurePT();

		// Act
		float[] lElements = vertex.getElements();

		// Assert
		assert (vertex.getXYZW().length
				* 4 == VertexDataStructurePT.positionBytesCount) : "The positionBytesCount does not match the number of bytes in XYZW float array";
		assert (vertex.getXYZ().length
				* 4 == 12) : "Number of bytes in XYZ array is not as expected. Expected 12 bytes (3 floats)";

		assert (lElements.length * Float.BYTES == VertexDataStructurePT.elementCount
				* VertexDataStructurePT.elementBytes) : "The number of element bytes is not consistent.";

	}

}
