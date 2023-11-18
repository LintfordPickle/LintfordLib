package net.lintfordlib.core;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class AppResources {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mResourcesRootDirectory = "res" + FileUtils.FILE_SEPERATOR;
	private String mTexturesDirectory = "textures" + FileUtils.FILE_SEPERATOR;
	private String mPObjectsDirectory = "pojects" + FileUtils.FILE_SEPERATOR;
	private String mSpritesheetDirectory = "spritesheets" + FileUtils.FILE_SEPERATOR;
	private String mMusicDirectory = "music" + FileUtils.FILE_SEPERATOR;
	private String mSoundFxDirectory = "sfx" + FileUtils.FILE_SEPERATOR;
	private String mFontsDirectory = "fonts" + FileUtils.FILE_SEPERATOR;
	private String mShadersDirectory = "shaders" + FileUtils.FILE_SEPERATOR;
	private String mDefDirectory = "def" + FileUtils.FILE_SEPERATOR;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String root() {
		return mResourcesRootDirectory;
	}

	public void root(String newRootDirectory) {
		if (newRootDirectory == null || newRootDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set resources root directory to null or empty.");
			return;
		}

		if (newRootDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newRootDirectory = newRootDirectory + FileUtils.FILE_SEPERATOR;
		}

		mResourcesRootDirectory = newRootDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Resources root directory set to : " + mResourcesRootDirectory);
	}

	public String texturesDirectory() {
		return mResourcesRootDirectory + mTexturesDirectory;
	}

	public void texturesDirectory(String newTexturesDirectory) {
		if (newTexturesDirectory == null || newTexturesDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set textures directory to null or empty.");
			return;
		}

		if (newTexturesDirectory.startsWith(root())) {
			newTexturesDirectory = newTexturesDirectory.substring(root().length());
		}

		if (newTexturesDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newTexturesDirectory = newTexturesDirectory + FileUtils.FILE_SEPERATOR;
		}

		mTexturesDirectory = newTexturesDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "textures directory set to : " + mTexturesDirectory);
	}

	public String pobjectsDirectory() {
		return mResourcesRootDirectory + mPObjectsDirectory;
	}

	public void pobjectsDirectory(String newPObjectsDirectory) {
		if (newPObjectsDirectory == null || newPObjectsDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set pobjects directory to null or empty.");
			return;
		}

		if (newPObjectsDirectory.startsWith(root())) {
			newPObjectsDirectory = newPObjectsDirectory.substring(root().length());
		}

		if (newPObjectsDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newPObjectsDirectory = newPObjectsDirectory + FileUtils.FILE_SEPERATOR;
		}

		mPObjectsDirectory = newPObjectsDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "textures root directory set to : " + mTexturesDirectory);
	}

	public String spritesheetsDirectory() {
		return mResourcesRootDirectory + mSpritesheetDirectory;
	}

	public void spritesheetsDirectory(String newSpritesheetsDirectory) {
		if (newSpritesheetsDirectory == null || newSpritesheetsDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set spritesheets directory to null or empty.");
			return;
		}

		if (newSpritesheetsDirectory.startsWith(root())) {
			newSpritesheetsDirectory = newSpritesheetsDirectory.substring(root().length());
		}

		if (newSpritesheetsDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newSpritesheetsDirectory = newSpritesheetsDirectory + FileUtils.FILE_SEPERATOR;
		}

		mSpritesheetDirectory = newSpritesheetsDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Spritesheets resources directory set to : " + mSpritesheetDirectory);
	}

	public String musicDirectory() {
		return mResourcesRootDirectory + mMusicDirectory;
	}

	public void musicDirectory(String newMusicDirectory) {
		if (newMusicDirectory == null || newMusicDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set music resources directory to null or empty.");
			return;
		}

		if (newMusicDirectory.startsWith(root())) {
			newMusicDirectory = newMusicDirectory.substring(root().length());
		}

		if (newMusicDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newMusicDirectory = newMusicDirectory + FileUtils.FILE_SEPERATOR;
		}

		mMusicDirectory = newMusicDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Music resources directory set to : " + mMusicDirectory);
	}

	public String sfxDirectory() {
		return mResourcesRootDirectory + mSoundFxDirectory;
	}

	public void sfxDirectory(String newSfxDirectory) {
		if (newSfxDirectory == null || newSfxDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set sfx resources directory to null or empty.");
			return;
		}

		if (newSfxDirectory.startsWith(root())) {
			newSfxDirectory = newSfxDirectory.substring(root().length());
		}

		if (newSfxDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newSfxDirectory = newSfxDirectory + FileUtils.FILE_SEPERATOR;
		}

		mSoundFxDirectory = newSfxDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Sfx resources directory set to : " + mSoundFxDirectory);
	}

	public String fontsDirectory() {
		return mResourcesRootDirectory + mFontsDirectory;
	}

	public void fontsDirectory(String newFontsDirectory) {
		if (newFontsDirectory == null || newFontsDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set fonts resources directory to null or empty.");
			return;
		}

		if (newFontsDirectory.startsWith(root())) {
			newFontsDirectory = newFontsDirectory.substring(root().length());
		}

		if (newFontsDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newFontsDirectory = newFontsDirectory + FileUtils.FILE_SEPERATOR;
		}

		mFontsDirectory = newFontsDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Fonts resources directory set to : " + mFontsDirectory);
	}

	public String shadersDirectory() {
		return mResourcesRootDirectory + mShadersDirectory;
	}

	public void shadersDirectory(String newShadersDirectory) {
		if (newShadersDirectory == null || newShadersDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set shaders resources directory to null or empty.");
			return;
		}

		if (newShadersDirectory.startsWith(root())) {
			newShadersDirectory = newShadersDirectory.substring(root().length());
		}

		if (newShadersDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newShadersDirectory = newShadersDirectory + FileUtils.FILE_SEPERATOR;
		}

		mShadersDirectory = newShadersDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Shaders resources directory set to : " + mShadersDirectory);
	}

	public String defsDirectory() {
		return mResourcesRootDirectory + mDefDirectory;
	}

	public void defsDirectory(String newDefsDirectory) {
		if (newDefsDirectory == null || newDefsDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set defs resources directory to null or empty.");
			return;
		}

		if (newDefsDirectory.startsWith(root())) {
			newDefsDirectory = newDefsDirectory.substring(root().length());
		}

		if (newDefsDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newDefsDirectory = newDefsDirectory + FileUtils.FILE_SEPERATOR;
		}

		mDefDirectory = newDefsDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Shaders resources directory set to : " + mDefDirectory);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	AppResources() {

	}

}
