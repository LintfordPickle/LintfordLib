package net.lintford.library.data.entities;

import net.lintford.library.core.time.GameTime;
import net.lintford.library.data.BaseData;

public abstract class WorldEntity extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;
	
	// TODO: Track Physics constants in a dedicated location (i.e. PhysicsConstants.Java)
	public static final float FRICTION_X = 0.96f;
	public static final float FRICTION_Y = 0.96f;
	public static final float EPSILON = 0.001f;
	public static final float GRAVITY = 0.04f;

	public final static float SKIN_WIDTH = 0.15f;
	
	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x, y; // center position
	public transient float oldX, oldY; // old center positions
	public float dx, dy; // movement
	public float dr; 
	public transient float oldDX, oldDY; // movement

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setPosition(float pWorldX, float pWorldY) {
		x = pWorldX;
		y = pWorldY;

		dx = dy = 0;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WorldEntity() {
		dx = dy = 0f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setVelocity(float pVelX, float pVelY) {
		dx = pVelX;
		dy = pVelY;

	}

	public boolean hasCollision(int x, int y) {
		return false;
	}

	public void update(GameTime pGameTime)
	{
	    oldX = x;
	    oldY = y;
	    
	    oldDX = dx;
	    oldDY = dy;
	    
	}
	
	// --------------------------------------
	// Abstract Methods
	// --------------------------------------

	public abstract boolean intersects(WorldEntity pOther);

}