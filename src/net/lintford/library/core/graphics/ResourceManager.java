package net.lintford.library.core.graphics;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.graphics.fonts.FontManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.options.MasterConfig;

/**
 * @References Path Watcher: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * @TODO Need to put the directory watcher into a separate thread, because it is blocking
 */
public class ResourceManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected MasterConfig mConfig;

	protected Path mResourceTexturePath;
	protected WatchService mTexturePathWatcher;

	protected FontManager mFontManager;
	protected SpriteSheetManager mSpriteSheetManager;
	protected AudioManager mAudioManager;

	// SoundManager
	// MusicManager

	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public MasterConfig config() {
		return mConfig;
	}

	public SpriteSheetManager spriteSheetManager() {
		return mSpriteSheetManager;
	}

	public FontManager fontManager() {
		return mFontManager;
	}

	public AudioManager audioManager() {
		return mAudioManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourceManager(MasterConfig pConfig) {
		mConfig = pConfig;

		mFontManager = new FontManager();

		mResourceTexturePath = Paths.get("res//textures//");

		// Setup the SpritesheetManager
		mSpriteSheetManager = new SpriteSheetManager();
		mAudioManager = new AudioManager();

		// Setup the AnimationManager

		// Setup the Texture Manager*
		// *textureManager is actually setup as a singletonclass in the LWJGLCore. Here we just add a directory watcher to watch for changes.
		if (ConstantsTable.getBooleanValueDef("DEBUG_TEXTURE_RELOAD_WATCHER", true)) {
			try {
				mTexturePathWatcher = mResourceTexturePath.getFileSystem().newWatchService();

				Files.walkFileTree(mResourceTexturePath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						dir.register(mTexturePathWatcher, StandardWatchEventKinds.ENTRY_MODIFY);
						return FileVisitResult.CONTINUE;
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent() {
		// Force creation here if not already
		TextureManager.textureManager();
		mAudioManager.loadALContent();

		mFontManager.loadGLContent(this);

		// TODO: Need the resource manager to also manage shaders (so they can be recompiled etc).

		mIsLoaded = true;
	}

	public void unloadContent() {
		mFontManager.unloadGLContent();
		mAudioManager.unloadALContent();

		TextureManager.textureManager().unloadGLContent();

		mIsLoaded = false;
	}

	public void update(LintfordCore pCore) {
		if (ConstantsTable.getBooleanValueDef("DEBUG_TEXTURE_RELOAD_WATCHER", true)) {
			WatchKey lKey = mTexturePathWatcher.poll();
			if (lKey != null) {

				List<WatchEvent<?>> events = lKey.pollEvents();
				for (WatchEvent<?> event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						TextureManager.textureManager().reloadTextures();

					}

				}

				lKey.reset();
			}
		}

	}

}
