package net.lintfordlib.renderers.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorHashGridRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor HashGrid Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected EditorHashGridController mHashGridController;
	private boolean mRenderHashGrid;
	private boolean mFilterContents = false;
	private List<Integer> mFilterWhitelist = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean renderHashGrid() {
		return mRenderHashGrid;
	}

	public void renderHashGrid(boolean newValue) {
		mRenderHashGrid = newValue;
	}

	@Override
	public boolean isInitialized() {
		return mHashGridController != null;
	}

	public void insertIntoWhitelist(int newWhitelistItemUid) {
		if (mFilterWhitelist.contains(newWhitelistItemUid) == false) {
			mFilterWhitelist.add(newWhitelistItemUid);

			Collections.sort(mFilterWhitelist);
		}
	}

	public void removeFromWhitelist(int newWhitelistItemUid) {
		if (mFilterWhitelist.contains(newWhitelistItemUid))
			mFilterWhitelist.remove(newWhitelistItemUid);
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorHashGridRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();
		mHashGridController = (EditorHashGridController) lControllerManager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);

		// TOOD: Formalize this
		mFilterWhitelist.add(1); // GridEntityType.GRID_ENTITY_TYPE_PHYSICS_OBJECTS
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (mRenderHashGrid)
			drawSpatialHashGridGrid(core);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawSpatialHashGridGrid(LintfordCore core) {

		final var lLineBatch = core.sharedResources().uiLineBatch();
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lHashGrid = mHashGridController.hashGrid();
		final var mBoundaryWidth = lHashGrid.boundaryWidth();
		final var mBoundaryHeight = lHashGrid.boundaryHeight();

		final float lHalfBW = mBoundaryWidth * .5f;
		final float lHalfBH = mBoundaryHeight * .5f;

		final int mNumTilesWide = lHashGrid.numTilesWide();
		final int mNumTilesHigh = lHashGrid.numTilesHigh();

		final float lTileSizeW = mBoundaryWidth / (float) mNumTilesWide;
		final float lTileSizeH = mBoundaryHeight / (float) mNumTilesHigh;

		lLineBatch.lineType(GL11.GL_LINES);
		lLineBatch.begin(core.gameCamera());
		lFontUnit.begin(core.gameCamera());

		final var lFontSize = 1.f;
		final var lFontSpacing = lFontUnit.fontHeight();

		// FIXME: frustum culling

		for (int xx = 0; xx < mNumTilesWide; xx++) {
			lLineBatch.draw(-lHalfBW + (xx * lTileSizeW), -lHalfBH, -lHalfBW + (xx * lTileSizeW), lHalfBH, .01f, 1f, 0f, 0f, .75f);

			for (int yy = 0; yy < mNumTilesHigh; yy++) {
				lLineBatch.draw(-lHalfBW, -lHalfBH + (yy * lTileSizeH), lHalfBW, -lHalfBH + (yy * lTileSizeH), .01f, 1f, 1f, 0f, .75f);

				final int lCellKey = lHashGrid.getCellKeyFromWorldPosition(-lHalfBW + (xx * lTileSizeW), -lHalfBH + (yy * lTileSizeH));
				lFontUnit.drawText(String.valueOf(lCellKey), -lHalfBW + (xx * lTileSizeW) + 2f, -lHalfBH + (yy * lTileSizeH) + 1f, .001f, lFontSize);

				final var lCellContents = lHashGrid.getCell(lCellKey);
				if (lCellContents != null && lCellContents.size() > 0) {
					final int lNumCellContent = lCellContents.size();

					for (int j = 0; j < lNumCellContent; j++) {
						final var entity = lCellContents.get(j);

						if (mFilterContents) {
							if (Collections.binarySearch(mFilterWhitelist, entity.gridEntityType) > -1) {
								lFontUnit.drawText(String.valueOf(entity.uid), -lHalfBW + (xx * lTileSizeW) + 10f, -lHalfBH + (yy * lTileSizeH) + lFontSpacing + (j * lFontSpacing), .001f, lFontSize);
							}

						} else {
							lFontUnit.drawText(String.valueOf(entity.uid), -lHalfBW + (xx * lTileSizeW) + 10f, -lHalfBH + (yy * lTileSizeH) + lFontSpacing + (j * lFontSpacing), .001f, lFontSize);
						}
					}
				}
			}
		}

		lLineBatch.end();
		lFontUnit.end();
	}
}
