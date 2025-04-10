package net.lintfordlib.core.particles.particleemitters.shapes;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.particleemitters.ParticleEmitterInstance;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

// A2B traverses a path and ends when it reaches the end of the path
public class ParticleEmitterA2B extends ParticleEmitterShape {

	private class A2BShapeInstData {
		float startX;
		float startY;
		float startZ;
		float endX;
		float endY;
		float endZ;
		float lifeTime;
		float emitTimer;
		boolean isInUse;

		public void reset() {
			startX = 0.f;
			startY = 0.f;
			endX = 0.f;
			endY = 0.f;
			lifeTime = 0.f;
			emitTimer = 0.f;
			isInUse = false;
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5807058157231350398L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;
	public float heading;
	public float maxAngle;
	public float emitPeriod;

	private static final int MAX_POOL_SIZE = 4;
	private List<A2BShapeInstData> mEmitterInstPointsPool = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterA2B() {
		super(ParticleEmitterA2B.class.getSimpleName());

		for (int i = 0; i < MAX_POOL_SIZE; i++) {
			mEmitterInstPointsPool.add(new A2BShapeInstData());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float zDepth, float heading, float forceX, float forceY) {

		final var endX = worldX + (float) Math.cos(heading) * forceX;
		final var endY = worldX + (float) Math.sin(heading) * forceY;

		spawnNewPath(worldX, worldY, zDepth, endX, endY, zDepth);
	}

	private void spawnNewPath(float startX, float startY, float startZDepth, float endX, float endY, float endZDepth) {
		final var lNewInstData = getFreeShapeInstData();
		if (lNewInstData == null)
			return; // nothing free yet

		lNewInstData.startX = startX;
		lNewInstData.startY = startY;
		lNewInstData.startZ = startZDepth;
		lNewInstData.endX = endX;
		lNewInstData.endY = endY;
		lNewInstData.endZ = endZDepth;
		lNewInstData.lifeTime = 0.f;
		lNewInstData.isInUse = true;

	}

	@Override
	public void update(LintfordCore core, ParticleEmitterInstance inst) {
		super.update(core, inst);

		if (inst == null || inst.particleSystemInstance == null)
			return;

		final var dt = (float) core.gameTime().elapsedTimeMilli() / 1000.f;

		// TODO: for now, cheating on the life time
		final float endTime = 7.5f;

		for (int i = 0; i < MAX_POOL_SIZE; i++) {
			final var curInst = mEmitterInstPointsPool.get(i);
			if (curInst.isInUse) {

				curInst.emitTimer -= dt;
				curInst.lifeTime += dt;

				if (curInst.emitTimer <= 0.f) {
					final var xx = curInst.startX + curInst.endX * (curInst.lifeTime / endTime);
					final var yy = curInst.startY + curInst.endY * (curInst.lifeTime / endTime);
					final var zz = curInst.startZ + curInst.endZ * (curInst.lifeTime / endTime);

					inst.particleSystemInstance.spawnParticle(xx, yy, zz, 0.f, 0.f);
					curInst.emitTimer = emitPeriod;
				}

				if (curInst.lifeTime >= endTime)
					curInst.reset();

			}
		}
	}

	private A2BShapeInstData getFreeShapeInstData() {
		for (int i = 0; i < MAX_POOL_SIZE; i++) {
			if (!mEmitterInstPointsPool.get(i).isInUse)
				return mEmitterInstPointsPool.get(i);
		}
		return null;
	}

}
