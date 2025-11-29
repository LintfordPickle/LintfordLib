package net.lintfordlib.core.audio.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.debug.Debug;

public class WaveAudioFile extends AudioFileBase {

	/**
	 * Internal class to hold parsed WAV file data
	 */
	private static class WavFile {
		int audioFormat;
		int channels;
		int sampleRate;
		int bitsPerSample;
		ByteOrder byteOrder;
		ByteBuffer audioData;

		/**
		 * Parses a WAV file from a ByteBuffer
		 */
		private static WavFile parseWavFile(ByteBuffer buffer) {
			buffer.order(ByteOrder.LITTLE_ENDIAN); // WAV files are little-endian by default

			// Read RIFF header
			if (!checkChunkId(buffer, "RIFF")) {
				Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Not a valid WAV file - missing RIFF header");
				return null;
			}

			buffer.getInt(); // fileSize : File size minus 8 bytes

			if (!checkChunkId(buffer, "WAVE")) {
				Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Not a valid WAV file - missing WAVE identifier");
				return null;
			}

			WavFile wavFile = new WavFile();
			boolean foundFmt = false;
			boolean foundData = false;

			// Parse chunks
			while (buffer.hasRemaining() && (!foundFmt || !foundData)) {
				if (buffer.remaining() < 8)
					break;

				String chunkId = readChunkId(buffer);
				int chunkSize = buffer.getInt();

				if (chunkId.equals("fmt ")) {
					// Parse format chunk
					wavFile.audioFormat = buffer.getShort() & 0xFFFF;
					wavFile.channels = buffer.getShort() & 0xFFFF;
					wavFile.sampleRate = buffer.getInt();
					buffer.getInt(); // byteRate
					buffer.getShort(); // blockAlign
					wavFile.bitsPerSample = buffer.getShort() & 0xFFFF;

					// Skip any extra format bytes
					int extraSize = chunkSize - 16;
					if (extraSize > 0) {
						buffer.position(buffer.position() + extraSize);
					}

					// WAV files are always little-endian for the header, but audio data follows the format
					wavFile.byteOrder = ByteOrder.LITTLE_ENDIAN;
					foundFmt = true;

				} else if (chunkId.equals("data")) {
					// Parse data chunk
					byte[] audioBytes = new byte[chunkSize];
					buffer.get(audioBytes);

					wavFile.audioData = BufferUtils.createByteBuffer(audioBytes.length);
					wavFile.audioData.put(audioBytes);
					wavFile.audioData.flip();

					foundData = true;

				} else {
					// Skip unknown chunk
					buffer.position(buffer.position() + chunkSize);
				}

				// WAV chunks are word-aligned (pad byte if odd size)
				if (chunkSize % 2 != 0 && buffer.hasRemaining()) {
					buffer.get();
				}
			}

			if (!foundFmt || !foundData) {
				Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Invalid WAV file - missing " + (!foundFmt ? "format" : "data") + " chunk");
				return null;
			}

			return wavFile;

		}

		/**
		 * Reads a 4-character chunk ID from the buffer
		 */
		private static String readChunkId(ByteBuffer buffer) {
			byte[] id = new byte[4];
			buffer.get(id);
			return new String(id);
		}

		/**
		 * Checks if the next 4 bytes match the expected chunk ID
		 */
		private static boolean checkChunkId(ByteBuffer buffer, String expected) {
			return readChunkId(buffer).equals(expected);
		}
	}

	public static class AudioData {

		// --------------------------------------
		// Variables
		// --------------------------------------

		public final ByteBuffer data;
		public final int format;
		public final int samplerate;
		public final int channels;
		public final int sizeInBits;

		// --------------------------------------
		// Constructors
		// --------------------------------------

