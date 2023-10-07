package net.lintfordlib.core.maths;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class MathUtil {

	// --------------------------------------
	// Statics
	// --------------------------------------

	private static FloatBuffer int_mat_float_buffer;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static void allocResources() {
		if (int_mat_float_buffer == null) {
			int_mat_float_buffer = MemoryUtil.memAllocFloat(16);
		}
	}

	public static void freeResources() {
		if (int_mat_float_buffer != null) {
			MemoryUtil.memFree(int_mat_float_buffer);
			int_mat_float_buffer = null;
		}
	}

	public static FloatBuffer getMatBufferRowMaj(Matrix4f matrix) {
		if (int_mat_float_buffer == null)
			allocResources();

		int_mat_float_buffer.clear();

		// row-major
		int_mat_float_buffer.put(matrix.m00).put(matrix.m10).put(matrix.m20).put(matrix.m30);
		int_mat_float_buffer.put(matrix.m01).put(matrix.m11).put(matrix.m21).put(matrix.m31);
		int_mat_float_buffer.put(matrix.m02).put(matrix.m12).put(matrix.m22).put(matrix.m32);
		int_mat_float_buffer.put(matrix.m03).put(matrix.m13).put(matrix.m23).put(matrix.m33);

		int_mat_float_buffer.flip();
		return int_mat_float_buffer;
	}

	public static FloatBuffer getMatBufferColMaj(Matrix4f matrix) {
		if (int_mat_float_buffer == null)
			allocResources();

		int_mat_float_buffer.clear();

		// col-major
		int_mat_float_buffer.put(matrix.m00).put(matrix.m01).put(matrix.m02).put(matrix.m03);
		int_mat_float_buffer.put(matrix.m10).put(matrix.m11).put(matrix.m12).put(matrix.m13);
		int_mat_float_buffer.put(matrix.m20).put(matrix.m21).put(matrix.m22).put(matrix.m23);
		int_mat_float_buffer.put(matrix.m30).put(matrix.m31).put(matrix.m32).put(matrix.m33);

		int_mat_float_buffer.flip();
		return int_mat_float_buffer;
	}

	/**
	 * According to opengl.org, a col-maj matrix has translation elements at position 15,16 and 17. (http://www.opengl.org/archives/resources/faq/technical/transformations.htm)
	 * 
	 * @return A FloatBuffer containing a col-maj matrix
	 */
	public FloatBuffer copyToBufferColMaj(final FloatBuffer floatBuffer, Matrix4f matrix) {
		if (floatBuffer == null)
			return null;

		floatBuffer.clear();

		floatBuffer.put(matrix.m00).put(matrix.m10).put(matrix.m20).put(matrix.m30);
		floatBuffer.put(matrix.m01).put(matrix.m11).put(matrix.m21).put(matrix.m31);
		floatBuffer.put(matrix.m02).put(matrix.m12).put(matrix.m22).put(matrix.m32);
		floatBuffer.put(matrix.m03).put(matrix.m13).put(matrix.m23).put(matrix.m33);

		return floatBuffer;
	}

	/** @return A FloatBuffer containing a row-maj matrix */
	public FloatBuffer getBufferTranspose(Matrix4f matrix) {
		int_mat_float_buffer.clear();

		int_mat_float_buffer.put(matrix.m00).put(matrix.m01).put(matrix.m02).put(matrix.m03);
		int_mat_float_buffer.put(matrix.m10).put(matrix.m11).put(matrix.m12).put(matrix.m13);
		int_mat_float_buffer.put(matrix.m20).put(matrix.m21).put(matrix.m22).put(matrix.m23);
		int_mat_float_buffer.put(matrix.m30).put(matrix.m31).put(matrix.m32).put(matrix.m33);

		int_mat_float_buffer.flip();

		return int_mat_float_buffer;
	}
}
