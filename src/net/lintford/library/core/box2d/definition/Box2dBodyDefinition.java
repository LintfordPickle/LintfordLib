package net.lintford.library.core.box2d.definition;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

public class Box2dBodyDefinition {

	public static final int BODY_TYPE_INDEX_STATIC = 0;
	public static final int BODY_TYPE_INDEX_KINEMATIC = 1;
	public static final int BODY_TYPE_INDEX_DYNAMIC = 2;
	
	// --------------------------------------
	// Variables
	// --------------------------------------

	public BodyDef bodyDefinition = new BodyDef();
	public List<Box2dFixtureDefinition> fixtureList = new ArrayList<>();

	public String name;
	
	// Body data is stored directly in the BodyDef instance above.
	public int bodyTypeIndex;

	// Mass data
	public float mass;
	public Vec2 massCenter;
	public float massI;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dBodyDefinition() {

	}

}
