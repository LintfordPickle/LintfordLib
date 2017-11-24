package net.ld.library.core.graphics;

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

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.fonts.FontManager;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.time.GameTime;

/**
 * @References Path Watcher: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * @TODO Need to put the directory watcher into a separate thread, because it is blocking
 */
public class ResourceManager {

	// =============================================
	// Variables
	// =============================================

	protected DisplayConfig mDisplayConfig;
	private FontManager mFontManager;

	private Path mResourcePath;
	private WatchService mTexturePathWatcher;
	
	private boolean mIsLoaded;

	// =============================================
	// Properties
	// =============================================

	public FontManager fontManager() {
		return mFontManager;
	}
	
	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}
	
	// =============================================
	// Constructor
	// =============================================

	public ResourceManager(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;
		
		mFontManager = new FontManager();

	}

	// =============================================
	// Core-Method
	// =============================================

	public void loadGLContent(){
		mFontManager.loadGLContent(this);
		
		mIsLoaded = true;
		
	}
	
	public void unloadGLContent(){
		mFontManager.unloadGLContent();
		
		mIsLoaded = false;
		
	}
	
	public void update(GameTime pGameTime) {
		if (mTexturePathWatcher != null) {
			WatchKey lKey = mTexturePathWatcher.poll();
			if (lKey != null) {

				List<WatchEvent<?>> events = lKey.pollEvents();
				for (WatchEvent<?> event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("Detected change in texture folder. Reload of textures triggered ...");

						TextureManager.textureManager().reloadTextures();
					}
				}

				lKey.reset();
			}

		}

	}

	// =============================================
	// Methods
	// =============================================

	public void watchTextureDirectory(String pPath) {
		if (pPath == null || pPath.length() == 0) {
			System.out.println("Cannot watch texture directory. No valid directory supplied");
			return;
		}
		mResourcePath = Paths.get(pPath);

		try {
			System.out.println("Create directory watcher on " + mResourcePath);
			mTexturePathWatcher = mResourcePath.getFileSystem().newWatchService();

			Files.walkFileTree(mResourcePath, new SimpleFileVisitor<Path>() {
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
