package net.lintfordlib.core.audio.data;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

public class OGGAudioData extends AudioData {

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(String audioName, InputStream inputStream) {
		if (isLoaded())
			return false;

		mName = audioName;
		mBufferID = AL10.alGenBuffers();

		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			final var pcm = readVorbis(inputStream, 32 * 1024, info);
			alBufferData(mBufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());

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
			throw new RuntimeException(e);
		}

		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if (decoder == NULL)
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));

		STBVorbis.stb_vorbis_get_info(decoder, info);

		int channels = info.channels();

		int samples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

		ShortBuffer pcm = BufferUtils.createShortBuffer(samples);

		pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
		STBVorbis.stb_vorbis_close(decoder);

		return pcm;
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
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

}
