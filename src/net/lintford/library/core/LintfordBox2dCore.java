package net.lintford.library.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import net.lintford.library.GameInfo;

public abstract class LintfordBox2dCore extends LintfordCore {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final Vec2 gravity = new Vec2(0, 9.8f);

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected World mWorld = new World(gravity);

	protected int mWelocityIterations = 6;
	protected int mPositionIterations = 2;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public LintfordBox2dCore(GameInfo pGameInfo) {
		this(pGameInfo, null);

	}

	public LintfordBox2dCore(GameInfo pGameInfo, String[] pArgs) {
		this(pGameInfo, pArgs, false);

	}

	public LintfordBox2dCore(GameInfo pGameInfo, String[] pArgs, boolean pHeadless) {
		super(pGameInfo, pArgs, pHeadless);

		mIsFixedTimeStep = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mWorld.step((float) mGameTime.elapseGameTimeSeconds(), mWelocityIterations, mPositionIterations);

	}

}
