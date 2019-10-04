package net.lintford.library.core.graphics.particles;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleColor;
import org.jbox2d.particle.ParticleDef;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleSystem;
import org.jbox2d.particle.ParticleType;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;

public class Box2dParticleSystemWrapper {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final String mParticleSystemName;

	private ParticleSystem mParticleSystem;
	private ParticleGroup mParticleGroup;

	private Box2dWorldController mBox2dWorldController;
	private ParticleDef mParticleDef;
	private ParticleGroupDef mParticleGroupDef;

	private String mTextureName;
	private String mTextureFilename;

	private boolean mIsinitialized;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getParticleCount() {
		return mParticleSystem.getParticleCount();
	}

	public ParticleColor[] particleColorBuffer() {
		return mParticleSystem.getParticleColorBuffer();
	}

	public Vec2[] particlePositionBuffer() {
		return mParticleSystem.getParticlePositionBuffer();
	}

	public boolean isinitialized() {
		return mIsinitialized;
	}

	public String name() {
		return mParticleSystemName;
	}

	/** Returns the internal texture name. */
	public String textureName() {
		return mTextureName;
	}

	/** Returns the filename of the texture. */
	public String textureFilename() {
		return mTextureFilename;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dParticleSystemWrapper(String pParticleSystemName) {
		mParticleSystemName = pParticleSystemName;

		mParticleDef = new ParticleDef();
		mParticleGroupDef = new ParticleGroupDef();

		mIsinitialized = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore pCore, int pEntityGroupID) {
		mBox2dWorldController = (Box2dWorldController) pCore.controllerManager().getControllerByNameRequired(Box2dWorldController.CONTROLLER_NAME, pEntityGroupID);

		mParticleSystem = new ParticleSystem(mBox2dWorldController.world());

		CircleShape shape = new CircleShape();
		shape.m_p.set(0, 30);
		shape.m_radius = 20;

		mParticleGroupDef = new ParticleGroupDef();
		mParticleGroupDef.flags = ParticleType.b2_waterParticle;
		mParticleGroupDef.shape = shape;

		mParticleGroup = mParticleSystem.createParticleGroup(mParticleGroupDef);
		
		mBox2dWorldController.world().setParticleRadius(0.35f);
		mBox2dWorldController.world().setParticleDamping(0.2f);

		mIsinitialized = true;

	}

	public void setParticleDef(ParticleDef pParticleDef) {
		mParticleDef = pParticleDef;

	}

	public void setTextureInfo(final String pTextureName, final String pTextureFilename) {
		mTextureName = pTextureName;
		mTextureFilename = pTextureFilename;

	}

	public void update(LintfordCore pCore) {
		// mParticleSystem.computeParticleCollisionEnergy();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link Box2dParticleSystemWrapper}. */
	public void spawnParticle(float pX, float pY, float pVelX, float pVelY, float pLife) {

		mParticleDef.position.x = pX;
		mParticleDef.position.y = pY;
		mParticleDef.velocity.x = pVelX * 200f;
		mParticleDef.velocity.y = pVelY;

		int pI = mParticleSystem.createParticle(mParticleDef);
		

	}

	public void reset() {

	}

}
