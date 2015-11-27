package net.ld.library.core.particles;

import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.maths.Vector4f;

public class Particle {

	// =============================================
	// Variables
	// =============================================

	private boolean mIsFree;
	
	public float mTotalLife;
	public float mLife;

	public float angle;

	public Vector2f position;
	public Vector2f size;
	public Vector2f acceleration;
	public Vector2f velocity;
	public Vector4f color;
	
	public ISprite sprite;

	// =============================================
	// Properties
	// =============================================

	public boolean isFree() {
		return mIsFree;
	}

	// =============================================
	// Constructor
	// =============================================

	public Particle() {

		color = new Vector4f(1f);
		position = new Vector2f();
		size = new Vector2f();
		acceleration = new Vector2f();
		velocity = new Vector2f();

		reset();
	}

	// =============================================
	// Methods
	// =============================================

	public void assignParticle(float pX, float pY, float pVelX, float pVelY, ISprite pSprite) {
		mIsFree = false;
		sprite = pSprite;

		position.x = pX;
		position.y = pY;

		size.x = pSprite.getWidth();
		size.y = pSprite.getHeight();

		velocity.x = pVelX;
		velocity.y = pVelY;

		acceleration.x = 0;
		acceleration.y = 0;
	}

	public void reset() {
		mIsFree = true;
		mLife = 0;

		position.x = 0;
		position.y = 0;

		size.x = 128f;
		size.y = 128f;

		velocity.x = 0;
		velocity.y = 0;

		acceleration.x = 0;
		acceleration.y = 0;
	}
}
