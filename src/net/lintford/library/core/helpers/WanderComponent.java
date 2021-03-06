package net.lintford.library.core.helpers;

import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;

public class WanderComponent {

	public final Vector2f centerPosition = new Vector2f();
	public final Vector2f maxDistFromCenter = new Vector2f();

	public final Vector2f wanderPosition = new Vector2f();
	private final Vector2f wanderDirection = new Vector2f();
	private float wanderAngle;

	public WanderComponent() {
		centerPosition.x = 0.f;
		centerPosition.y = 0.f;

	}

	public float update(float pCurrentHeading, float pTurnToFaceSpeed) {
		wanderAngle = pCurrentHeading;

		// the first step of the wander behavior is to use the random number generator to offset the current wanderDirection by some random amount.
		// .25 is a bit of a magic number, but it controls how erratic the wander behavior is. Larger numbers will make the characters "wobble" more,
		// smaller numbers will make them more stable. we want just enough wobbliness to be interesting without looking odd.
		wanderDirection.x = (float) Math.cos(wanderAngle) + MathHelper.lerp(-.25f, .25f, (float) RandomNumbers.RANDOM.nextFloat());
		wanderDirection.y = (float) Math.sin(wanderAngle) + MathHelper.lerp(-.25f, .25f, (float) RandomNumbers.RANDOM.nextFloat());
		wanderDirection.nor();

		// ... and then turn to face in the wander direction. We don't turn at the maximum turning speed, but at 15% of it. Again, this is a bit of a magic
		// number: it works well for this sample, but feel free to tweak it.
		wanderAngle = MathHelper.turnToFace(wanderPosition.x, wanderPosition.y, wanderPosition.x + wanderDirection.x, wanderPosition.y + wanderDirection.y, wanderAngle, .35f * pTurnToFaceSpeed);

		// Here we are creating a curve that we can apply to the turnSpeed. This curve will make it so that if we are close to the center of the screen,
		// we won't turn very much. However, the further we are from the screen center, the more we turn. At most, we will turn at 30% of our maximum
		// turn speed. This too is a "magic number" which works well for the sample. Feel free to play around with this one as well: smaller values will make
		// the characters explore further away from the center, but they may get stuck on the walls. Larger numbers will hold the characters to center of
		// the screen. If the number is too large, the characters may end up "orbiting" the center.
		float distanceFromScreenCenter = Vector2f.len(centerPosition.x, centerPosition.y, wanderPosition.x, wanderPosition.y);
		float maxDistanceFromScreenCenter = Math.min(maxDistFromCenter.x, maxDistFromCenter.y);

		float normalizedDistance = distanceFromScreenCenter / maxDistanceFromScreenCenter;

		float turnToCenterSpeed = .05f * normalizedDistance * normalizedDistance * pTurnToFaceSpeed;

		wanderAngle = MathHelper.turnToFace(wanderPosition.x, wanderPosition.y, centerPosition.x, centerPosition.y, wanderAngle, turnToCenterSpeed);
		return wanderAngle;

	}

}
