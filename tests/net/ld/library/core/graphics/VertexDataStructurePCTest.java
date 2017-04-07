package net.ld.library.core.graphics;

import org.junit.Test;

public class VertexDataStructurePCTest {

	/**
	 * Tests the validity of the internal consistency of elements returned by
	 * VertexDataStructurePC.
	 */
	@Test
	public void testInternalConsistency() {
		// Arrange
		VertexDataStructurePC vertex0 = new VertexDataStructurePC();
		VertexDataStructurePC vertex1 = new VertexDataStructurePC();

		// Act
		vertex0.xyzw(2, 4, 8, 1);
		vertex0.rgba(3, 6, 9, 12);

		vertex1.xyz(1, 2, 3);
		vertex1.rgb(3, 6, 9);

		float[] lElements = vertex0.getElements();

		// Assert
		{ // vertex 0 checks

			assert (vertex0.getXYZW()[0] == 2) : "XYZW getter returns incorrect X component";
			assert (vertex0.getXYZW()[1] == 4) : "XYZW getter returns incorrect Y component";
			assert (vertex0.getXYZW()[2] == 8) : "XYZW getter returns incorrect Z component";
			assert (vertex0.getXYZW()[3] == 1) : "XYZW getter returns incorrect W component";

			assert (vertex0.getXYZ()[0] == 2) : "XYZ getter returns incorrect X component";
			assert (vertex0.getXYZ()[1] == 4) : "XYZ getter array returns incorrect Y component";
			assert (vertex0.getXYZ()[2] == 8) : "XYZ getter array returns incorrect Z component";

			assert (vertex0.getRGBA()[0] == 3) : "RGBA getter array returns incorrect R component";
			assert (vertex0.getRGBA()[1] == 6) : "RGBA getter array returns incorrect G component";
			assert (vertex0.getRGBA()[2] == 9) : "RGBA getter array returns incorrect B component";
			assert (vertex0.getRGBA()[3] == 12) : "RGBA getter array returns incorrect A component";
			
			assert (lElements.length == 8) : "The number of elements in vertex.elements in not correct.";
			assert (lElements.length == VertexDataStructurePC.elementCount) : "The number of elements in vertex.elements in not correct.";

		}

		{ // vertex 1 checks

			assert (vertex1.getXYZW()[0] == 1) : "XYZW getter returns incorrect X component";
			assert (vertex1.getXYZW()[1] == 2) : "XYZW getter returns incorrect Y component";
			assert (vertex1.getXYZW()[2] == 3) : "XYZW getter returns incorrect Z component";
			assert (vertex1.getXYZW()[3] == 1) : "XYZW getter returns incorrect W component";

			assert (vertex1.getXYZ()[0] == 1) : "XYZ getter returns incorrect X component";
			assert (vertex1.getXYZ()[1] == 2) : "XYZ getter array returns incorrect Y component";
			assert (vertex1.getXYZ()[2] == 3) : "XYZ getter array returns incorrect Z component";

			assert (vertex1.getRGBA()[0] == 3) : "RGBA getter array returns incorrect R component";
			assert (vertex1.getRGBA()[1] == 6) : "RGBA getter array returns incorrect G component";
			assert (vertex1.getRGBA()[2] == 9) : "RGBA getter array returns incorrect B component";
			assert (vertex1.getRGBA()[3] == 1) : "RGBA getter array returns incorrect A component";

			assert (vertex1.getRGB()[0] == 3) : "RGB getter array returns incorrect R component";
			assert (vertex1.getRGB()[1] == 6) : "RGB getter array returns incorrect G component";
			assert (vertex1.getRGB()[2] == 9) : "RGB getter array returns incorrect B component";

		}

	}

	/**
	 * Tests the validity of the internal sizes of the class variables
	 * associated with VertexDataStructurePT.
	 */
	@Test
	public void testInternalSizes() {
		// Arrange
		VertexDataStructurePC vertex = new VertexDataStructurePC();

		// Act
		float[] lElements = vertex.getElements();

		// Assert
		assert (vertex.getXYZW().length
				* 4 == VertexDataStructurePC.positionBytesCount) : "The positionBytesCount does not match the number of bytes in XYZW float array";
		assert (vertex.getXYZ().length
				* 4 == 12) : "Number of bytes in XYZ array is not as expected. Expected 12 bytes (3 floats)";

		assert (lElements.length * Float.BYTES == VertexDataStructurePC.elementCount
				* VertexDataStructurePC.elementBytes) : "The number of element bytes is not consistent.";

	}

}
