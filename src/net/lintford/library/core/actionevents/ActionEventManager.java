package net.lintford.library.core.actionevents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.debug.Debug;

public class ActionEventManager {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	// @formatter:off
	public enum PlaybackMode {
		Normal,   // The manager is largely inactive, providing neither playback of a file nor recording of actions to a file.
		Record,   // The manager actively records input and state and stores into the file (mFilename)
		Playback      // The manager actively reads input and state and exposes the data in the headerByteBuffer and dataByteBuffer.
	}
	// @formatter:on

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final PlaybackMode mPlaybackMode;
	private String mFilename;

	protected final ByteBuffer mHeaderByteBuffer;
	protected final ByteBuffer mDataByteBuffer;

	// ---------------------------------------------
	// Properties
	// --------------------------------------------

	/** Returns the header bytebuffer for the {@link ActionEventManager}. If the ActionEventManager was created with mode Normal, then this will return null. */
	public ByteBuffer headerByteBuffer() {
		return mHeaderByteBuffer;
	}

	/** Returns the data (actions) bytebuffer for the {@link ActionEventManager}. If the ActionEventManager was created with mode Normal, then this will return null. */
	public ByteBuffer dataByteBuffer() {
		return mDataByteBuffer;
	}

	/**
	 * Returns the mode designated during creation of this {@link ActionEventManager}. If the mode is {@link PlaybackMode.Record} or {@link PlaybackMode.Read}, then this manager will have appropriate byteBuffers.
	 */
	public PlaybackMode mode() {
		return mPlaybackMode;
	}

	/** Returns true if we are in READ mode and the end of the input file has been reached. */
	public boolean endOfFileReached() {
		return mPlaybackMode == PlaybackMode.Playback && mDataByteBuffer.position() == mDataByteBuffer.limit();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ActionEventManager(PlaybackMode mode, int headerSizeInBytes, int dataSizeInBytes) {
		mPlaybackMode = mode;

		if (mPlaybackMode != PlaybackMode.Normal) {
			mHeaderByteBuffer = MemoryUtil.memAlloc(headerSizeInBytes);
			mDataByteBuffer = MemoryUtil.memAlloc(dataSizeInBytes);
		} else {
			mHeaderByteBuffer = null;
			mDataByteBuffer = null;
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void filename(String filename) {
		mFilename = filename;
	}

	public String filename() {
		return mFilename;
	}

	// ---- FILE

	public void saveToFile() {
		if (mFilename == null || mFilename.length() == 0)
			return;

		mHeaderByteBuffer.flip();
		mDataByteBuffer.flip();

		var file = new File(mFilename);
		try (var output = new FileOutputStream(file)) {
			var dest = output.getChannel();

			// --- HEADER
			dest.write(mHeaderByteBuffer);

			// --- DATA
			dest.write(mDataByteBuffer);
			dest.close();
		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save input to file: " + mFilename);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
			return;
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save input to file: " + mFilename);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
			return;
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Saved input to file: " + mFilename);
	}

	public boolean loadFromFile(String filename) {
		var file = new File(filename);

		if (!file.exists() || file.isDirectory())
			return false;

		try (var output = new FileInputStream(file)) {
			var src = output.getChannel();

			src.read(mHeaderByteBuffer);
			src.read(mDataByteBuffer);
			src.close();

			mHeaderByteBuffer.flip();
			mDataByteBuffer.flip();

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load input to file: " + mFilename);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
			return false;
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load input to file: " + mFilename);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
			return false;
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Input file loaded: " + mFilename);
		return true;
	}

}
