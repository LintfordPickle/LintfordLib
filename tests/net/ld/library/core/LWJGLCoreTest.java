/**
 * 
 */
package net.ld.library.core;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ld.library.GameInfo;
import net.ld.library.core.LWJGLCore;
import net.ld.library.core.rendering.RenderState;

/**
 * Tests the core functionality of the abstract {@link LWJGLCore} class when it
 * is extended.
 */
public class LWJGLCoreTest {

	@BeforeClass
	public static void runOnceBeforeClass() {
		System.out.println("@BeforeClass - runOnceBeforeClass");

	}

	/** Tests the creation of an OpenGL window */
	@Test
	public void windowCreatedSuccessfully() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		boolean result = lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (result) : "LGWJGLCore failed to create window";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a
	 * {@link LWJGLCore} object is created.
	 */
	@Test
	public void gameTimeCreation() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (lCoreObject.gameTime() != null) : "LGWJGLCore failed to instantiate a valid GameTime object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a
	 * {@link LWJGLCore} object is created.
	 */
	@Test
	public void inputStateCreation() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (lCoreObject.inputState() != null) : "LGWJGLCore failed to instantiate a valid InputState object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a
	 * {@link LWJGLCore} object is created.
	 */
	@Test
	public void hudCameraCreation() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (lCoreObject.hud() != null) : "LGWJGLCore failed to instantiate a valid HUD object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a
	 * {@link LWJGLCore} object is created.
	 */
	@Test
	public void gameCameraCreation() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (lCoreObject.gameCamera() != null) : "LGWJGLCore failed to instantiate a valid Camera object";

	}

	/**
	 * Tests that a valid {@link RenderState} object is instantiated when a
	 * {@link LWJGLCore} object is created.
	 */
	@Test
	public void renderStateCreation() {
		// Arrange (Get all objects ready)
		LWJGLCore lCoreObject = createLWJGLCoreObject();

		// Act (perform the action under test)
		lCoreObject.createWindow();

		// Assert (Test the action was performed correctly)
		assert (lCoreObject.renderState() != null) : "LGWJGLCore failed to instantiate a valid RenderState object";

	}

	/** Creates a new instance of LWJGLCore and returns it. */
	private LWJGLCore createLWJGLCoreObject() {
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
