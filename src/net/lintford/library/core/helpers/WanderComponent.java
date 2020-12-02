package net.lintford.library.core.helpers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;

public class WanderComponent {

	public final Vector2f centerPosition = new Vector2f();
	public final Vector2f maxDistFromCenter = new Vector2f();

	public final Vector2f wanderPosition = new Vector2f();
	public final Vector2f wanderDirection = new Vector2f();
	private float wanderAngle;

	public WanderComponent() {
		centerPosition.x = 0.f;
		centerPosition.y = 0.f;

	}

	public void update(LintfordCore pCore, float pTurnToFaceSpeed) {
		wanderDirection.x += MathHelper.lerp(-.25f, .25f, (float) RandomNumbers.RANDOM.nextFloat());
		wanderDirection.y += MathHelper.lerp(-.25f, .25f, (float) RandomNumbers.RANDOM.nextFloat());
		wanderDirection.nor();

		wanderAngle = MathHelper.turnToFace(wanderPosition.x, wanderPosition.y, wanderPosition.x + wanderDirection.x, wanderPosition.y + wanderDirection.y, wanderAngle, .025f);

		float distanceFromScreenCenter = wanderPosition.len();
		float maxDistanceFromScreenCenter = Math.min(maxDistFromCenter.x, maxDistFromCenter.y);

		float normalizedDistance = distanceFromScreenCenter / maxDistanceFromScreenCenter;

		float turnToCenterSpeed = 0.25f * normalizedDistance * normalizedDistance * .25f;

		wanderAngle = MathHelper.turnToFace(wanderPosition.x, wanderPosition.y, centerPosition.x, centerPosition.y, wanderAngle, turnToCenterSpeed);
	}

}
