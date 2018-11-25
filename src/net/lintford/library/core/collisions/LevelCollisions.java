package net.lintford.library.core.collisions;

public class LevelCollisions {

//	public static final float SKIN_WIDTH = 0.025f;
//
//	//
//	// Poly Collision Checks
//	//
//
//	// TODO: Polygonal collision checks against the level
//
//	//
//	// Circle Collision Checks
//	//
//
//	static public boolean hasLeftCollision(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasLeftCollision(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasLeftCollision(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float RADIUS = pEntity.radius;
//		final float oblx = pEntity.oldX - RADIUS;
//		final float obly = pEntity.oldY + RADIUS - SKIN_WIDTH;
//
//		final float nblx = pEntity.x - RADIUS;
//		final float nbly = pEntity.y + RADIUS - SKIN_WIDTH;
//
//		// Calculate the horizontal span and dist
//		int endX = (int) (nblx / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begX = Math.max((int) (oblx / ConstantsTable.BLOCK_SIZE_PIXELS), endX);
//		int dist = Math.max(Math.abs(endX - begX), 1);
//
//		// Horizontally iterate over the range of the entity, from right to left
//		for (int tileIndexX = begX; tileIndexX >= endX; tileIndexX--) {
//			float bly = MathHelper.lerp(nbly, obly, (float) Math.abs(endX - tileIndexX) / dist);
//			float tly = bly - RADIUS * 2;
//
//			// Vertically check each block position at this point on the horizontal line
//			for (float checkedTileY = tly;; checkedTileY += ConstantsTable.BLOCK_SIZE_PIXELS) {
//				checkedTileY = Math.min(checkedTileY, bly);
//
//				int tileCoordY = (int) (checkedTileY / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCX = (int) (pEntity.x / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (playerCX > (tileIndexX + pOffset) && hasLevelCollision(pLevel, tileIndexX + pOffset, tileCoordY)) {
//
//					if (pPhysicsState != null) {
//						// Store the result (the tile's right hand edge) in the physics state
//						pPhysicsState.collisionX = (tileIndexX + 1) * ConstantsTable.BLOCK_SIZE_PIXELS + SKIN_WIDTH;
//						pPhysicsState.mPushesLeftWall = true;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileY >= bly)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasRightCollision(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasRightCollision(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasRightCollision(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float RADIUS = pEntity.radius;
//		final float obrx = pEntity.oldX + RADIUS;
//		final float obry = pEntity.oldY + RADIUS - SKIN_WIDTH;
//
//		final float nbrx = pEntity.x + RADIUS;
//		final float nbry = pEntity.y + RADIUS - SKIN_WIDTH;
//
//		// Calculate the horizontal span and dist
//		int endX = (int) (nbrx / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begX = Math.min((int) (obrx / ConstantsTable.BLOCK_SIZE_PIXELS), endX);
//		int dist = Math.max(Math.abs(endX - begX), 1);
//
//		// Horizontally iterate over the range of the entity, from left to right
//		for (int tileIndexX = begX; tileIndexX <= endX; tileIndexX++) {
//			float bry = MathHelper.lerp(nbry, obry, (float) Math.abs(endX - tileIndexX) / dist);
//			float trry = bry - RADIUS * 2;
//
//			// Vertically check each block position at this point on the horizontal line
//			for (float checkedTileY = trry;; checkedTileY += ConstantsTable.BLOCK_SIZE_PIXELS) {
//				checkedTileY = Math.min(checkedTileY, bry);
//
//				int tileCoordY = (int) (checkedTileY / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCX = (int) (pEntity.x / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (playerCX < (tileIndexX + pOffset) && hasLevelCollision(pLevel, tileIndexX + pOffset, tileCoordY)) {
//					if (pPhysicsState != null) {
//						// Store the result (the tile's left hand edge) in the physics state
//						pPhysicsState.collisionX = tileIndexX * ConstantsTable.BLOCK_SIZE_PIXELS - SKIN_WIDTH;
//						pPhysicsState.mPushesRightWall = true;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileY >= bry)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasCeiling(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasCeiling(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasCeiling(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float RADIUS = pEntity.radius;
//		final float otlx = pEntity.oldX - RADIUS;
//		final float otly = pEntity.oldY - RADIUS;
//
//		final float ntlx = pEntity.x - RADIUS;
//		final float ntly = pEntity.y - RADIUS;
//
//		// Calculate the vertical span and dist
//		int endY = (int) (ntly / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begY = Math.max((int) (otly / ConstantsTable.BLOCK_SIZE_PIXELS), endY);
//		int dist = Math.max(Math.abs(endY - begY), 1);
//
//		// Vertically iterate over the range of the entity between oldBottomLeft nad newBottomLeft
//		for (int tileIndexY = begY; tileIndexY >= endY; tileIndexY--) {
//			float tlx = MathHelper.lerp(ntlx, otlx, (float) Math.abs(endY - tileIndexY) / dist);
//			float trx = tlx + RADIUS * 2;
//
//			// Horizontal check at this vertical position at each block position
//			for (float checkedTileX = tlx;; checkedTileX += ConstantsTable.BLOCK_SIZE_PIXELS) {
//
//				checkedTileX = Math.min(checkedTileX, trx);
//
//				int tileCoordX = (int) (checkedTileX / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (hasLevelCollision(pLevel, tileCoordX, tileIndexY + pOffset)) {
//					if (pPhysicsState != null) {
//						// Calculate the tile's top position
//						pPhysicsState.collisionY = (tileIndexY + 1) * ConstantsTable.BLOCK_SIZE_PIXELS;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileX >= trx)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasGround(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasGround(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasGround(IGridCollider pLevel, CircleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float RADIUS = pEntity.radius;
//		final float oblx = pEntity.oldX - RADIUS;
//		final float obly = pEntity.oldY + RADIUS;
//
//		final float nblx = pEntity.x - RADIUS;
//		final float nbly = pEntity.y + RADIUS;
//
//		// Calculate the vertical span and dist
//		int endY = (int) (nbly / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begY = Math.min((int) (obly / ConstantsTable.BLOCK_SIZE_PIXELS) - 1, endY);
//		int dist = Math.max(Math.abs(endY - begY), 1);
//
//		// sanity check that the distance between begY and endY isn't something stupid (like when I messed the physics up)
//
//		// Vertically iterate over the range of the entity between oldBottomLeft nad newBottomLeft
//		for (int tileIndexY = begY; tileIndexY <= endY; tileIndexY++) {
//			float blx = MathHelper.lerp(nblx, oblx, (float) Math.abs(endY - tileIndexY) / dist);
//			float brx = blx + RADIUS * 2;
//
//			// Horizontal check at this vertical position at each block position
//			for (float checkedTileX = blx;; checkedTileX += ConstantsTable.BLOCK_SIZE_PIXELS) {
//
//				checkedTileX = Math.min(checkedTileX, brx);
//
//				int tileCoordX = (int) (checkedTileX / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCY = (int) ((pEntity.y + RADIUS) / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if ((tileIndexY + pOffset) >= playerCY && hasLevelCollision(pLevel, tileCoordX, tileIndexY + pOffset)) {
//					if (pPhysicsState != null) {
//						// Calculate the tile's top position
//						pPhysicsState.collisionY = tileIndexY * ConstantsTable.BLOCK_SIZE_PIXELS;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileX >= brx)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	//
//	// Rectangle Collision Checks
//	//
//
//	static public boolean hasLeftCollision(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasLeftCollision(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasLeftCollision(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//
//		final float oblx = pEntity.oldX - pEntity.width / 2;
//		final float obly = pEntity.oldY + pEntity.height / 2 - SKIN_WIDTH;
//
//		final float nblx = pEntity.x - pEntity.width / 2;
//		final float nbly = pEntity.y + pEntity.height / 2 - SKIN_WIDTH;
//
//		// Calculate the horizontal span and dist
//		int endX = (int) (nblx / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begX = Math.max((int) (oblx / ConstantsTable.BLOCK_SIZE_PIXELS), endX);
//		int dist = Math.max(Math.abs(endX - begX), 1);
//
//		// Horizontally iterate over the range of the entity, from right to left
//		for (int tileIndexX = begX; tileIndexX >= endX; tileIndexX--) {
//			float bly = MathHelper.lerp(nbly, obly, (float) Math.abs(endX - tileIndexX) / dist);
//			float tly = bly - pEntity.height;
//
//			// Vertically check each block position at this point on the horizontal line
//			for (float checkedTileY = tly;; checkedTileY += ConstantsTable.BLOCK_SIZE_PIXELS) {
//				checkedTileY = Math.min(checkedTileY, bly);
//
//				int tileCoordY = (int) (checkedTileY / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCX = (int) (pEntity.x / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (playerCX > (tileIndexX + pOffset) && hasLevelCollision(pLevel, tileIndexX + pOffset, tileCoordY)) {
//
//					if (pPhysicsState != null) {
//						// Store the result (the tile's right hand edge) in the physics state
//						pPhysicsState.collisionX = (tileIndexX + 1) * ConstantsTable.BLOCK_SIZE_PIXELS + SKIN_WIDTH;
//						pPhysicsState.mPushesLeftWall = true;
//
//						if (checkedTileY - SKIN_WIDTH > bly - ConstantsTable.BLOCK_SIZE_PIXELS)
//							pPhysicsState.mPushesLeftWallFoot = true;
//
//						if (checkedTileY - SKIN_WIDTH > bly - ConstantsTable.BLOCK_SIZE_PIXELS * 2)
//							pPhysicsState.mPushesLeftWallWaist = true;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileY >= bly)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasRightCollision(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasRightCollision(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasRightCollision(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float obrx = pEntity.oldX + pEntity.width / 2;
//		final float obry = pEntity.oldY + pEntity.height / 2 - SKIN_WIDTH;
//
//		final float nbrx = pEntity.x + pEntity.width / 2;
//		final float nbry = pEntity.y + pEntity.height / 2 - SKIN_WIDTH;
//
//		// Calculate the horizontal span and dist
//		int endX = (int) (nbrx / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begX = Math.min((int) (obrx / ConstantsTable.BLOCK_SIZE_PIXELS), endX);
//		int dist = Math.max(Math.abs(endX - begX), 1);
//
//		// Horizontally iterate over the range of the entity, from left to right
//		for (int tileIndexX = begX; tileIndexX <= endX; tileIndexX++) {
//			float bry = MathHelper.lerp(nbry, obry, (float) Math.abs(endX - tileIndexX) / dist);
//			float trry = bry - pEntity.height;
//
//			// Vertically check each block position at this point on the horizontal line
//			for (float checkedTileY = trry;; checkedTileY += ConstantsTable.BLOCK_SIZE_PIXELS) {
//				checkedTileY = Math.min(checkedTileY, bry);
//
//				int tileCoordY = (int) (checkedTileY / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCX = (int) (pEntity.x / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (playerCX < (tileIndexX + pOffset) && hasLevelCollision(pLevel, tileIndexX + pOffset, tileCoordY)) {
//					if (pPhysicsState != null) {
//						// Store the result (the tile's left hand edge) in the physics state
//						pPhysicsState.collisionX = tileIndexX * ConstantsTable.BLOCK_SIZE_PIXELS - SKIN_WIDTH;
//						pPhysicsState.mPushesRightWall = true;
//
//						if (checkedTileY - SKIN_WIDTH > bry - ConstantsTable.BLOCK_SIZE_PIXELS)
//							pPhysicsState.mPushesRightWallFoot = true;
//
//						if (checkedTileY - SKIN_WIDTH > bry - ConstantsTable.BLOCK_SIZE_PIXELS * 2)
//							pPhysicsState.mPushesRightWallWaist = true;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileY >= bry)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasCeiling(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState) {
//		return hasCeiling(pLevel, pEntity, pPhysicsState, 0);
//
//	}
//
//	static public boolean hasCeiling(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState, int pOffset) {
//		final float otlx = pEntity.oldX - pEntity.width / 2;
//		final float otly = pEntity.oldY - pEntity.height / 2;
//
//		final float ntlx = pEntity.x - pEntity.width / 2;
//		final float ntly = pEntity.y - pEntity.height / 2;
//
//		// Calculate the vertical span and dist
//		int endY = (int) (ntly / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begY = Math.max((int) (otly / ConstantsTable.BLOCK_SIZE_PIXELS), endY);
//		int dist = Math.max(Math.abs(endY - begY), 1);
//
//		// Vertically iterate over the range of the entity between oldBottomLeft nad newBottomLeft
//		for (int tileIndexY = begY; tileIndexY >= endY; tileIndexY--) {
//			float tlx = MathHelper.lerp(ntlx, otlx, (float) Math.abs(endY - tileIndexY) / dist);
//			float trx = tlx + pEntity.width;
//
//			// Horizontal check at this vertical position at each block position
//			for (float checkedTileX = tlx;; checkedTileX += ConstantsTable.BLOCK_SIZE_PIXELS) {
//
//				checkedTileX = Math.min(checkedTileX, trx);
//
//				int tileCoordX = (int) (checkedTileX / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (hasLevelCollision(pLevel, tileCoordX, tileIndexY + pOffset)) {
//					if (pPhysicsState != null) {
//						// Calculate the tile's top position
//						pPhysicsState.collisionY = (tileIndexY + 1) * ConstantsTable.BLOCK_SIZE_PIXELS;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileX >= trx)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	static public boolean hasGround(IGridCollider pLevel, RectangleEntity pEntity, PhysicsState pPhysicsState) {
//		final float oblx = pEntity.oldX - pEntity.width / 2;
//		final float obly = pEntity.oldY + pEntity.height / 2;
//
//		final float nblx = pEntity.x - pEntity.width / 2;
//		final float nbly = pEntity.y + pEntity.height / 2;
//
//		// Calculate the vertical span and dist
//		int endY = (int) (nbly / ConstantsTable.BLOCK_SIZE_PIXELS);
//		int begY = Math.min((int) (obly / ConstantsTable.BLOCK_SIZE_PIXELS) - 1, endY);
//		int dist = Math.max(Math.abs(endY - begY), 1);
//
//		// sanity check that the distance between begY and endY isn't something stupid (like when I messed the physics up)
//
//		// Vertically iterate over the range of the entity between oldBottomLeft nad newBottomLeft
//		for (int tileIndexY = begY; tileIndexY <= endY; tileIndexY++) {
//			float blx = MathHelper.lerp(nblx, oblx, (float) Math.abs(endY - tileIndexY) / dist);
//			float brx = blx + pEntity.width;
//
//			// Horizontal check at this vertical position at each block position
//			for (float checkedTileX = blx;; checkedTileX += ConstantsTable.BLOCK_SIZE_PIXELS) {
//
//				checkedTileX = Math.min(checkedTileX, brx);
//
//				int tileCoordX = (int) (checkedTileX / ConstantsTable.BLOCK_SIZE_PIXELS);
//				int playerCY = (int) ((pEntity.y + pEntity.height / 2) / ConstantsTable.BLOCK_SIZE_PIXELS);
//
//				// Check the collision with the tile below
//				if (tileIndexY >= playerCY && hasLevelCollision(pLevel, tileCoordX, tileIndexY)) {
//					if (pPhysicsState != null) {
//						// Calculate the tile's top position
//						pPhysicsState.collisionY = tileIndexY * ConstantsTable.BLOCK_SIZE_PIXELS;
//
//					}
//
//					return true;
//
//				}
//
//				// Logical exit condition
//				if (checkedTileX >= brx)
//					break;
//
//			}
//
//		}
//
//		return false;
//
//	}
//
//	//
//	// General Methods
//	//
//
//	static private boolean hasLevelCollision(IGridCollider pGridCollider, int pCX, int pCY) {
//		if (pGridCollider == null)
//			return true;
//
//		return pGridCollider.hasGridCollision(pCX, pCY);
//
//	}

}
