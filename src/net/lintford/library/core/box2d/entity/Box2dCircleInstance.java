package net.lintford.library.core.box2d.entity;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import net.lintford.library.data.BaseData;

public class Box2dCircleInstance extends BaseData implements ShapeInstance {

	private static final long serialVersionUID = 3899380845018117078L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient CircleShape circleShape;

	public Vec2 center = new Vec2();
	public float radius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dCircleInstance() {
		this(0.5f);

	}

	public Box2dCircleInstance(float pRadius) {
		radius = pRadius;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Shape shape() {
		return circleShape;
	}

	public void savePhysics() {
		if (circleShape == null)
			return;

		radius = circleShape.m_radius;
		center.set(circleShape.m_p);

	}

	public void loadPhysics() {
		circleShape = new CircleShape();

		circleShape.m_p.set(center);
		circleShape.m_radius = radius;

	}

	@Override
	public ShapeInstance getCopy() {
		Box2dCircleInstance lReturn = new Box2dCircleInstance();

		lReturn.center.set(center);
		lReturn.radius = radius;

		return lReturn;
	}

}
