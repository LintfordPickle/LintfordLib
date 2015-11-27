package net.ld.library.core.particles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatchColor;
import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.graphics.sprites.Sprite;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.GameLoaderPart;

public class ParticleManager implements GameLoaderPart {

	// =============================================
	// Constants
	// =============================================

	private static int MAX_PARTICLES = 200;

	// FIXME: use a correct z-depth
	private static float Z_DEPTH = 0.7f;

	// =============================================
	// Variables
	// =============================================

	private String mParticleTextureName;
	private String mParticleTextureFilename;
	private List<Particle> mParticles;
	private List<IParticleReactor> mReactors;
	private List<IParticleInitialiser> mInitialisers;
	private List<IParticleModifier> mModifiers;
	private final int mCapacity;

	private SpriteBatchColor mSpriteBatch;
	private boolean mIsLoaded;
	private Sprite mShadowSprite = new Sprite(0, 80, 32, 20);
	public float shadowHeight = 0f; 
	public boolean shadows = true;

	// =============================================
	// Properties
	// =============================================

	public List<Particle> particles() {
		return mParticles;
	}

	// =============================================
	// Constructor
	// =============================================

	public ParticleManager() {
		this(MAX_PARTICLES);
	}

	public ParticleManager(int pCapacity) {

		mParticles = new ArrayList<>();
		mInitialisers = new ArrayList<>();
		mModifiers = new ArrayList<>();
		mReactors = new ArrayList<>();

		MAX_PARTICLES = pCapacity;
		mCapacity = MAX_PARTICLES;

		mSpriteBatch = new SpriteBatchColor();

		mIsLoaded = false;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initilaise(String pTextureName, String pParticleTextureFilename) {
		mParticleTextureName = pTextureName;
		mParticleTextureFilename = pParticleTextureFilename;

		// Create a pool of particles
		for (int i = 0; i < mCapacity; i++) {
			mParticles.add(new Particle());
		}
	}

	@Override
	public void loadContent(ResourceManager pResourceManager) {

		TextureManager.textureManager().loadTexture(mParticleTextureName, mParticleTextureFilename, GL11.GL_NEAREST);

		mSpriteBatch.loadContent(pResourceManager);

		mIsLoaded = true;
	}

	public void update(GameTime pGameTime) {
		final int lNumModifiers = mModifiers.size();
		for (int i = 0; i < mCapacity; i++) {
			Particle lCurParticle = mParticles.get(i);

			if (lCurParticle.isFree())
				continue;

			//
			lCurParticle.mLife -= pGameTime.elapseGameTime();

			if (lCurParticle.mLife <= 0) {

				final int lNumReactors = mReactors.size();
				for (int j = 0; j < lNumReactors; j++) {
					mReactors.get(j).onParticleDeath(lCurParticle);
				}

				lCurParticle.reset();
				continue;
			}

			lCurParticle.sprite.update(pGameTime);

			for (int j = 0; j < lNumModifiers; j++) {
				mModifiers.get(j).update(mParticles.get(i), pGameTime);
			}
		}
	}

	public void draw(RenderState pRenderState) {

		mSpriteBatch.begin(pRenderState.gameCamera());
		for (int i = 0; i < mCapacity; i++) {
			Particle lCurParticle = mParticles.get(i);

			if (lCurParticle.isFree())
				continue;

			Vector2f lNewVector = new Vector2f(lCurParticle.position);
			lNewVector.y += shadowHeight;
			if(shadows){
			mSpriteBatch.draw(lNewVector, Z_DEPTH, lCurParticle.color, 0f, TextureManager.textureManager().getTexture(mParticleTextureName), mShadowSprite);
			}
			mSpriteBatch.draw(lCurParticle.position, Z_DEPTH, lCurParticle.color, lCurParticle.angle, TextureManager.textureManager().getTexture(mParticleTextureName), lCurParticle.sprite.getSprite());

		}
		mSpriteBatch.end();
	}

	// =============================================
	// Methods
	// =============================================

	public void addParticle(float pX, float pY, float pVelX, float pVelY, ISprite pSprite) {
		for (int i = 0; i < mCapacity; i++) {
			if (!mParticles.get(i).isFree())
				continue;

			// TODO: Replace the width and height of particles with something proper later
			mParticles.get(i).assignParticle(pX, pY, pVelX, pVelY, pSprite);

			final int lNumInitialisers = mInitialisers.size();
			for (int j = 0; j < lNumInitialisers; j++) {
				mInitialisers.get(j).initialise(mParticles.get(i));
			}

			final int lNumReactors = mReactors.size();
			for (int j = 0; j < lNumReactors; j++) {
				mReactors.get(j).onParticleSpawn(mParticles.get(i));
			}

			break;
		}
	}

	public void addInitialiser(IParticleInitialiser pInitialiser) {
		if (pInitialiser == null)
			return;

		if (!mInitialisers.contains(pInitialiser)) {
			mInitialisers.add(pInitialiser);
		}
	}

	public void addModifier(IParticleModifier pModifier) {
		if (pModifier == null)
			return;

		if (!mModifiers.contains(pModifier)) {
			mModifiers.add(pModifier);
		}
	}

	public void addReactor(IParticleReactor pReactor) {
		if (pReactor == null)
			return;

		if (!mReactors.contains(pReactor)) {
			mReactors.add(pReactor);
		}
	}

	@Override
	public String getTitle() {
		return "Particle Systems";
	}

	@Override
	public boolean isLoaded() {
		return mIsLoaded;
	}

}
