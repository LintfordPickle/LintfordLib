package net.lintford.library.core.box2d.entity;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import net.lintford.library.data.BaseData;

public class Box2dEdgeInstance extends BaseData implements ShapeInstance {

	private static final long serialVersionUID = 1364802849512686266L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient EdgeShape edgeShape;

	public boolean hasVertex0 = false;
	public boolean hasVertex3 = false;

	public Vec2 vertex0 = new Vec2();
	public Vec2 vertex1 = new Vec2();
	public Vec2 vertex2 = new Vec2();
	public Vec2 vertex3 = new Vec2();

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Shape shape() {
		return edgeShape;
	}

	public void savePhysics() {
		if (edgeShape == null)
			return;

		hasVertex0 = edgeShape.m_hasVertex0;
		hasVertex3 = edgeShape.m_hasVertex3;

		if (hasVertex0)
			vertex0.set(edgeShape.m_vertex0);

		vertex1.set(edgeShape.m_vertex1);
		vertex2.set(edgeShape.m_vertex2);

		if (hasVertex3)
			vertex3.set(edgeShape.m_vertex3);

	}

	public void loadPhysics() {
		edgeShape = new EdgeShape();

		edgeShape.m_hasVertex0 = hasVertex0;
		edgeShape.m_hasVertex3 = hasVertex3;

		edgeShape.m_vertex0.set(vertex0);
		edgeShape.m_vertex1.set(vertex1);
		edgeShape.m_vertex2.set(vertex2);
		edgeShape.m_vertex3.set(vertex3);

	}

	@Override
	public ShapeInstance getCopy() {
		Box2dEdgeInstance lReturn = new Box2dEdgeInstance();

		lReturn.hasVertex0 = hasVertex0;
		lReturn.hasVertex3 = hasVertex3;

		lReturn.vertex0.set(vertex0);
		lReturn.vertex1.set(vertex1);
		lReturn.vertex2.set(vertex2);
		lReturn.vertex3.set(vertex3);

		return lReturn;
	}

}
