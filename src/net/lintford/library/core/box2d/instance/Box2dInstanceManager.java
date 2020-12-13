package net.lintford.library.core.box2d.instance;

import net.lintford.library.core.box2d.pools.Box2dBodyInstanceRepository;
import net.lintford.library.core.box2d.pools.Box2dFixtureInstanceRepository;
import net.lintford.library.core.box2d.pools.Box2dRevJointInstanceRepository;
import net.lintford.library.core.box2d.pools.Box2dWeldJointInstanceRepository;

public class Box2dInstanceManager {

	// this class and its members don't need to be serialized!

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Box2dBodyInstanceRepository mBox2dBodyInstanceRepository;
	private Box2dRevJointInstanceRepository mBox2dJointInstanceRepository;
	private Box2dWeldJointInstanceRepository mBox2dWeldInstanceRepository;
	private Box2dFixtureInstanceRepository mBox2dFixtureInstanceRepository;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Box2dFixtureInstanceRepository box2dFixtureInstanceRepository() {
		return mBox2dFixtureInstanceRepository;
	}

	public Box2dBodyInstanceRepository box2dBodyInstanceRepository() {
		return mBox2dBodyInstanceRepository;
	}

	public Box2dRevJointInstanceRepository box2dJointInstanceRepository() {
		return mBox2dJointInstanceRepository;
	}

	public Box2dWeldJointInstanceRepository box2dWeldJointInstanceRepository() {
		return mBox2dWeldInstanceRepository;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dInstanceManager() {
		mBox2dBodyInstanceRepository = new Box2dBodyInstanceRepository();
		mBox2dJointInstanceRepository = new Box2dRevJointInstanceRepository();
		mBox2dFixtureInstanceRepository = new Box2dFixtureInstanceRepository();
		mBox2dWeldInstanceRepository = new Box2dWeldJointInstanceRepository();

	}

}
