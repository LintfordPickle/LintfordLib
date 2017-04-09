package net.ld.library.controllers;

import org.junit.Test;

import net.ld.library.cellworld.CellWorldEntity;
import net.ld.library.core.camera.Camera;
import net.ld.library.core.time.GameTime;

public class CameraFollowEntityTest {

	@Test
	public void cameraFollowPositionSetupTest() {
		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);
		
		CameraFollowCellEntityController lFollowController = new CameraFollowCellEntityController();
		CellWorldEntity lEntity = new CellWorldEntity();
		GameTime lGameTime = new GameTime();
		
		// Act
		lFollowController.setFollowCharacter(lEntity);
		lFollowController.setCamera(lCamera);		
		
		lFollowController.update(lGameTime);
		
		// Assert
		assert(lCamera.targetPosition().x == -0) : "Camera target position X is not set at entity position (start)";
		assert(lCamera.targetPosition().y == -0) : "Camera target position Y is not set at entity position (start)";
		
	}
	
	@Test
	public void cameraFollowPositionTest() {

		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);
		
		CameraFollowCellEntityController lFollowController = new CameraFollowCellEntityController();
		CellWorldEntity lEntity = new CellWorldEntity();
		lEntity.setPosition(50f, 76f);
		GameTime lGameTime = new GameTime();
		
		// Act
		lFollowController.setFollowCharacter(lEntity);
		lFollowController.setCamera(lCamera);		
		
		lFollowController.update(lGameTime);
		
		// Assert
		assert(lCamera.targetPosition().x == -50f) : "Camera target position X is not set at entity position";
		assert(lCamera.targetPosition().y == -76f) : "Camera target position Y is not set at entity position";
		
	}

}
