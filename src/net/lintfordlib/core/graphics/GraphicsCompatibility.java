package net.lintfordlib.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.lintfordlib.assets.ResourceManager;

public class GraphicsCompatibility {

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SuppressWarnings("unused")
	private final ResourceManager mResourceManager;

	private float mAliasedLineWidthMin;
	private float mAliasedLineWidthMax;
	private float mSmoothLineWidthMin;
	private float mSmoothLineWidthMax;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the minimum width supported as per GL12.GL_ALIASED_LINE_WIDTH_RANGE.
	 */
	public float aliasedLineWidthMin() {
		return mAliasedLineWidthMin;
	}

	/**
	 * Returns the maximum width supported as per GL12.GL_ALIASED_LINE_WIDTH_RANGE.
	 */
	public float aliasedLineWidthMax() {
		return mAliasedLineWidthMax;
	}

	/**
	 * Returns the minimum width supported as per GL12.GL_SMOOTH_LINE_WIDTH_RANGE.
	 */
	public float smoothLineWidthMin() {
		return mSmoothLineWidthMin;
	}

	/**
	 * Returns the maximum width supported as per GL12.GL_SMOOTH_LINE_WIDTH_RANGE.
	 */
	public float smoothLineWidthMax() {
		return mSmoothLineWidthMax;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GraphicsCompatibility(ResourceManager resources) {
		mResourceManager = resources;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialize() {
		final var aliasedLineWidthMin = new int[2];
		final var smoothLineWidthMin = new int[2];
		GL11.glGetIntegerv(GL12.GL_ALIASED_LINE_WIDTH_RANGE, aliasedLineWidthMin);
		GL11.glGetIntegerv(GL12.GL_SMOOTH_LINE_WIDTH_RANGE, smoothLineWidthMin);

		mAliasedLineWidthMin = aliasedLineWidthMin[0];
		mAliasedLineWidthMax = aliasedLineWidthMin[1];
		mSmoothLineWidthMin = smoothLineWidthMin[0];
		mSmoothLineWidthMax = smoothLineWidthMin[1];
	}

}
