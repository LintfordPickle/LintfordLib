package net.lintford.library.core;

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

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.audio.music.MusicManager;
import net.lintford.library.core.box2d.PObjectManager;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphRepository;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.options.MasterConfig;

/**
 * @References Path Watcher: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * @TODO Need to put the directory watcher into a separate thread, because it is blocking
 */
public class ResourceManager {

	public static final String DEBUG_LIVE_RESOURCES_RELOAD_NAME = "DEBUG_LIVE_TEXTURE_RELOAD";
	public static final String DEBUG_LIVE_RESOURCES_RELOAD_ENABLED = "true";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected MasterConfig mConfig;

	protected Path mResourceTexturePath;
	protected Path mResourceSpriteSheetPath;
	protected WatchService mTexturePathWatcher;
	protected WatchService mSpriteSheetPathWatcher;

	protected TextureManager mTextureManager;
	protected BitmapFontManager mFontManager;
	protected SpriteSheetManager mSpriteSheetManager;
	protected AudioManager mAudioManager;
	protected PObjectManager mPObjectManager;
	protected SpriteGraphRepository mSpriteGraphRepository;

	// TODO: ResouceManagers still to be implemented:
	// GeometryManager
	// ShaderManager

	private boolean mIsLoaded;
	private boolean mMonitorResourcesForChanges;

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

	public TextureManager textureManager() {
		return mTextureManager;
	}

	public BitmapFontManager fontManager() {
		return mFontManager;
	}

	public AudioManager audioManager() {
		return mAudioManager;
	}

	public MusicManager musicManager() {
		return mAudioManager.musicManager();
	}

	public PObjectManager pobjectManager() {
		return mPObjectManager;
	}

	public SpriteGraphRepository spriteGraphRepository() {
		return mSpriteGraphRepository;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourceManager(MasterConfig pConfig) {
		mConfig = pConfig;

		mFontManager = new BitmapFontManager();
		mFontManager.initialize(this);
		mTextureManager = new TextureManager();
		mSpriteSheetManager = new SpriteSheetManager();
		mAudioManager = new AudioManager(pConfig.audio());
		mSpriteGraphRepository = new SpriteGraphRepository();
		mPObjectManager = new PObjectManager();

		enableFolderWatchersForResourceChanges();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent() {
		mTextureManager.loadGLContent(this);
		mSpriteSheetManager.loadGLContent(this);
		mAudioManager.loadALContent(this);
		mPObjectManager.loadGLContent(this);
		mSpriteGraphRepository.loadGLContent(this);

		mIsLoaded = true;
	}

	public void unloadContent() {
		mAudioManager.unloadALContent();
		mTextureManager.unloadGLContent();
		mPObjectManager.unloadGLContent();
		mSpriteGraphRepository.unloadGLContent();

		mIsLoaded = false;
	}

	public void update(LintfordCore pCore) {
		if (mMonitorResourcesForChanges) {
			WatchKey lKey = mTexturePathWatcher.poll();
			if (lKey != null) {

				List<WatchEvent<?>> events = lKey.pollEvents();
				for (WatchEvent<?> event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						mTextureManager.reloadTextures();

					}

				}

				lKey.reset();
			}

			if (mSpriteSheetPathWatcher != null) {
				WatchKey lSpriteFileKey = mSpriteSheetPathWatcher.poll();
				if (lSpriteFileKey != null) {

					List<WatchEvent<?>> events = lSpriteFileKey.pollEvents();
					for (WatchEvent<?> event : events) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							// Reload the spritesheet in question ...
							mSpriteSheetManager.reload();
						}

					}

					lSpriteFileKey.reset();
				}

			}
		}

	}

	public void increaseReferenceCounts(int pEntityGroupID) {
		mTextureManager.increaseReferenceCounts(pEntityGroupID);

	}

	public void decreaseReferenceCounts(int pEntityGroupID) {
		mTextureManager.decreaseReferenceCounts(pEntityGroupID);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void enableFolderWatchersForResourceChanges() {
		ConstantsApp.registerValue(DEBUG_LIVE_RESOURCES_RELOAD_NAME, DEBUG_LIVE_RESOURCES_RELOAD_ENABLED);

		if (ConstantsApp.getBooleanValueDef(DEBUG_LIVE_RESOURCES_RELOAD_NAME, false)) {
			try {
				Path lTexturesDirectory = Paths.get("res//textures//");
				mTexturePathWatcher = lTexturesDirectory.getFileSystem().newWatchService();
				Files.walkFileTree(lTexturesDirectory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						dir.register(mTexturePathWatcher, StandardWatchEventKinds.ENTRY_MODIFY);
						return FileVisitResult.CONTINUE;
					}
				});

				Path lSpriteSheetDirectory = Paths.get("res//spritesheets//game//");
				mSpriteSheetPathWatcher = lSpriteSheetDirectory.getFileSystem().newWatchService();
				Files.walkFileTree(lSpriteSheetDirectory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						dir.register(mSpriteSheetPathWatcher, StandardWatchEventKinds.ENTRY_MODIFY);
						return FileVisitResult.CONTINUE;
					}

				});

				mMonitorResourcesForChanges = true;

			} catch (Exception e) {
				mMonitorResourcesForChanges = false;
			}
		}
	}

}
