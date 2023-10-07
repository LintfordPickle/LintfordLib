package net.lintfordlib.core.audio.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
		AudioFormat audioformat = audioInputStream.getFormat();

		// get channels
		int channels = 0;
		if (audioformat.getChannels() == 1) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_MONO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_MONO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else if (audioformat.getChannels() == 2) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_STEREO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_STEREO16;
			} else {
				assert false : "Illegal sample size";
			}
			Debug.debugManager().logger().w(AudioManager.class.getSimpleName(), "WAV has stereo sound - this sound will not be played in 3d space");
		} else {
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Only mono or stereo sound files are supported.");
			return null;
		}

		// read data into buffer
		ByteBuffer buffer = null;
		try {
			int available = audioInputStream.available();
			if (available <= 0) {
				available = audioInputStream.getFormat().getChannels() * (int) audioInputStream.getFrameLength() * audioInputStream.getFormat().getSampleSizeInBits() / 8;
			}
			byte[] buf = new byte[audioInputStream.available()];
			int read = 0, total = 0;
			while ((read = audioInputStream.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
				total += read;
			}
			buffer = convertAudioBytes(buf, audioformat.getSampleSizeInBits() == 16, audioformat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		} catch (IOException ioe) {
			return null;
		} finally {
			try {
				audioInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new WaveData(buffer, channels, (int) audioformat.getSampleRate(), channels, audioformat.getSampleSizeInBits());
	}

	private static ByteBuffer convertAudioBytes(byte[] bytes, boolean twoBytesData, ByteOrder order) {
		ByteBuffer dest = ByteBuffer.allocateDirect(bytes.length);
		dest.order(ByteOrder.nativeOrder());
		ByteBuffer src = ByteBuffer.wrap(bytes);
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