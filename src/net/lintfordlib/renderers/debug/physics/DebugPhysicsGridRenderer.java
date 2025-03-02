package net.lintfordlib.renderers.debug.physics;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.spatial.PhysicsHashGrid;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugPhysicsGridRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics Debug Hashgrid Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected PhysicsWorld mWorld;
	protected LineBatch mLineBatch;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mWorld != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DebugPhysicsGridRenderer(RendererManager rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		registerPassTypeIndex(RenderPass.RENDER_PASS_COLOR);

		mLineBatch = new LineBatch();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();
		final var lPhysicsController = (PhysicsController) lControllerManager.getControllerByNameRequired(PhysicsController.CONTROLLER_NAME, mEntityGroupUid);

		mWorld = lPhysicsController.world();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mLineBatch.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mLineBatch.unloadResources();
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		drawSpatialHashGridGrid(core, mWorld.grid());
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawSpatialHashGridGrid(LintfordCore core, PhysicsHashGrid<RigidBody> grid) {
		final var mBoundaryWidth = grid.boundaryWidthInUnits() * ConstantsPhysics.UnitsToPixels();
		final var mBoundaryHeight = grid.boundaryHeightInUnits() * ConstantsPhysics.UnitsToPixels();

		final var lHalfBW = mBoundaryWidth / 2.f;
		final var lHalfBH = mBoundaryHeight / 2.f;

		final var mNumTilesWide = grid.numTilesWide();
		final var mNumTilesHigh = grid.numTilesHigh();

		final var lTileSizeW = mBoundaryWidth / mNumTilesWide;
		final var lTileSizeH = mBoundaryHeight / mNumTilesHigh;

		final var lFontUnit = mRendererManager.uiTextFont();

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.begin(core.gameCamera());
		lFontUnit.begin(core.gameCamera());

		for (int xx = 0; xx < mNumTilesWide; xx++) {
			mLineBatch.draw(-lHalfBW + (xx * lTileSizeW), -lHalfBH, -lHalfBW + (xx * lTileSizeW), lHalfBH, .01f, 1f, 0f, 0f, .5f);

			for (int yy = 0; yy < mNumTilesHigh; yy++) {
				mLineBatch.draw(-lHalfBW, -lHalfBH + (yy * lTileSizeH), lHalfBW, -lHalfBH + (yy * lTileSizeH), .01f, 1f, 1f, 0f, 1.0f);

				final float xWorld = (-lHalfBW + xx * lTileSizeW + 1);
				final float yWorld = (-lHalfBH + yy * lTileSizeH + 1);

				final int lCellKey = grid.getCellKeyFromWorldPosition(xWorld, yWorld);
				lFontUnit.drawText(String.valueOf(lCellKey), -lHalfBW + (xx * lTileSizeW) + 2f, -lHalfBH + (yy * lTileSizeH) + 1f, .001f, 0.5f);

				final var lCellContents = grid.getCell(lCellKey);
				if (lCellContents != null && lCellContents.size() > 0) {
					final int lNumCellContent = lCellContents.size();
					for (int j = 0; j < lNumCellContent; j++) {
						final var entity = lCellContents.get(j);
						lFontUnit.drawText(String.valueOf(entity.uid), -lHalfBW + (xx * lTileSizeW) + 10f, -lHalfBH + (yy * lTileSizeH) + 10f + (j * 7), .001f, 0.5f);
					}
				}
			}
		}

		mLineBatch.end();
		lFontUnit.end();
	}
}
