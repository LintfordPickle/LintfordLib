package net.ld.library.core.camera;

import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;

/**
 * A interface for cameras, which provides the minimum needed matrices for GLSL
 * rendering (view, projection and a basic view frustum for culling ops).
 */
public interface ICamera {

	/** Returns the view matrix of this {@link ICamera} implementing class. */
	public abstract Matrix4f view();

	/**
	 * Returns the projection matrix of this {@link ICamera} implementing class.
	 */
	public abstract Matrix4f projection();

	/**
	 * Returns a {@link Rectangle} specifying the bounds of this {@link ICamera}
	 * implementing class (i.e. the view frustum)
	 */
	public abstract Rectangle boundingRectangle();

}
