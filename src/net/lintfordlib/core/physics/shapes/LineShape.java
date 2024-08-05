package net.lintfordlib.core.physics.shapes;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class LineShape extends BaseShape {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private LineShape() {
		mShapeType = ShapeType.LineWidth;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void rebuildAABB(Transform t) {
		var minX = Float.MAX_VALUE;
		var minY = Float.MAX_VALUE;
		var maxX = -Float.MAX_VALUE;
		var maxY = -Float.MAX_VALUE;

		// @formatter:off
		var vertices = mTransformedVertices;
		
		var sex = vertices.get(1).x - vertices.get(0).x;
		var sey = vertices.get(1).y - vertices.get(0).y;
		final var len = Math.sqrt(sex*sex+sey*sey);
		sex /= len; // 0
		sey /= len; // 1
		
		final var l_x = vertices.get(0).x;
		final var l_y = vertices.get(0).y;
		
		final var l_x00 = l_x -sey*mHeight*.5f;
		final var l_y00 = l_y +sex*mHeight*.5f;
		
		final var l_x01 = l_x +sey*mHeight*.5f;
		final var l_y01 = l_y -sex*mHeight*.5f;
		
		final var r_x = vertices.get(1).x;
		final var r_y = vertices.get(1).y;
		
		final var r_x00 = r_x -sey*mHeight*.5f;
		final var r_y00 = r_y +sex*mHeight*.5f;
		
		final var r_x01 = r_x +sey*mHeight*.5f;
		final var r_y01 = r_y -sex*mHeight*.5f;
		
		if(l_x00 < minX) minX = l_x00;
		if(l_x01 < minX) minX = l_x01;
		if(r_x00 < minX) minX = r_x00;
		if(r_x01 < minX) minX = r_x01;
		
		if(l_x00 > maxX) maxX = l_x00;
		if(l_x01 > maxX) maxX = l_x01;
		if(r_x00 > maxX) maxX = r_x00;
		if(r_x01 > maxX) maxX = r_x01;
		
		if(l_y00 < minY) minY = l_y00;
		if(l_y01 < minY) minY = l_y01;
		if(r_y00 < minY) minY = r_y00;
		if(r_y01 < minY) minY = r_y01;
		
		if(l_y00 > maxY) maxY = l_y00;
		if(l_y01 > maxY) maxY = l_y01;
		if(r_y00 > maxY) maxY = r_y00;
		if(r_y01 > maxY) maxY = r_y01;
		// @formatter:on

		final var lWidth = maxX - minX;
		final var lHeight = maxY - minY;

		final var expand = mHeight;
		mAABB.set(minX - expand * .5f, minY - expand * .5f, lWidth + expand, lHeight + expand);
	}

	@Override
	public void computeMass() {
		mArea = mWidth * mHeight;
		mMass = mArea * mDensity;
		mInertia = (1.f / 12.f) * mMass * (mHeight * mHeight + mWidth * mWidth);
	}

	private void set(float unitPositionX, float unitPositionY, float unitWidth, float unitColDistToLine, float rotRadians) {
		mWidth = unitWidth;
		mHeight = unitColDistToLine;
		mRadius = (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight) * .5f;

		computeMass();

		// CCW winding order

		final var s = (float) Math.sin(rotRadians);
		final var c = (float) Math.cos(rotRadians);

		final var local_l = new Vector2f(-mWidth * .5f * c, -mWidth * .5f * s);
		final var local_r = new Vector2f(mWidth * .5f * c, mWidth * .5f * s);

		localCenter.x = (local_l.x + local_r.x) * .5f;
		localCenter.y = (local_l.y + local_r.y) * .5f;

		mLocalVertices.clear();
		mLocalVertices.add(local_l.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_r.add(unitPositionX, unitPositionY));

		mTransformedVertices.clear();
		mTransformedVertices.add(new Vector2f(local_l));
		mTransformedVertices.add(new Vector2f(local_r));
	}

	// --------------------------------------
	// Factory-Methods
	// --------------------------------------

	public static LineShape createLineShape(float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		return createLineShape(0.f, 0.f, unitWidth, unitHeight, rotRadians, density, restitution, staticFriction, dynamicFriction);
	}

	public static LineShape createLineShape(float unitLocalPosX, float unitLocalPosY, float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewLineShape = new LineShape();

		lNewLineShape.mStaticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewLineShape.mDynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewLineShape.mRestitution = MathHelper.clamp(restitution, 0f, 1f);
		lNewLineShape.mDensity = Math.abs(density);

		lNewLineShape.set(unitLocalPosX, unitLocalPosY, unitWidth, unitHeight, rotRadians);

		return lNewLineShape;
	}
}
