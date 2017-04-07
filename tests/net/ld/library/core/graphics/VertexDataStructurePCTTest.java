package net.ld.library.core.graphics;

import org.junit.Test;

public class VertexDataStructurePCTTest {

	/**
	 * Tests the validity of the internal consistency of elements returned by
	 * VertexDataStructurePCT.
	 */
	@Test
	public void testInternalConsistency() {
		// Arrange
		VertexDataStructurePCT vertex0 = new VertexDataStructurePCT();
		VertexDataStructurePCT vertex1 = new VertexDataStructurePCT();

		// Act
		vertex0.xyzw(2, 4, 8, 1);
		vertex0.rgba(3, 6, 9, 12);
		vertex0.uv(-0.25f, -0.75f);

		vertex1.xyz(1, 2, 3);
		vertex1.rgb(3, 6, 9);
		vertex1.uv(0.25f, 0.75f);

		float[] lElements = vertex0.getElements();

		// Assert
		{ // vertex 0 checks

			assert (vertex0.getXYZW()[0] == 2) : "vertex0 XYZW getter returns incorrect X component";
			assert (vertex0.getXYZW()[1] == 4) : "vertex0 XYZW getter returns incorrect Y component";
			assert (vertex0.getXYZW()[2] == 8) : "vertex0 XYZW getter returns incorrect Z component";
			assert (vertex0.getXYZW()[3] == 1) : "vertex0 XYZW getter returns incorrect W component";

			assert (vertex0.getXYZ()[0] == 2) : "vertex0 XYZ getter array returns incorrect X component";
			assert (vertex0.getXYZ()[1] == 4) : "vertex0 XYZ getter array returns incorrect Y component";
			assert (vertex0.getXYZ()[2] == 8) : "vertex0 XYZ getter array returns incorrect Z component";

			assert (vertex0.getRGBA()[0] == 3) : "vertex0 RGBA getter array returns incorrect R component";
			assert (vertex0.getRGBA()[1] == 6) : "vertex0 RGBA getter array returns incorrect G component";
			assert (vertex0.getRGBA()[2] == 9) : "vertex0 RGBA getter array returns incorrect B component";
			assert (vertex0.getRGBA()[3] == 12) : "vertex0 RGBA getter array returns incorrect A component";
			
			assert (vertex0.getRGB()[0] == 3) : "vertex0 RGB getter array returns incorrect R component";
			assert (vertex0.getRGB()[1] == 6) : "vertex0 RGB getter array returns incorrect G component";
			assert (vertex0.getRGB()[2] == 9) : "vertex0 RGB getter array returns incorrect B component";
			
			assert (vertex0.getUV()[0] == -0.25f) : "vertex0 UV getter array returns incorrect U component";
			assert (vertex0.getUV()[1] == -0.75f) : "vertex0 UV getter array returns incorrect V component";
			
			assert (lElements.length == 8) : "The number of elements in vertex.elements in not correct.";
			assert (lElements.length == VertexDataStructurePCT.elementCount) : "The number of elements in vertex.elements in not correct.";

		}

		{ // vertex 1 checks

			assert (vertex1.getXYZW()[0] == 2) : "vertex1 XYZW getter returns incorrect X component";
			assert (vertex1.getXYZW()[1] == 4) : "vertex1 XYZW getter returns incorrect Y component";
			assert (vertex1.getXYZW()[2] == 8) : "vertex1 XYZW getter returns incorrect Z component";
			assert (vertex1.getXYZW()[3] == 1) : "vertex1 XYZW getter returns incorrect W component";

			assert (vertex1.getXYZ()[0] == 2) : "vertex1 XYZ getter returns incorrect X component";
			assert (vertex1.getXYZ()[1] == 4) : "vertex1 XYZ getter array returns incorrect Y component";
			assert (vertex1.getXYZ()[2] == 8) : "vertex1 XYZ getter array returns incorrect Z component";

			assert (vertex1.getRGBA()[0] == 3) : "vertex1 RGBA getter array returns incorrect R component";
			assert (vertex1.getRGBA()[1] == 6) : "vertex1 RGBA getter array returns incorrect G component";
			assert (vertex1.getRGBA()[2] == 9) : "vertex1 RGBA getter array returns incorrect B component";
			assert (vertex1.getRGBA()[3] == 12) : "vertex1 RGBA getter array returns incorrect A component";
			
			assert (vertex1.getUV()[0] == 0.25f) : "vertex1 UV getter array returns incorrect U component";
			assert (vertex1.getUV()[1] == 0.75f) : "vertex1 UV getter array returns incorrect V component";

		}

	}

	/**
	 * Tests the validity of the internal sizes of the class variables
	 * associated with VertexDataStructurePCT.
	 */
	@Test
	public void testInternalSizes() {
		// Arrange
		VertexDataStructurePCT vertex = new VertexDataStructurePCT();

		// Act
		float[] lElements = vertex.getElements();

		// Assert
		assert (vertex.getXYZW().length
				* 4 == VertexDataStructurePCT.positionBytesCount) : "The positionBytesCount does not match the number of bytes in XYZW float array";
		assert (vertex.getXYZ().length
				* 4 == 12) : "Number of bytes in XYZ array is not as expected. Expected 12 bytes (3 floats)";

		assert (lElements.length * Float.BYTES == VertexDataStructurePCT.elementCount
				* VertexDataStructurePCT.elementBytes) : "The number of element bytes is not consistent.";

	}

}
