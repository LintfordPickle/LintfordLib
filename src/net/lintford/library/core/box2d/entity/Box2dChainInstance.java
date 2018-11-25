package net.lintford.library.core.box2d.entity;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.Shape;

import net.lintford.library.data.BaseData;

public class Box2dChainInstance extends BaseData implements ShapeInstance {

	private static final long serialVersionUID = -6261742843197325804L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient ChainShape chainShape;

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
