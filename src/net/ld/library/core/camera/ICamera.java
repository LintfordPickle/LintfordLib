package net.ld.library.core.camera;

import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

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
	
	// =============================================
	// Methods
	// =============================================

	public abstract void update(GameTime pGameTime);
	
	public abstract void setTargetPosition(float pX, float pY);
	
	public abstract Vector2f getMouseCameraSpace(double pMouseX, double pMouseY);
	
}
