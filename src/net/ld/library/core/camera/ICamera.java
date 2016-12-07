package net.ld.library.core.camera;

import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;

public interface ICamera {
	// =============================================
	// Properties
	// =============================================

	public abstract Matrix4f projection();

	public abstract Matrix4f view();

	public abstract Rectangle boundingRectangle();

	public abstract float getMinX();

	public abstract float getMaxX();

	public abstract float getMinY();

	public abstract float getMaxY();

	public abstract float getWidth();

	public abstract float getHeight();
	
	public abstract Vector2f getPosition();
	
	public abstract float getZoomFactor();
	
	public abstract void setZoomFactor(float pNewValue);
	
	public abstract float getZoomFactorOverOne();
	
	// =============================================
	// Methods
	// =============================================

	public abstract void setPosition(float pX, float pY);

	public abstract Vector2f getMouseCameraSpace();
	
	public abstract float getMouseCameraSpaceX();
	
	public abstract float getMouseCameraSpaceY();
	
	public abstract float getPointCameraSpaceX(float pPointX);
	
	public abstract float getPointCameraSpaceY(float pPointY);
	
	
}
