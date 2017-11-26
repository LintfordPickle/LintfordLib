package net.lintford.library.core.collisions;

import java.io.Serializable;

import net.lintford.library.core.time.GameTime;

public class PhysicsState implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3796685365768809197L;

	public static final float SKIN_WIDTH = 0.025f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float collisionX;
	public float collisionY;

	public float blockX;
	public float blockY;

	public boolean mPushedRightWall;
	public boolean mPushesRightWall;

	public boolean mPushedLeftWall;
	public boolean mPushesLeftWall;

	public boolean isBlockOverhead; // Is block in the next tile above
	public boolean isBlockOverheadPlusOne; // Is block overhead +1

	public boolean mWasOnGround;
	public boolean mOnGround;

	public boolean mWasAtCeiling;
	public boolean mAtCeiling;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {
		collisionX = 0;
		collisionY = 0;

		mPushesLeftWall = false;
		mPushesRightWall = false;
		mOnGround = false;
		mAtCeiling = false;

		isBlockOverhead = false;

	}

	public void update(GameTime pGameTime) {
		mWasOnGround = mOnGround;
		mPushedRightWall = mPushesRightWall;
		mPushedLeftWall = mPushesLeftWall;
		mWasAtCeiling = mAtCeiling;

		reset();

	}

	public void setOnGround() {
		mOnGround = true;

	}

}