		public AudioData(ByteBuffer data, int format, int samplerate, int channels, int sizeInBits) {
			this.data = data;
			this.format = format;
			this.samplerate = samplerate;
			this.channels = channels;
			this.sizeInBits = sizeInBits;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		/** Disposes the {@link WaveData}. */
		public void dispose() {
			data.clear();
		}

		// --------------------------------------
		// MEthods
		// --------------------------------------

		/**
		 * Creates a new WaveData
		 * 
		 * @param data       actual wavedata
		 * @param format     format of wave data
		 * @param samplerate sample rate of data
		 * @param channels   number of channels
		 * @param sizeInBits bit depth
		 */
		static AudioData create(InputStream inputStream) {
			try {
				return create(readAllBytes(inputStream));
			} catch (Exception e) {
				Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Unable to create WaveData from inputstream, " + e.getMessage());
				Debug.debugManager().logger().printException(WaveAudioFile.class.getSimpleName(), e);
				return null;
			}
		}

		/**
		 * Creates a WaveData container from the specified bytes containing WAV data
		 *
		 * @param buffer array of bytes containing the complete WAV file
		 * @return WaveData containing data, or null if a failure occurred
		 */
		static AudioData create(byte[] buffer) {
			final var directBuffer = BufferUtils.createByteBuffer(buffer.length);
			directBuffer.put(buffer);
			directBuffer.flip();

			return create(directBuffer);
		}

		/**
		 * Creates a WaveData container from the specified ByteBuffer containing WAV data.
		 *
		 * @param wavBuffer ByteBuffer containing WAV file data
		 * @return WaveData containing data, or null if a failure occurred
		 */
		static AudioData create(ByteBuffer wavBuffer) {

			try {

				final var wavFile = WavFile.parseWavFile(wavBuffer);

				if (wavFile == null) {
					Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Failed to parse WAV file");
					return null;
				}

				// Validate encoding
				if (wavFile.audioFormat != 1 && wavFile.audioFormat != 3) { // 1 = PCM, 3 = IEEE Float
					Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported audio format: " + wavFile.audioFormat + ". Only PCM (1) and IEEE Float (3) are supported.");
					return null;
				}

				// Validate sample rate
				if (wavFile.sampleRate != 8000 && wavFile.sampleRate != 11025 && wavFile.sampleRate != 22050 && wavFile.sampleRate != 44100 && wavFile.sampleRate != 48000 && wavFile.sampleRate != 96000) {
					Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "Unusual sample rate: " + wavFile.sampleRate + "Hz. This may not play correctly on all systems.");
				}

				// Determine OpenAL format
				int alFormat = 0;
				int outputBitsPerSample = wavFile.bitsPerSample;

				if (wavFile.channels == 1) {
					switch (wavFile.bitsPerSample) {
					case 8:
						alFormat = AL10.AL_FORMAT_MONO8;
						break;
					case 16:
						alFormat = AL10.AL_FORMAT_MONO16;
						break;
					case 24:
						Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "24-bit audio will be converted to 16-bit (OpenAL core limitation)");
						alFormat = AL10.AL_FORMAT_MONO16;
						outputBitsPerSample = 16;
						break;
					case 32:
						Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "32-bit audio will be converted to 16-bit");
						alFormat = AL10.AL_FORMAT_MONO16;
						outputBitsPerSample = 16;
						break;
					default:
						Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported sample size: " + wavFile.bitsPerSample + " bits");
						return null;
					}
				} else if (wavFile.channels == 2) {
					switch (wavFile.bitsPerSample) {
					case 8:
						alFormat = AL10.AL_FORMAT_STEREO8;
						break;
					case 16:
						alFormat = AL10.AL_FORMAT_STEREO16;
						break;
					case 24:
						Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "24-bit stereo will be converted to 16-bit");
						alFormat = AL10.AL_FORMAT_STEREO16;
						outputBitsPerSample = 16;
						break;
					case 32:
						Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "32-bit stereo will be converted to 16-bit");
						alFormat = AL10.AL_FORMAT_STEREO16;
						outputBitsPerSample = 16;
						break;
					default:
						Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported sample size: " + wavFile.bitsPerSample + " bits");
						return null;
					}

					Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "WAV has stereo sound - this sound will not be played in 3d space");
				} else if (wavFile.channels > 2) {
					Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Multi-channel audio (" + wavFile.channels + " channels) is not supported. " + "Use mono for 3D positioned audio or stereo for background music.");
					return null;
				} else {
					Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Invalid channel count: " + wavFile.channels);
					return null;
				}

				// Convert audio data if needed
				var audioData = wavFile.audioData;
				if (wavFile.bitsPerSample == 24 || wavFile.bitsPerSample == 32) {
					audioData = convertToSixteenBit(wavFile.audioData, wavFile.bitsPerSample, wavFile.channels, wavFile.byteOrder == ByteOrder.BIG_ENDIAN);
				}

				// Convert to native byte order for OpenAL
				final var convertedData = convertToNativeOrder(audioData, outputBitsPerSample == 16, wavFile.byteOrder);

				Debug.debugManager().logger().i(WaveAudioFile.class.getSimpleName(), String.format("Loaded WAV audio: %d channels, %dHz, %d-bit, %d bytes", wavFile.channels, wavFile.sampleRate, outputBitsPerSample, convertedData.remaining()));

				
				return new AudioData(convertedData, alFormat, wavFile.sampleRate, wavFile.channels, outputBitsPerSample);

			} catch (Exception e) {
				Debug.debugManager().logger().e(WaveAudioFile.class.getSimpleName(), "Error parsing WAV file: " + e.getMessage());
				Debug.debugManager().logger().printException(WaveAudioFile.class.getSimpleName(), e);
				return null;
			}
		}

		// --------------------------------------
		// Helpers-Methods
		// --------------------------------------

		/**
		 * Convert 24-bit or 32-bit audio to 16-bit
		 */
		private static ByteBuffer convertToSixteenBit(ByteBuffer audioData, int bitsPerSample, int channels, boolean isBigEndian) {
			int bytesPerSample = bitsPerSample / 8;
			int samples = audioData.remaining() / bytesPerSample;

			ByteBuffer converted = BufferUtils.createByteBuffer(samples * 2); // 16-bit = 2 bytes
			converted.order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

			audioData.rewind();

			for (int i = 0; i < samples; i++) {
				int sampleValue = 0;

				if (bitsPerSample == 24) {
					// Read 24-bit sample
					byte b1 = audioData.get();
					byte b2 = audioData.get();
					byte b3 = audioData.get();

					if (isBigEndian) {
						sampleValue = ((b1 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | (b3 & 0xFF);
					} else {
						sampleValue = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF) << 16);
					}

					// Sign extend
					if (sampleValue >= 0x800000) {
						sampleValue -= 0x1000000;
					}

					// Convert to 16-bit
					sampleValue >>= 8;

				} else if (bitsPerSample == 32) {
					// Read 32-bit sample
					byte b1 = audioData.get();
					byte b2 = audioData.get();
					byte b3 = audioData.get();
					byte b4 = audioData.get();

					if (isBigEndian) {
						sampleValue = ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
					} else {
						sampleValue = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
					}

					// Convert to 16-bit
					sampleValue >>= 16;
				}

				// Clamp to 16-bit range
				sampleValue = Math.max(-32768, Math.min(32767, sampleValue));

				// Write as short
				converted.putShort((short) sampleValue);
			}

			converted.flip();
			return converted;
		}

		/**
		 * Convert audio data to native byte order
		 */
		private static ByteBuffer convertToNativeOrder(ByteBuffer audioData, boolean is16Bit, ByteOrder sourceOrder) {
			ByteBuffer result = BufferUtils.createByteBuffer(audioData.remaining());
			result.order(ByteOrder.nativeOrder());

			audioData.rewind();
			ByteBuffer source = audioData.duplicate();
			source.order(sourceOrder);

			if (is16Bit) {
				// Convert 16-bit samples
				while (source.hasRemaining()) {
					result.putShort(source.getShort());
				}
			} else {
				// 8-bit data is not affected by byte order
				result.put(source);
			}

			result.flip();
			return result;
		}

		/**
		 * Helper method to read all bytes from an InputStream
		 */
		private static byte[] readAllBytes(InputStream inputStream) throws IOException {
			byte[] buffer = new byte[8192];
			int bytesRead;
			int totalBytes = 0;
			byte[] result = new byte[0];

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				final var newResult = new byte[totalBytes + bytesRead];
				System.arraycopy(result, 0, newResult, 0, totalBytes);
				System.arraycopy(buffer, 0, newResult, totalBytes, bytesRead);
				result = newResult;
				totalBytes += bytesRead;
			}

			return result;
		}
	}

	// --------------------------------------
	// Static
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(String name, InputStream inputStream) {
		if (isLoaded())
			return false;

		mName = name;
		mBufferID = AL10.alGenBuffers();

		final var lWaveAudioData = AudioData.create(inputStream);

		if (lWaveAudioData == null)
			return false;

		AL10.alBufferData(mBufferID, lWaveAudioData.format, lWaveAudioData.data, lWaveAudioData.samplerate);

		mSize = AL10.alGetBufferi(mBufferID, AL10.AL_SIZE);
		mBitsPerSample = lWaveAudioData.sizeInBits;
		mFrequency = lWaveAudioData.samplerate;
		mChannels = lWaveAudioData.channels;

		final var lLengthInSamples = mSize * 8 / (1f * mBitsPerSample);
		mDurationInSeconds = (float) lLengthInSamples / (float) mFrequency;

		if (ConstantsApp.getBooleanValueDef("DEBUG_AUDIO_ENABLED", false)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioEntity Name: " + name);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Size: " + mSize);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Frequency: " + mFrequency);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Channels: " + mChannels);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "mBitsPerSample: " + mBitsPerSample);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Duration (Seconds): " + mDurationInSeconds);
			Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");

		}

		lWaveAudioData.dispose();

		return true;

	}

}
