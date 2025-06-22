package net.lintfordlib.assets;

import java.io.File;
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
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.audio.music.MusicManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.spritegraph.SpriteGraphRepository;
import net.lintfordlib.core.graphics.GraphicsCompatibility;
import net.lintfordlib.core.graphics.fonts.BitmapFontManager;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetManager;
import net.lintfordlib.core.graphics.textures.TextureManager;
import net.lintfordlib.options.MasterConfig;

/**
 * @References Path Watcher: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * @TODO Directory watcher no longer working. Also need to put the directory watcher into a separate thread.
 */
public class ResourceManager {

	public static final String DEBUG_LIVE_RESOURCES_RELOAD_NAME = "DEBUG_LIVE_TEXTURE_RELOAD";
	public static final String DEBUG_LIVE_RESOURCES_RELOAD_ENABLED = "true";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected MasterConfig mConfig;
	protected GraphicsCompatibility mGraphicsCompatibility;

	protected Path mResourceTexturePath;
	protected Path mResourceSpriteSheetPath;
	protected WatchService mTexturePathWatcher;
	protected WatchService mSpriteSheetPathWatcher;

	protected TextureManager mTextureManager;
	protected BitmapFontManager mFontManager;
	protected SpriteSheetManager mSpriteSheetManager;
	protected AudioManager mAudioManager;
	protected SpriteGraphRepository mSpriteGraphRepository;

	private boolean mResourcesLoaded;
	private boolean mMonitorResourcesForChanges;
	private ResourceMap mResourceMap;

	private final List<Integer> mProtectedEntityGroupUids = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEntityGroupUidProtected(int entityGroupUid) {
		return mProtectedEntityGroupUids.contains(entityGroupUid);
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	public MasterConfig config() {
		return mConfig;
	}

	public GraphicsCompatibility graphicsCompatibility() {
		return mGraphicsCompatibility;
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

	public SpriteGraphRepository spriteGraphRepository() {
		return mSpriteGraphRepository;
	}

	// returns true if called on the main thread. otherwise false
	public boolean isMainOpenGlThread() {
		return Thread.currentThread().threadId() == config().display().mainOpenGlThreadId();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourceManager(MasterConfig config) {
		mConfig = config;

		mGraphicsCompatibility = new GraphicsCompatibility(this);

		mFontManager = new BitmapFontManager();
		mFontManager.initialize(this);
		mTextureManager = new TextureManager();
		mSpriteSheetManager = new SpriteSheetManager();
		mSpriteSheetManager.initialize(this);
		mAudioManager = new AudioManager(config.audio());
		mSpriteGraphRepository = new SpriteGraphRepository();

		enableFolderWatchersForResourceChanges();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources() {
		mGraphicsCompatibility.initialize();

		mTextureManager.loadResources(this);
		mAudioManager.loadResources(this);
		mSpriteGraphRepository.loadResources(this);

		mResourcesLoaded = true;
	}

	public void unloadContent() {
		mAudioManager.unloadResources();
		mTextureManager.unloadResources();
		mSpriteGraphRepository.unloadResources();

		mResourcesLoaded = false;
	}

	public void loadResourcesFromResMap(File filepath, int entityGroupUid) {
		mResourceMap = ResourceMapIo.tryLoadResourceMapFromFile(filepath);

		if (mResourceMap == null)
			return;

		final var lBaseDirectory = filepath.getParent();
		mResourceMap.loadResourcesIntoManager(this, lBaseDirectory, entityGroupUid);

	}

	public void update(LintfordCore core) {
		if (mMonitorResourcesForChanges) {

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F4, null)) {
				mTextureManager.reloadAllTextures();
			}

			if (mTexturePathWatcher != null) {
				final var watchKey = mTexturePathWatcher.poll();
				if (watchKey != null) {
					final var events = watchKey.pollEvents();
					for (final var event : events) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							mTextureManager.reloadAllTextures();
						}
					}

					watchKey.reset();
				}
			}

			if (mSpriteSheetPathWatcher != null) {
				final var spriteWatchKey = mSpriteSheetPathWatcher.poll();
				if (spriteWatchKey != null) {

					final var events = spriteWatchKey.pollEvents();
					for (final var event : events) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							mSpriteSheetManager.reload();
						}
					}

					spriteWatchKey.reset();
				}
			}
		}
	}

	public void increaseReferenceCounts(int entityGrouUid) {
		mTextureManager.increaseReferenceCounts(entityGrouUid);
	}

	public void decreaseReferenceCounts(int entityGroupUid) {
		mTextureManager.decreaseReferenceCounts(entityGroupUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addProtectedEntityGroupUid(int protectedEntityGroupUid) {
		if (mProtectedEntityGroupUids.contains(protectedEntityGroupUid) == false) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Adding EntityGroupUid to protected list: " + protectedEntityGroupUid);
			mProtectedEntityGroupUids.add(protectedEntityGroupUid);
		}
	}

	public void removeProtectedEntityGroupUid(int protectedEntityGroupUid) {
		if (mProtectedEntityGroupUids.contains(protectedEntityGroupUid) == true) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing EntityGroupUid to protected list: " + protectedEntityGroupUid);
			mProtectedEntityGroupUids.remove(protectedEntityGroupUid);
		}
	}

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

				mMonitorResourcesForChanges = true;

			} catch (Exception e) {
				mMonitorResourcesForChanges = mMonitorResourcesForChanges || false;
			}

			try {

				Path lSpriteSheetDirectory = Paths.get("res//spritesheets//");
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
				mMonitorResourcesForChanges = mMonitorResourcesForChanges || false;
			}
		}
	}
}