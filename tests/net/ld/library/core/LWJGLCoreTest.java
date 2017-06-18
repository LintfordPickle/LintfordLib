/**
 * 
 */
package net.ld.library.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ld.library.GameInfo;
import net.ld.library.core.rendering.RenderState;

/**
 * Tests the core functionality of the abstract {@link LWJGLCore} class when it is extended.
 */
public class LWJGLCoreTest {

	private static LWJGLCore coreLWJGLObject;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Arrange
		coreLWJGLObject = createLWJGLCoreObject();

		// Act
		boolean result = coreLWJGLObject.createWindow();

		// Assert
		assert (result) : "LGWJGLCore failed to create window";

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Arrange

		// Act
		coreLWJGLObject.closeApp();

		// Assert

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a {@link LWJGLCore} object is created.
	 */
	@Test
	public void gameTimeCreation() {
		// Arrange

		// Act

		// Assert (Test the action was performed correctly)
		assert (coreLWJGLObject.gameTime() != null) : "LGWJGLCore failed to instantiate a valid GameTime object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a {@link LWJGLCore} object is created.
	 */
	@Test
	public void inputStateCreation() {
		// Arrange

		// Act

		// Assert
		assert (coreLWJGLObject.inputState() != null) : "LGWJGLCore failed to instantiate a valid InputState object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a {@link LWJGLCore} object is created.
	 */
	@Test
	public void hudCameraCreation() {
		// Arrange

		// Act

		// Assert
		assert (coreLWJGLObject.hud() != null) : "LGWJGLCore failed to instantiate a valid HUD object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a {@link LWJGLCore} object is created.
	 */
	@Test
	public void gameCameraCreation() {
		// Arrange

		// Act

		// Assert
		assert (coreLWJGLObject.gameCamera() != null) : "LGWJGLCore failed to instantiate a valid Camera object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a {@link LWJGLCore} object is created.
	 */
	@Test
	public void renderStateCreation() {
		// Arrange

		// Act

		// Assert
		assert (coreLWJGLObject.renderState() != null) : "LGWJGLCore failed to instantiate a valid RenderState object";

	}

	/** Creates a new instance of LWJGLCore and returns it. */
	private static LWJGLCore createLWJGLCoreObject() {
		final GameInfo GAME_INFO = new GameInfo() {
		};
		LWJGLCore testObject = new LWJGLCore(GAME_INFO) {

			@Override
			public void onInitialiseGL() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onInitialiseApp() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onDraw(RenderState pRenderState) {
				// TODO Auto-generated method stub

			}
		};

		return testObject;
	}

}
