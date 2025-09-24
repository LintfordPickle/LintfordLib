package net.lintfordlib.core.audio.data;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

public class OGGAudioData extends AudioDataBase {

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(String audioName, InputStream inputStream) {
		if (isLoaded())
			return false;

		mName = audioName;
		int tempBufferID = AL10.alGenBuffers();

		if (tempBufferID == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to generate OpenAL buffer for: " + audioName);
			return false;
		}

		try (final var info = STBVorbisInfo.malloc()) {
			final var pcm = readVorbis(inputStream, 32 * 1024, info);
			if (pcm == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to decode OGG Vorbis data for: " + audioName);

				AL10.alDeleteBuffers(tempBufferID);
				return false;
			}

			alBufferData(mBufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());

			// Check for OpenAL errors
			final var alError = AL10.alGetError();
			if (alError != AL10.AL_NO_ERROR) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "OpenAL error uploading audio data: " + alError + " for file: " + audioName);
				AL10.alDeleteBuffers(tempBufferID);
				return false;
			}

			// Only assign buffer ID after successful upload
			mBufferID = tempBufferID;

			mSize = AL10.alGetBufferi(mBufferID, AL10.AL_SIZE);
			mBitsPerSample = AL10.alGetBufferi(mBufferID, AL10.AL_BITS);
			mFrequency = info.sample_rate();
			mChannels = info.channels();

			final int lLengthInSamples = pcm.capacity();
			mDurationInSeconds = (float) lLengthInSamples / (float) mFrequency;

			if (ConstantsApp.getBooleanValueDef("DEBUG_AUDIO_ENABLED", false)) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");
				Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioEntity Name: " + audioName);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Size: " + mSize);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Frequency: " + mFrequency);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Channels: " + mChannels);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "mBitsPerSample: " + mBitsPerSample);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Duration (Seconds): " + mDurationInSeconds);
				Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");
			}

			return true;
		} catch (Exception e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Exception loading OGG file: " + audioName + " - " + e.getMessage());

			if (tempBufferID != 0)
				AL10.alDeleteBuffers(tempBufferID);

			return false;
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	static ShortBuffer readVorbis(InputStream pInputStream, int bufferSize, STBVorbisInfo info) {
		ByteBuffer vorbis;
		try {
			vorbis = ioResourceToByteBuffer(pInputStream, bufferSize);
		} catch (IOException e) {
			Debug.debugManager().logger().e(OGGAudioData.class.getSimpleName(), "Failed to read OGG file data: " + e.getMessage());
			return null;
		}

		final var error = BufferUtils.createIntBuffer(1);
		final var decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if (decoder == NULL) {
			Debug.debugManager().logger().e(OGGAudioData.class.getSimpleName(), "Failed to open Ogg Vorbis file. STB Error: " + error.get(0));
			return null;
		}

		try {
			STBVorbis.stb_vorbis_get_info(decoder, info);

			final var channels = info.channels();
			if (channels < 1 || channels > 2) {
				Debug.debugManager().logger().e(OGGAudioData.class.getSimpleName(), "Unsupported channel count: " + channels + ". Only mono and stereo are supported.");
				return null;
			}

			final var samples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
			if (samples <= 0) {
				Debug.debugManager().logger().e(OGGAudioData.class.getSimpleName(), "Invalid sample count: " + samples);
				return null;
			}

			// Create buffer for all samples across all channels
			final var pcm = BufferUtils.createShortBuffer(samples * channels);

			final var samplesRead = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
			if (samplesRead <= 0) {
				Debug.debugManager().logger().e(OGGAudioData.class.getSimpleName(), "Failed to decode OGG samples. Samples read: " + samplesRead);
				return null;
			}

			pcm.limit(samplesRead * channels);
			pcm.rewind();

			return pcm;

		} finally {

			STBVorbis.stb_vorbis_close(decoder);
		}
	}

	/**
	 * Reads the specified resource and returns the raw data as a ByteBuffer.
	 *
	 * @param resource   the resource to read
	 * @param bufferSize the initial buffer size
	 *
	 * @return the resource data
	 *
	 * @throws IOException if an IO error occurs
	 */
	public static ByteBuffer ioResourceToByteBuffer(InputStream pInputStream, int bufferSize) throws IOException {
		ByteBuffer buffer;

		try (ReadableByteChannel rbc = Channels.newChannel(pInputStream)) {
			buffer = createByteBuffer(bufferSize);

			while (true) {
				int bytes = rbc.read(buffer);

				if (bytes == -1)
					break;

				if (buffer.remaining() == 0)
					buffer = resizeBuffer(buffer, buffer.capacity() * 2);
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {

		final var originalPosition = buffer.position();
		final var originalLimit = buffer.limit();

		try {
			final var newBuffer = BufferUtils.createByteBuffer(newCapacity);

			// Prepare buffer for reading without modifying original state permanently
			buffer.flip();
			newBuffer.put(buffer);

			return newBuffer;

		} catch (Exception e) {
			buffer.position(originalPosition);
			buffer.limit(originalLimit);
			throw e;
		}
	}

}
