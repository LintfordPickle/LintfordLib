package net.lintfordlib.core.audio.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.openal.AL10;

import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.debug.Debug;

/* 
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are 
 * met:
 * 
 * * Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of 
 *   its contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Utility class for loading WAV files.
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$ $Id$
 */
public class WaveData {
	/** actual wave data */
	public final ByteBuffer data;

	/** format type of data */
	public final int format;

	/** sample rate of data */
	public final int samplerate;

	/** number of channels in data */
	public final int channels;

	public final int sizeInBits;

	/**
	 * Creates a new WaveData
	 * 
	 * @param data       actual wavedata
	 * @param format     format of wave data
	 * @param samplerate sample rate of data
	 */
	private WaveData(ByteBuffer data, int format, int samplerate, int channels, int sizeInBits) {
		this.data = data;
		this.format = format;
		this.samplerate = samplerate;
		this.channels = channels;
		this.sizeInBits = sizeInBits;
	}

	/** Disposes the {@link WaveData}. */
	public void dispose() {
		data.clear();
	}

	/**
	 * Creates a WaveData container from the specified inputstream
	 * 
	 * @param inputStream InputStream to read from
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(InputStream inputStream) {
		try {
			return create(AudioSystem.getAudioInputStream(inputStream));
		} catch (Exception e) {
			Debug.debugManager().logger().e(WaveData.class.getSimpleName(), "Unable to create from inputstream, " + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a WaveData container from the specified bytes
	 *
	 * @param buffer array of bytes containing the complete wave file
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(byte[] buffer) {
		try {
			return create(AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer))));
		} catch (Exception e) {
			Debug.debugManager().logger().e(WaveData.class.getSimpleName(), "Unable to create from byte array, " + e.getMessage());
			Debug.debugManager().logger().printException(WaveData.class.getSimpleName(), e);

			return null;

		}
	}

	/**
	 * Creates a WaveData container from the specified ByetBuffer. If the buffer is backed by an array, it will be used directly, else the contents of the buffer will be copied using get(byte[]).
	 *
	 * @param buffer ByteBuffer containing sound file
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(ByteBuffer buffer) {
		try {
			byte[] bytes = null;

			if (buffer.hasArray()) {
				bytes = buffer.array();
			} else {
				bytes = new byte[buffer.capacity()];
				buffer.get(bytes);
			}
			return create(bytes);
		} catch (Exception e) {
			Debug.debugManager().logger().e(WaveData.class.getSimpleName(), "Unable to create from ByteBuffer, " + e.getMessage());
			Debug.debugManager().logger().printException(WaveData.class.getSimpleName(), e);

			return null;

		}
	}

	/**
	 * Creates a WaveData container from the specified stream
	 * 
	 * @param audioInputStream AudioInputStream to read from
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(AudioInputStream audioInputStream) {
		// get format of data
		final var audioformat = audioInputStream.getFormat();

		if (audioformat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED && audioformat.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported audio encoding: " + audioformat.getEncoding() + ". Only PCM is supported.");
			return null;
		}

		float sampleRate = audioformat.getSampleRate();
		if (sampleRate != 8000 && sampleRate != 11025 && sampleRate != 22050 && sampleRate != 44100 && sampleRate != 48000 && sampleRate != 96000) {
			Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "Unusual sample rate: " + sampleRate + "Hz. This may not play correctly on all systems.");
		}

		int alFormat = 0;
		int channels = audioformat.getChannels();
		int sampleSizeInBits = audioformat.getSampleSizeInBits();

		if (channels == 1) {
			switch (sampleSizeInBits) {
			case 8:
				alFormat = AL10.AL_FORMAT_MONO8;
				break;
			case 16:
				alFormat = AL10.AL_FORMAT_MONO16;
				break;
			case 24:
				// 24-bit not directly supported by OpenAL core - convert to 16-bit
				Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "24-bit audio will be converted to 16-bit (OpenAL core limitation)");
				alFormat = AL10.AL_FORMAT_MONO16;
				break;
			case 32:
				// Check if float32 extension is available (pseudo-code - implementation depends on your OpenAL wrapper)
				// if (AL.isExtensionPresent("AL_EXT_float32")) {
				//     alFormat = AL_EXT_float32.AL_FORMAT_MONO_FLOAT32;
				// } else {
				Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "32-bit audio will be converted to 16-bit (AL_EXT_float32 not available)");
				alFormat = AL10.AL_FORMAT_MONO16;
				// }
				break;
			default:
				Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported sample size: " + sampleSizeInBits + " bits");
				return null;
			}
		} else if (audioformat.getChannels() == 2) {
			switch (sampleSizeInBits) {
			case 8:
				alFormat = AL10.AL_FORMAT_STEREO8;
				break;
			case 16:
				alFormat = AL10.AL_FORMAT_STEREO16;
				break;
			case 24:
				Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "24-bit stereo will be converted to 16-bit");
				alFormat = AL10.AL_FORMAT_STEREO16;
				break;
			case 32:
				Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "32-bit stereo will be converted to 16-bit");
				alFormat = AL10.AL_FORMAT_STEREO16;
				break;
			default:
				Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Unsupported sample size: " + sampleSizeInBits + " bits");
				return null;
			}

			Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "WAV has stereo sound - this sound will not be played in 3d space");
		} else if (channels > 2) {
			// Multi-channel audio (5.1, 7.1, etc.)
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Multi-channel audio (" + channels + " channels) is not supported. " + "Use mono for 3D positioned audio or stereo for background music.");
			return null;
		} else {
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Invalid channel count: " + channels);
			return null;
		}

		// read data into buffer
		ByteBuffer buffer = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] tempBuffer = new byte[8192];
			int bytesRead;

			while ((bytesRead = audioInputStream.read(tempBuffer)) != -1) {
				baos.write(tempBuffer, 0, bytesRead);
			}

			byte[] audioData = baos.toByteArray();

			// Convert audio data, handling bit depth conversion if needed
			if (sampleSizeInBits == 24 || sampleSizeInBits == 32) {
				audioData = convertToSixteenBit(audioData, audioformat);
				sampleSizeInBits = 16; // Update for the conversion result
			}

			buffer = convertAudioBytes(audioData, sampleSizeInBits == 16, audioformat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		} catch (IOException ioe) {
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Failed to read audio data: " + ioe.getMessage());
			return null;
		} finally {
			try {
				audioInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new WaveData(buffer, alFormat, (int) audioformat.getSampleRate(), alFormat, sampleSizeInBits);
	}

	/**
	 * Convert 24-bit or 32-bit audio to 16-bit
	 */
	private static byte[] convertToSixteenBit(byte[] audioData, AudioFormat format) {
		int sampleSizeInBits = format.getSampleSizeInBits();
		int bytesPerSample = sampleSizeInBits / 8;
		boolean isBigEndian = format.isBigEndian();

		int samples = audioData.length / bytesPerSample;
		byte[] converted = new byte[samples * 2]; // 16-bit = 2 bytes per sample

		for (int i = 0; i < samples; i++) {
			int sampleValue = 0;

			if (sampleSizeInBits == 24) {
				// Read 24-bit sample
				if (isBigEndian) {
					sampleValue = ((audioData[i * 3] & 0xFF) << 16) | ((audioData[i * 3 + 1] & 0xFF) << 8) | (audioData[i * 3 + 2] & 0xFF);
					if (sampleValue >= 0x800000)
						sampleValue -= 0x1000000; // Sign extend
				} else {
					sampleValue = (audioData[i * 3] & 0xFF) | ((audioData[i * 3 + 1] & 0xFF) << 8) | ((audioData[i * 3 + 2] & 0xFF) << 16);
					if (sampleValue >= 0x800000)
						sampleValue -= 0x1000000; // Sign extend
				}
				// Convert 24-bit to 16-bit by shifting right 8 bits
				sampleValue >>= 8;
			} else if (sampleSizeInBits == 32) {
				// Read 32-bit sample (assuming PCM integer, not float)
				if (isBigEndian) {
					sampleValue = ((audioData[i * 4] & 0xFF) << 24) | ((audioData[i * 4 + 1] & 0xFF) << 16) | ((audioData[i * 4 + 2] & 0xFF) << 8) | (audioData[i * 4 + 3] & 0xFF);
				} else {
					sampleValue = (audioData[i * 4] & 0xFF) | ((audioData[i * 4 + 1] & 0xFF) << 8) | ((audioData[i * 4 + 2] & 0xFF) << 16) | ((audioData[i * 4 + 3] & 0xFF) << 24);
				}
				// Convert 32-bit to 16-bit by shifting right 16 bits
				sampleValue >>= 16;
			}

			// Clamp to 16-bit range
			sampleValue = Math.max(-32768, Math.min(32767, sampleValue));

			// Write 16-bit sample
			if (isBigEndian) {
				converted[i * 2] = (byte) (sampleValue >> 8);
				converted[i * 2 + 1] = (byte) (sampleValue & 0xFF);
			} else {
				converted[i * 2] = (byte) (sampleValue & 0xFF);
				converted[i * 2 + 1] = (byte) (sampleValue >> 8);
			}
		}

		return converted;
	}

	private static ByteBuffer convertAudioBytes(byte[] bytes, boolean twoBytesData, ByteOrder order) {

		final var dest = ByteBuffer.allocateDirect(bytes.length);
		dest.order(ByteOrder.nativeOrder());

		final var src = ByteBuffer.wrap(bytes);
		src.order(order);
		if (twoBytesData) {
			ShortBuffer dest_short = dest.asShortBuffer();
			ShortBuffer src_short = src.asShortBuffer();
			while (src_short.hasRemaining())
				dest_short.put(src_short.get());
		} else {
			while (src.hasRemaining())
				dest.put(src.get());
		}
		dest.rewind();
		return dest;
	}
}