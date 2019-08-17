package net.lintford.library.core.box2d.entity;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import net.lintford.library.core.entity.BaseData;

public class Box2dChainInstance extends BaseData implements ShapeInstance {

	private static final long serialVersionUID = -6261742843197325804L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient ChainShape chainShape;

	public boolean hasPrevVertex = false;
	public boolean hasNextVertex = false;

	public Vec2 vertex0 = new Vec2();
	public Vec2 vertex1 = new Vec2();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dChainInstance() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Shape shape() {
		return chainShape;
	}

	public void savePhysics() {
		if (chainShape == null)
			return;

	}

	public void loadPhysics() {

	}

	@Override
	public ShapeInstance getCopy() {
		Box2dChainInstance lReturn = new Box2dChainInstance();

		return lReturn;
	}

}
