package net.lintfordlib.core.physics.spatial;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.MathHelper;

public class PhysicsHashGrid<T extends PhysicsGridEntity> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int COLLISION_ENTITY_TYPE_NONE = 0;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private float mBoundaryWidthInUnits;
	private float mBoundaryHeightInUnits;

	private int mTilesWide;
	private int mTilesHigh;

	private int mQueryId;

	private List<List<T>> mCells;
	private List<Integer> mActiveCellKeys;
	private int mGridUidCounter;

	// SHARED between all queries!
	private final List<T> mReturnResultsList = new ArrayList<>(48);

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getNewGridUid() {
		return mGridUidCounter++;
	}

	public int getTotalCellCount() {
		return mCells.size();
	}

	public List<Integer> getActiveCellKeys() {
		return mActiveCellKeys;
	}

	public List<T> getCell(int cellKey) {
		if (cellKey < 0 || cellKey >= mCells.size())
			return null;

		return mCells.get(cellKey);
	}

	public float boundaryWidthInUnits() {
		return mBoundaryWidthInUnits;
	}

	public float boundaryHeightInUnits() {
		return mBoundaryHeightInUnits;
	}

	public int numTilesWide() {
		return mTilesWide;
	}

	public int numTilesHigh() {
		return mTilesHigh;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	/**
	 * Creates a spatial hash grid around 0,0 with the specified area dimensions and tile size
	 */
	public PhysicsHashGrid(float boundaryWidthInUnits, float boundaryHeightInUnits, int tilesWide, int tilesHigh) {
		mBoundaryWidthInUnits = boundaryWidthInUnits;
		mBoundaryHeightInUnits = boundaryHeightInUnits;

		mTilesWide = tilesWide;
		mTilesHigh = tilesHigh;
		final int totalCells = mTilesWide * mTilesHigh;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Created a HashGrid with " + totalCells + " total cells");

		mActiveCellKeys = new ArrayList<>();
		mCells = new ArrayList<>();
		for (int i = 0; i < totalCells; i++) {
			mCells.add(new ArrayList<>());
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public int getCellKeyFromWorldPosition(float worldX, float worldY) {
		final var lToUnits = ConstantsPhysics.PixelsToUnits();

		final int cellX = getColumnAtX(worldX * lToUnits);
		final int cellY = getRowAtY(worldY * lToUnits);
		return getKey(cellX, cellY);
	}

	public int getCellKeyFromUnitPosition(float unitX, float unitY) {
		final int cellX = getColumnAtX(unitX);
		final int cellY = getRowAtY(unitY);
		return getKey(cellX, cellY);
	}

	public void addEntity(T entity) {
		insertEntity(entity);
	}

	public void updateEntity(T entity) {
		if (entity == null)
			return;

		if (entity.isGridCacheOld(this) == false)
			return;

		removeEntity(entity);
		insertEntity(entity);
	}

	public void removeEntity(T entity) {
		if (entity.isOnGrid() == false)
			return;

		for (int xx = entity.minUnitX; xx <= entity.maxUnitX; xx++) {
			for (int yy = entity.minUnitY; yy <= entity.maxUnitY; yy++) {
				final var lCellKey = getKey(xx, yy);
				final var lCell = mCells.get(lCellKey); // O(1)
				lCell.remove(entity); // O(n)
			}
		}

		entity.minUnitX = -1;
		entity.minUnitY = -1;
		entity.maxUnitX = -1;
		entity.maxUnitY = -1;
	}

	public void findNearbyEntities(List<T> toFill, float centerX, float centerY, float radius) {
		toFill.addAll(findNearbyEntities(centerX, centerY, radius, COLLISION_ENTITY_TYPE_NONE));
	}

	public void findNearbyEntities(List<T> toFill, float centerX, float centerY, float radius, int entityTypeFlag) {
		toFill.addAll(findNearbyEntities(centerX, centerY, radius, entityTypeFlag));
	}

	// O(n)
	public List<T> findNearbyEntities(float centerX, float centerY, float radius) {
		return findNearbyEntities(centerX, centerY, radius, COLLISION_ENTITY_TYPE_NONE);
	}

	public List<T> findNearbyEntities(float centerX, float centerY, float radius, int entityTypeFlag) {
		mReturnResultsList.clear();

		final int minX = getColumnAtX((int) (centerX - radius));
		final int minY = getRowAtY((int) (centerY - radius));

		final int maxX = getColumnAtX((int) (centerX + radius));
		final int maxY = getRowAtY((int) (centerY + radius));

		mQueryId++;

		for (int xx = minX; xx <= maxX; xx++) {
			for (int yy = minY; yy <= maxY; yy++) {
				final int lCellKey = getKey(xx, yy);
				final var lCell = mCells.get(lCellKey);

				final int lNumEntitiesInCell = lCell.size();
				for (int j = 0; j < lNumEntitiesInCell; j++) { // O(n)
					final var lEntity = lCell.get(j);
					if (lEntity.queryId != mQueryId) {
						mReturnResultsList.add(lEntity); // O(1) - (O(n) if new array has to be created)
						lEntity.queryId = mQueryId;
					}
				}
			}
		}

		return mReturnResultsList;
	}

	// ---

	// O(n)
	private void insertEntity(T entity) {
		entity.fillEntityBounds(this);

		for (int xx = entity.minUnitX; xx <= entity.maxUnitX; xx++) {
			for (int yy = entity.minUnitY; yy <= entity.maxUnitY; yy++) {
				final var lCellKey = getKey(xx, yy);

				final var lCell = mCells.get(lCellKey); // O(1)
				if (!lCell.contains(entity)) // O(n)
					lCell.add(entity); // O(1) - (O(n) if new array has to be created)

				if (mActiveCellKeys.contains(lCellKey) == false) // O(n)
					mActiveCellKeys.add(lCellKey);
			}
		}
	}

	public int getKey(int xx, int yy) {
		return yy * mTilesWide + xx;
	}

	public int getColumnAtX(float x) {
		final float divisor = mBoundaryWidthInUnits / mTilesWide;
		return MathHelper.clampi((int) ((x + mBoundaryWidthInUnits / 2.f) / divisor), 0, mTilesWide - 1);
	}

	public int getRowAtY(float y) {
		final float divisor = mBoundaryHeightInUnits / mTilesHigh;
		return MathHelper.clampi((int) ((y + mBoundaryHeightInUnits / 2.f) / divisor), 0, mTilesHigh - 1);
	}

}
