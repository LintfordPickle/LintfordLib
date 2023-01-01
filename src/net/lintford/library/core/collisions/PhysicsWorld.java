package net.lintford.library.core.collisions;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.Vector2f;

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

	public int numBodies() {
		return mBodies.size();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsWorld() {
	}

	public PhysicsWorld(float gravityX, float gravityY) {
		mGravityX = gravityX;
		mGravityY = gravityY;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void step(float time) {

		// 1. Movement
		final int lNumBodies = mBodies.size();
		for (int i = 0; i < lNumBodies; i++) {
			mBodies.get(i).step(time, mGravityX, mGravityY);
		}

		// 2. Collision
		for (int i = 0; i < lNumBodies - 1; i++) {
			final var lBodyA = mBodies.get(i);

			for (int j = i + 1; j < lNumBodies; j++) {
				final var lBodyB = mBodies.get(j);

				if (lBodyA.isStatic() && lBodyB.isStatic())
					continue;

				final var result = collide(lBodyA, lBodyB);
				if (result != null && result.intersection) {

					if (lBodyA.isStatic()) {
						lBodyB.x += result.normal.x * result.depth;
						lBodyB.y += result.normal.y * result.depth;
					} else if (lBodyB.isStatic()) {
						lBodyA.x -= result.normal.x * result.depth;
						lBodyA.y -= result.normal.y * result.depth;
					} else {
						lBodyA.x += -result.normal.x * result.depth / 2.f;
						lBodyA.y += -result.normal.y * result.depth / 2.f;

						lBodyB.x += result.normal.x * result.depth / 2.f;
						lBodyB.y += result.normal.y * result.depth / 2.f;
					}

					resolveCollision(lBodyA, lBodyB, result);
				}
			}
		}
	}

	private CollisionManifold collide(RigidBody bodyA, RigidBody bodyB) {
		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (SAT.intersectsPolygons(bodyA.getTransformedVertices(), bodyB.getTransformedVertices(), SAT.tempResult)) {
					return SAT.tempResult;
				}
			} else if (lShapeTypeB == ShapeType.Circle) {
				if (SAT.intersectsCirclePolygon(bodyB.x, bodyB.y, bodyB.radius, bodyA.getTransformedVertices(), bodyB.x, bodyB.y, SAT.tempResult)) {
					return SAT.tempResult;
				}
			}

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (SAT.intersectsCirclePolygon(bodyA.x, bodyA.y, bodyA.radius, bodyB.getTransformedVertices(), bodyB.x, bodyB.y, SAT.tempResult)) {
					SAT.tempResult.normal.x = -SAT.tempResult.normal.x;
					SAT.tempResult.normal.y = -SAT.tempResult.normal.y;
					return SAT.tempResult;
				}

			} else if (lShapeTypeB == ShapeType.Circle) {
				if (SAT.intersectsCircles(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.radius, SAT.tempResult)) {
					return SAT.tempResult;
				}
			}
		}

		return null;
	}

	private void resolveCollision(RigidBody bodyA, RigidBody bodyB, CollisionManifold manifold) {
		final float relVelX = bodyB.vx - bodyA.vx;
		final float relVelY = bodyB.vy - bodyA.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, manifold.normal.x, manifold.normal.y);

		if (dotVelNor > 0.f)
			return;

		final float minRestitution = Math.min(bodyA.restitution(), bodyB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (bodyA.invMass() + bodyB.invMass());

		final float impulseX = j * manifold.normal.x;
		final float impulseY = j * manifold.normal.y;

		bodyA.vx -= impulseX * bodyA.invMass();
		bodyA.vy -= impulseY * bodyA.invMass();

		bodyB.vx += impulseX * bodyB.invMass();
		bodyB.vy += impulseY * bodyB.invMass();
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
