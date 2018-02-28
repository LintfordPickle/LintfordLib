package net.lintford.library.core.geometry.spritegraph;

import java.io.Serializable;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInst implements Serializable {

	private static final long serialVersionUID = -5557875926084955431L;
	
	// --------------------------------------
	// Variables
	// --------------------------------------

	public SpriteGraphNodeInst rootNode;
	public String spriteGraphName;
	public String objectState;
	private boolean mIsLoaded;
	public float positionX;
	public float positionY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isFree() {
		return !(spriteGraphName != null && spriteGraphName.length() > 0);
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public SpriteGraphNodeInst getNode(String pName) {
		return rootNode.getNode(pName);
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphInst() {

	}

	public SpriteGraphInst(SpriteGraphDef pSpriteGraphDef) {
		this();

		spriteGraphName = pSpriteGraphDef.name;
		rootNode = new SpriteGraphNodeInst(pSpriteGraphDef.rootNode);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		rootNode.loadContent(pResourceManager);
		mIsLoaded = true;

	}

	public void unloadGLContent() {
		rootNode.unloadContent();
		mIsLoaded = false;

	}

	/**
	 * Updates the {@link SpriteGraphInst} and all its {@link SpriteGraphNodeInst} child parts, updating their positions and vertices.
	 */
	public void update(LintfordCore pCore) {
		if (!mIsLoaded)
			return;

		rootNode.setPosition(positionX, positionY);
		rootNode.update(pCore, this, null);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {
		unloadGLContent();

		// reset child nodes
		rootNode.reset();
		spriteGraphName = null;

	}

}
