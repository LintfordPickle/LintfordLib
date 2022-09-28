package net.lintford.library.core.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.box2d.instance.Box2dInstanceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.instances.IndexedPoolInstanceManager;

public class PObjectManager extends IndexedPoolInstanceManager<JBox2dEntityInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8661579477274556146L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Box2dInstanceManager mBox2dInstanceManager;
	private PObjectDefinitionRepository mPObjectRepository;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Box2dInstanceManager box2dInstanceManager() {
		return mBox2dInstanceManager;
	}

	public PObjectDefinitionRepository definitionRepository() {
		return mPObjectRepository;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectRepository = new PObjectDefinitionRepository();
		mBox2dInstanceManager = new Box2dInstanceManager();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {

	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance getNewInstanceBoxInstance(World box2dWorld, int bodyType, float worldWidth, float worldHeight) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(PObjectDefinitionRepository.POBJECT_BOX_NAME);
		final var lJBox2dInstance = getNewInstanceFromPObject(box2dWorld, lPObjectDefinition);

		lJBox2dInstance.setFixtureDimensions(PObjectDefinitionRepository.MAIN_FIXTURE_NAME, ConstantsPhysics.toUnits(worldWidth), ConstantsPhysics.toUnits(worldHeight));
		lJBox2dInstance.setBodyType(PObjectDefinitionRepository.MAIN_BODY_NAME, bodyType);

		return lJBox2dInstance;
	}

	public JBox2dEntityInstance getNewInstanceCircleInstance(World box2dWorld, int bodyType, float worldRadius) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(PObjectDefinitionRepository.POBJECT_CIRCLE_NAME);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObjectDefinition named " + PObjectDefinitionRepository.POBJECT_CIRCLE_NAME);
			return null;
		}

		final var lJBox2dInstance = getNewInstanceFromPObject(box2dWorld, lPObjectDefinition);
		lJBox2dInstance.setFixtureRadius(PObjectDefinitionRepository.MAIN_FIXTURE_NAME, ConstantsPhysics.toUnits(worldRadius));
		lJBox2dInstance.setBodyType(PObjectDefinitionRepository.MAIN_BODY_NAME, bodyType);

		return lJBox2dInstance;
	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World box2dWorld, String pObjectDefinitionName) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(pObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObjectDefinition named " + pObjectDefinitionName);
			return null;
		}

		return getNewInstanceFromPObject(box2dWorld, lPObjectDefinition);
	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World box2dWorld, PObjectDefinition pObjectDefinition) {
		final var lJBox2dEntityInstance = getFreePooledItem();
		if (lJBox2dEntityInstance == null)
			return null;

		lJBox2dEntityInstance.loadPObjectFromDefinition(mBox2dInstanceManager, pObjectDefinition);

		return lJBox2dEntityInstance;
	}

	@Override
	public void returnPooledItem(JBox2dEntityInstance returnItem) {
		if (returnItem == null)
			return;

		returnItem.returnPooledInstances(mBox2dInstanceManager);

		super.returnPooledItem(returnItem);
	}

	@Override
	protected JBox2dEntityInstance createPoolObjectInstance() {
		return new JBox2dEntityInstance(getNewInstanceUID());
	}
}
