package net.lintford.library.core.graphics;

import net.lintford.library.core.maths.Vector3f;

public class ColorConstants {
	
	// WindowClear Color
	public final static Vector3f BUFFER_CLEAR_DEBUG = new Vector3f(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f);
	public final static Vector3f BUFFER_CLEAR_RELEASE = new Vector3f(0.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f);
	
	// light sources
	public final static Vector3f CANDLE = new Vector3f(255f / 255f, 147f / 255f, 41f / 255f);
	public final static Vector3f FLAME = new Vector3f(255f / 255f, 147f / 255f, 41f / 255f);
	public final static Vector3f TUNGSTEN40 = new Vector3f(255f / 255f, 197f / 255f, 143f / 255f);
	public final static Vector3f TUNGSTEN100 = new Vector3f(255f / 255f, 214f / 255f, 224f / 255f);
	public final static Vector3f HALOGEN = new Vector3f(255f / 255f, 241f / 255f, 224f / 255f);
	
	// Day
	public final static Vector3f SUN = new Vector3f(255f / 255f, 255f / 255f, 251f / 255f);
	public final static Vector3f BLUE_SKY = new Vector3f(64f / 255f, 156f / 255f, 255f / 255f);
	public final static Vector3f OVERCAST = new Vector3f(201f / 255f, 226f / 255f, 255f / 255f);
	
	public final static Vector3f RED = new Vector3f(255f / 255f, 51f / 255f, 51f / 255f);
	public final static Vector3f GREEN = new Vector3f(51f / 255f, 255f / 255f, 51f / 255f);
	public final static Vector3f BLUE = new Vector3f(51f / 255f, 51f / 255f, 255f / 255f);
	
}
