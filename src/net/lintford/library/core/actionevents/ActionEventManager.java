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
		Normal,   // manager is inactive
		Record, // manager records input and state and stores into file
		Read    // manager provides data from input file
	}
	// @formatter:on

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PlaybackMode mPlaybackMode = PlaybackMode.Normal;
	private String mFilename;

	protected ByteBuffer mHeaderByteBuffer;
	protected ByteBuffer mDataByteBuffer;

	// ---------------------------------------------
	// Properties
	// --------------------------------------------

	public ByteBuffer headerByteBuffer() {
		return mHeaderByteBuffer;
	}

	public ByteBuffer dataByteBuffer() {
		return mDataByteBuffer;
	}

	public PlaybackMode mode() {
		return mPlaybackMode;
	}

	/** Returns true if we are in READ mode and the end of the input file has been reached. */
	public boolean endOfFileReached() {
		return mPlaybackMode == PlaybackMode.Read && mDataByteBuffer.position() == mDataByteBuffer.limit();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ActionEventManager(int headerSizeInBytes, int dataSizeInBytes) {
		mHeaderByteBuffer = MemoryUtil.memAlloc(headerSizeInBytes);
		mDataByteBuffer = MemoryUtil.memAlloc(dataSizeInBytes);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void setNormalMode() {
		mPlaybackMode = PlaybackMode.Normal;
	}

	public void setRecordingMode(String filename) {
		if (mPlaybackMode != PlaybackMode.Normal)
			return;

		mFilename = filename;
		mPlaybackMode = PlaybackMode.Record;
	}

	public void setPlaybackMode(String filename) {
		if (mPlaybackMode != PlaybackMode.Normal)
			return;

		mFilename = filename;
		if (loadFromFile()) {
			mPlaybackMode = PlaybackMode.Read;
		}
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

	private boolean loadFromFile() {
		var file = new File(mFilename);

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
