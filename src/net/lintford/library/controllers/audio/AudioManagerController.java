package net.lintford.library.controllers.audio;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.AudioManager;

public class AudioManagerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTORLLER_NAME = AudioManagerController.class.getSimpleName();

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this {@link AudioManagerController} has been initialised properly. */
	@Override
	public boolean isInitialised() {
		return mAudioManager != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioManagerController(final ControllerManager pControllerManager, final AudioManager pAudioManager, int pEntityGroupID) {
		super(pControllerManager, CONTORLLER_NAME, pEntityGroupID);

		mAudioManager = pAudioManager;

	}

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		mAudioManager = null;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

}
