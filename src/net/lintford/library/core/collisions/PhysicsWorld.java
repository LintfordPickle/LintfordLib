package net.lintford.library.core.collisions;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private float mGravityX;
	private float mGravityY; // mps/s

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<RigidBody> mBodies = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<RigidBody> bodies() {
		return mBodies;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsWorld() {
		mGravityX = 0.f;
		mGravityY = 0.f;// 9.81f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void step(float time) {

		// 1. Movement
		final int lNumBodies = mBodies.size();
		for (int i = 0; i < lNumBodies - 1; i++) {
			mBodies.get(i).step(time, mGravityX, mGravityY);
		}

		// 2. Collision
		for (int i = 0; i < lNumBodies - 1; i++) {
			final var lBodyA = mBodies.get(i);

			for (int j = i + 1; j < lNumBodies; j++) {
				final var lBodyB = mBodies.get(j);

				final var result = collide(lBodyA, lBodyB);
				if (result != null && result.intersection) {
					lBodyA.x += -result.normal.x * result.depth / 2.f;
					lBodyA.y += -result.normal.y * result.depth / 2.f;

					lBodyB.x += result.normal.x * result.depth / 2.f;
					lBodyB.y += result.normal.y * result.depth / 2.f;
				}
			}
		}
	}

	public CollisionManifold collide(RigidBody bodyA, RigidBody bodyB) {
		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon) {
				return SAT.intersectsPolygons(bodyA.getTransformedVertices(), bodyB.getTransformedVertices());

			} else if (lShapeTypeB == ShapeType.Circle) {
				final var lColManifold = SAT.intersectsCirclePolygon(bodyB.x, bodyB.y, bodyB.radius, bodyA.getTransformedVertices());
				lColManifold.normal.x = lColManifold.normal.x;
				lColManifold.normal.y = lColManifold.normal.y;
				return lColManifold;
			}

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon) {
				final var lColManifold =  SAT.intersectsCirclePolygon(bodyA.x, bodyA.y, bodyA.radius, bodyB.getTransformedVertices());
				lColManifold.normal.x = -lColManifold.normal.x;
				lColManifold.normal.y = -lColManifold.normal.y;
				return lColManifold;

			} else if (lShapeTypeB == ShapeType.Circle) {
				return SAT.intersectsCircles(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.radius);

			}
		}

		return null;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addBody(RigidBody newBody) {
		mBodies.add(newBody);
	}

	public boolean removeBody(RigidBody body) {
		return mBodies.remove(body);
	}

	public RigidBody getBodyByIndex(int bodyIndex) {
		if (bodyIndex < 0 || bodyIndex >= mBodies.size())
			return null;

		return mBodies.get(bodyIndex);
	}

}
