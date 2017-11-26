package net.lintford.library.core.audio;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

public class WaveAudioData extends AudioData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromFile(String pFilename) {
		if (isLoaded())
			return false;

		mBufferID = AL10.alGenBuffers();

		WaveData waveFile = WaveData.create(pFilename);

		if(waveFile == null){
			return false;
			
		}
		
		AL10.alBufferData(mBufferID, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();

		return true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
		ByteBuffer vorbis;
		try {
			vorbis = ioResourceToByteBuffer(resource, bufferSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if (decoder == NULL)
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));

		STBVorbis.stb_vorbis_get_info(decoder, info);

		int channels = info.channels();

		int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

		ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

		pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
		STBVorbis.stb_vorbis_close(decoder);

		return pcm;
	}

	// TODO (John): Move this into a dedicated helper class for resource loading
	/** Reads the specified resource and returns the raw data as a ByteBuffer.
	 *
	 * @param resource
	 *            the resource to read
	 * @param bufferSize
	 *            the initial buffer size
	 *
	 * @return the resource data
	 *
	 * @throws IOException
	 *             if an IO error occurs */
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1)
					;
			}
		} else {
			try (InputStream source = AudioManager.class.getClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1)
						break;
					if (buffer.remaining() == 0)
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	// TODO (John): Move this into a dedicated helper class for resource loading
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

}
