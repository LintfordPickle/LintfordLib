package net.lintfordlib.screens;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.physics.IPhysicsControllerCallback;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.physics.PhysicsSettings;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.RigidBody.BodyType;
import net.lintfordlib.core.physics.shapes.PolygonShape;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.renderers.debug.physics.DebugPhysicsRenderer;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.screens.BaseGameScreen;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class DemoPhysicsScreen extends BaseGameScreen implements IPhysicsControllerCallback {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsWorld mPhysicsWorld;

	private RigidBody bodyA;
	private RigidBody bodyB;
	private RigidBody ground;

	private PhysicsController mPhysicsController;

	private DebugPhysicsRenderer mDebugPhysicsRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DemoPhysicsScreen(ScreenManager screenManager) {
		super(screenManager);

		mRendererManager.renderState().setCustomRenderPasses(new RenderPass(RenderPass.RENDER_PASS_COLOR));
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_ESCAPE, this)) {
			screenManager.createLoadingScreen(new LoadingScreen(screenManager, true, new DemoPhysicsScreen(screenManager)));
		}

	}

	@Override
	public void draw(LintfordCore core) {

		final var cfb = ColorConstants.CORNFLOWER_BLUE;

		GL11.glClearColor(cfb.r, cfb.g, cfb.b, 0.f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		super.draw(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void createData(DataManager dataManager) {

		final var lDensity = 1.f;
		final var lRestitution = .1f;
		final var lStaticFriction = .7f;
		final var lDynamicFriction = .3f;

		bodyA = new RigidBody(BodyType.Dynamic);
		bodyA.addShape(PolygonShape.createBoxShape(128.f * ConstantsPhysics.PixelsToUnits(), 64.f * ConstantsPhysics.PixelsToUnits(), 0.f, lDensity, lRestitution, lStaticFriction, lDynamicFriction));
		bodyA.moveTo(-50 * ConstantsPhysics.PixelsToUnits(), -20 * ConstantsPhysics.PixelsToUnits());
		bodyA.categoryBits(0x01);
		bodyA.maskBits(0x01 | 0x02);

		bodyB = new RigidBody(BodyType.Dynamic);
		bodyB.addShape(PolygonShape.createBoxShape(128.f * ConstantsPhysics.PixelsToUnits(), 64.f * ConstantsPhysics.PixelsToUnits(), 0.f, lDensity, lRestitution, lStaticFriction, lDynamicFriction));
		bodyB.moveTo(100 * ConstantsPhysics.PixelsToUnits(), 0 * ConstantsPhysics.PixelsToUnits());
		bodyB.categoryBits(0x01);
		bodyB.maskBits(0x01 | 0x02);

		ground = new RigidBody(BodyType.Static);
		ground.addShape(PolygonShape.createBoxShape(800.f * ConstantsPhysics.PixelsToUnits(), 16.f * ConstantsPhysics.PixelsToUnits(), 0.f, lDensity, lRestitution, lStaticFriction, lDynamicFriction));
		ground.moveTo(0 * ConstantsPhysics.PixelsToUnits(), 150 * ConstantsPhysics.PixelsToUnits());
		ground.categoryBits(0x02);
		ground.maskBits(0x01 | 0x02);

	}

	@Override
	protected void createControllers(ControllerManager controllerManager) {
		mPhysicsController = new PhysicsController(controllerManager, this, entityGroupUid());

	}

	@Override
	protected void initializeControllers(LintfordCore core) {
		mPhysicsController.initialize(core);
		mPhysicsController.simulationRunning(true);
	}

	@Override
	protected void createRenderers(LintfordCore core) {
		mDebugPhysicsRenderer = new DebugPhysicsRenderer(mRendererManager, entityGroupUid());

	}

	@Override
	protected void initializeRenderers(LintfordCore core) {
		mDebugPhysicsRenderer.initialize(core);

	}

	@Override
	protected void loadRendererResources(ResourceManager resourceManager) {
		mDebugPhysicsRenderer.loadResources(resourceManager);

	}

	// --------------------------------------
	// Callbacks
	// --------------------------------------

	@Override
	public PhysicsWorld createPhysicsWorld() {
		final var physicsSettings = new PhysicsSettings();
		physicsSettings.gravityX = 0.f;
		physicsSettings.gravityY = 0.987f;

		final var lPhysicsWorld = new PhysicsWorld(physicsSettings);

		// TODO: This isn't nice
		lPhysicsWorld.addBody(bodyA);
		lPhysicsWorld.addBody(bodyB);
		lPhysicsWorld.addBody(ground);

		return lPhysicsWorld;
	}

}
