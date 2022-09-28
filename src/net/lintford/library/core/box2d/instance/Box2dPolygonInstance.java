package net.lintford.library.core.box2d.instance;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import net.lintford.library.core.entity.BaseInstanceData;

public class Box2dPolygonInstance extends BaseInstanceData implements ShapeInstance {

	private static final long serialVersionUID = -2122600675590005803L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient PolygonShape polygonShape;

	public int vertexCount;
	public Vec2[] vertices;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dPolygonInstance() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Shape shape() {
		return polygonShape;
	}

	public void savePhysics() {
		if (polygonShape == null)
			return;

		vertexCount = polygonShape.getVertexCount();
		vertices = polygonShape.getVertices();
	}

	public void loadPhysics() {
		polygonShape = new PolygonShape();
		polygonShape.set(vertices, vertexCount);
	}

	@Override
	public ShapeInstance getCopy() {
		final var lReturn = new Box2dPolygonInstance();
		lReturn.vertexCount = vertexCount;

		lReturn.vertices = new Vec2[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			lReturn.vertices[i] = new Vec2(vertices[i]);
		}

		return lReturn;
	}
}
