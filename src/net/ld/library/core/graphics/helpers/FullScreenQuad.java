package net.ld.library.core.graphics.helpers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.VertexDataStructurePT;

public class FullScreenQuad {

	// =============================================
	// Variables
	// =============================================

	private int mVaoId = -1;
	private int mVboId = -1;

	// =============================================
	// Constructor
	// =============================================

	public FullScreenQuad() {

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadContent(ResourceManager pResourceManager) {

		mVaoId = GL30.glGenVertexArrays();
		mVboId = GL15.glGenBuffers();

		final float lWindowWidth = pResourceManager.displayConfig().windowWidth();
		final float lWindowHeight = pResourceManager.displayConfig().windowHeight();

		VertexDataStructurePT lVertex0 = new VertexDataStructurePT();
		lVertex0.xyzw(-lWindowWidth * 0.5f, lWindowHeight * 0.5f, 0f);
		lVertex0.uv(0, 0);

		VertexDataStructurePT lVertex1 = new VertexDataStructurePT();
		lVertex1.xyzw(-lWindowWidth * 0.5f, -lWindowHeight * 0.5f, 0f);
		lVertex1.uv(0, 1);

		VertexDataStructurePT lVertex2 = new VertexDataStructurePT();
		lVertex2.xyzw(lWindowWidth * 0.5f, -lWindowHeight * 0.5f, 0f);
		lVertex2.uv(1, 1);

		VertexDataStructurePT lVertex3 = new VertexDataStructurePT();
		lVertex3.xyzw(lWindowWidth * 0.5f, lWindowHeight * 0.5f, 0f);
		lVertex3.uv(1, 0);

		FloatBuffer lBuffer = BufferUtils.createFloatBuffer(6 * VertexDataStructurePT.stride);

		lBuffer.put(lVertex0.getElements());
		lBuffer.put(lVertex1.getElements());
		lBuffer.put(lVertex2.getElements());

		lBuffer.put(lVertex2.getElements());
		lBuffer.put(lVertex3.getElements());
		lBuffer.put(lVertex0.getElements());

		lBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

	}

	public void draw() {

		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

	}

}
