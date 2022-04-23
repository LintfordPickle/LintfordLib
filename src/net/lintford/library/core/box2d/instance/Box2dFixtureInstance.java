package net.lintford.library.core.box2d.instance;

import java.io.Serializable;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.instances.IndexedPooledBaseData;

public class Box2dFixtureInstance extends IndexedPooledBaseData {

	private static final long serialVersionUID = -584704908299820185L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient Fixture mFixture;

	public ShapeInstance shape;

	public String name;
	public float density;
	public float friction;
	public float restitution;
	public boolean isSensor;

	public String fixtureType;

	// Filter
	public int categoryBits;
	public int groupIndex;
	public int maskBits;

	public String spriteName;
	public transient int spriteIndex = -1;
	public Serializable userdata;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dFixtureInstance(int pPoolUid) {
		super(pPoolUid);

		density = 1f;
		friction = 0.5f;
		restitution = 0.5f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void savePhysics() {
		if (mFixture == null)
			return;

		density = mFixture.m_density;
		friction = mFixture.m_friction;
		restitution = mFixture.m_restitution;
		isSensor = mFixture.m_isSensor;

		if (mFixture.m_filter != null) {
			categoryBits = mFixture.m_filter.categoryBits;
			groupIndex = mFixture.m_filter.groupIndex;
			maskBits = mFixture.m_filter.maskBits;

		}

		shape.savePhysics();

	}

	public void loadPhysics(World pWorld, Body pParentBody) {
		FixtureDef lFixtureDef = new FixtureDef();

		lFixtureDef.density = density;
		lFixtureDef.restitution = restitution;
		lFixtureDef.friction = friction;
		lFixtureDef.isSensor = isSensor;

		lFixtureDef.filter = new Filter();
		lFixtureDef.filter.categoryBits = categoryBits;
		lFixtureDef.filter.maskBits = maskBits;
		lFixtureDef.filter.groupIndex = groupIndex;

		shape.loadPhysics();
		lFixtureDef.shape = shape.shape();

		mFixture = pParentBody.createFixture(lFixtureDef);
		if (name != null && !name.isEmpty()) {
			mFixture.setUserData(name);

		}

	}

	public void unloadPhysics(World pWorld) {
		if (mFixture != null) {
			var lParentBody = mFixture.getBody();
			lParentBody.destroyFixture(mFixture);
			mFixture = null;
		}
	}

	public void update(LintfordCore pCore) {

	}

}