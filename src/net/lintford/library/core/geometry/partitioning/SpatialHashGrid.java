package net.lintford.library.core.geometry.partitioning;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.maths.MathHelper;

public class SpatialHashGrid<T extends GridEntity> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int COLLISION_ENTITY_TYPE_NONE = 0;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mBoundaryWidth;
	private int mBoundaryHeight;

	private int mTilesWide;
	private int mTilesHigh;

	private int mQueryId;

	private List<List<T>> mCells;
	private int mGridUidCounter;

	// SHARED between all queries!
	private final List<T> mReturnResultsList = new ArrayList<>(48);

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getNewGridUid() {
		return mGridUidCounter++;
	}

	public List<T> getCell(int cellKey) {
		if (cellKey < 0 || cellKey >= mCells.size())
			return null;

		return mCells.get(cellKey);
	}

	public int boundaryWidth() {
		return mBoundaryWidth;
	}

	public int boundaryHeight() {
		return mBoundaryHeight;
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
	public SpatialHashGrid(int boundaryWidth, int boundaryHeight, int tilesWide, int tilesHigh) {
		mBoundaryWidth = boundaryWidth;
		mBoundaryHeight = boundaryHeight;

		mTilesWide = tilesWide;
		mTilesHigh = tilesHigh;
		final int totalCells = mTilesWide * mTilesHigh;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Created a HashGrid with " + totalCells + " total cells");

		mCells = new ArrayList<>();
		for (int i = 0; i < totalCells; i++) {
			mCells.add(new ArrayList<>());
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public int getCellKeyFromWorldPosition(int worldX, int worldY) {
		final int cellX = getCellIndexX(worldX);
		final int cellY = getCellIndexY(worldY);
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

		for (int xx = entity.minX; xx <= entity.maxX; xx++) {
			for (int yy = entity.minY; yy <= entity.maxY; yy++) {
				final var lCellKey = getKey(xx, yy);
				final var lCell = mCells.get(lCellKey); // O(1)
				lCell.remove(entity); // O(n)
			}
		}

		entity.minX = -1;
		entity.minY = -1;
		entity.maxX = -1;
		entity.maxY = -1;
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

		final int minX = getCellIndexX((int) (centerX - radius));
		final int minY = getCellIndexY((int) (centerY - radius));

		final int maxX = getCellIndexX((int) (centerX + radius));
		final int maxY = getCellIndexY((int) (centerY + radius));

		mQueryId++;

		for (int xx = minX; xx <= maxX; xx++) {
			for (int yy = minY; yy <= maxY; yy++) {
				final int lCellKey = getKey(xx, yy);
				final var lCell = mCells.get(lCellKey);

				final int lNumEntitiesInCell = lCell.size();
				for (int j = 0; j < lNumEntitiesInCell; j++) { // O(n)
					final var lEntity = lCell.get(j);

					final var isFilterSet = entityTypeFlag != COLLISION_ENTITY_TYPE_NONE;
					if (isFilterSet) {
						final var doesEntityPass = (entityTypeFlag & lEntity.gridEntityType) == lEntity.gridEntityType;
						if (!doesEntityPass)
							continue;

					}

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

		for (int xx = entity.minX; xx <= entity.maxX; xx++) {
			for (int yy = entity.minY; yy <= entity.maxY; yy++) {
				final var lCellKey = getKey(xx, yy);

				final var lCell = mCells.get(lCellKey); // O(1)
				if (!lCell.contains(entity)) // O(n)
					lCell.add(entity); // O(1) - (O(n) if new array has to be created)
			}
		}
	}

	public int getKey(int xx, int yy) {
		return yy * mTilesWide + xx;
	}

	public int getCellIndexX(int x) {
		return MathHelper.clampi((x + mBoundaryWidth / 2) / (mBoundaryWidth / mTilesWide), 0, mTilesWide - 1);
	}

	public int getCellIndexY(int y) {
		return MathHelper.clampi((y + mBoundaryHeight / 2) / (mBoundaryHeight / mTilesHigh), 0, mTilesHigh - 1);
	}

}
