package net.lintford.library.controllers.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;

public class Box2dWorldController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2dWorldController";

	public static final float UNITS_TO_PIXELS = 32f;
	public static final float PIXELS_TO_UNITS = 1f / UNITS_TO_PIXELS;

	public static final int CATEGORY_CHARACTER = 0b00000001;
	public static final int CATEGORY_WEAPON = 0b00000010;
	public static final int CATEGORY_ITEM = 0b00000100;
	public static final int CATEGORY_OBJECT = 0b00001000;
	public static final int CATEGORY_GROUND = 0b00010000;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceController mResourceController;
	private World mWorld;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	@Override
	public boolean isInitialised() {
		return mWorld != null;

	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dWorldController(ControllerManager pControllerManager, World pWorld, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mWorld = pWorld;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		mResourceController = (ResourceController) pCore.controllerManager().getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mWorld != null) {
			mWorld.step((float) pCore.time().elapseGameTimeSeconds(), 5, 6);

		}

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance getCharacterBox2dInstance(float pWidth, float pHeight) {
		if (!isInitialised()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new CharacterBox2dEntityInstance - Box2dWorldController is not initialised!");
			return null;

		}

		ResourceManager lResourceManager = mResourceController.resourceManager();

		JBox2dEntityInstance lPlayerJBox2dEntity = lResourceManager.pobjectManager().getNewInstanceFromPObject(mWorld, "box");
		lPlayerJBox2dEntity.mainBody().bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC;

		lPlayerJBox2dEntity.setFixtureDimensions("box", pWidth, pHeight);
		lPlayerJBox2dEntity.setFixtureCategory(CATEGORY_CHARACTER);
		lPlayerJBox2dEntity.setFixtureBitMask(CATEGORY_ITEM | CATEGORY_GROUND | CATEGORY_OBJECT);
		lPlayerJBox2dEntity.setFixtureIsSensor(false);

		return lPlayerJBox2dEntity;

	}

	public JBox2dEntityInstance getObjectBox2dInstance(float pWidth, float pHeight) {
		if (!isInitialised()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new ObjectBox2dEntityInstance - Box2dWorldController is not initialised!");
			return null;

		}

		ResourceManager lResourceManager = mResourceController.resourceManager();

		JBox2dEntityInstance lObjectJBox2dEntity = lResourceManager.pobjectManager().getNewInstanceFromPObject(mWorld, "box");

		lObjectJBox2dEntity.mainBody().bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC;
		lObjectJBox2dEntity.setFixtureDimensions("box", pWidth, pHeight);
		lObjectJBox2dEntity.setFixtureCategory(CATEGORY_OBJECT);
		lObjectJBox2dEntity.setFixtureBitMask(CATEGORY_CHARACTER);
		lObjectJBox2dEntity.setFixtureIsSensor(true);

		return lObjectJBox2dEntity;

	}

	public JBox2dEntityInstance getItemBox2dInstance(float pRadius) {
		if (!isInitialised()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot retreive a new ItemBox2dEntityInstance - Box2dWorldController is not initialised!");
			return null;

		}

		JBox2dEntityInstance lItemJBox2dEntity = mResourceController.resourceManager().pobjectManager().getNewInstanceFromPObject(mWorld, "circle");
		lItemJBox2dEntity.mainBody().bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC;

		// Before we load the physics, need to adjust the box2d body/fixture to match the shape
		// which in this case, means adjusting the raidus
		lItemJBox2dEntity.setFixtureRadius("circle", 8f);
		lItemJBox2dEntity.setFixtureCategory(CATEGORY_ITEM);
		lItemJBox2dEntity.setFixtureBitMask(CATEGORY_CHARACTER | CATEGORY_GROUND);
		lItemJBox2dEntity.setFixtureIsSensor(false);

		return lItemJBox2dEntity;

	}

}
