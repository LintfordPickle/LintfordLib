package net.lintford.library.core.noisebuilder;

import net.lintford.library.core.maths.MathHelper;

public class NoiseBuilderSelect extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final NoiseBuilderScaler mLow = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mHigh = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mControl = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mThreshold = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mFalloff = new NoiseBuilderScaler(0);

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderSelect() {
		mLow.set(0);
		mHigh.set(0);
		mControl.set(0);
		mThreshold.set(0);
		mFalloff.set(0);
	}

	public NoiseBuilderSelect(float low, float high, float control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, float high, float control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, float high, float control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	// --------------------------------------

	public NoiseBuilderSelect(float low, float high, float control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, float high, float control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, float high, float control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	// --------------------------------------

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	// --------------------------------------

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(float low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	// --------------------------------------

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, float control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, float high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, float control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, float threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold) {
		this(low, high, control, threshold, 0);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, float falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	public NoiseBuilderSelect(NoiseBuilderModuleBase low, NoiseBuilderModuleBase high, NoiseBuilderModuleBase control, NoiseBuilderModuleBase threshold, NoiseBuilderModuleBase falloff) {
		mLow.set(low);
		mHigh.set(high);
		mControl.set(control);
		mThreshold.set(threshold);
		mFalloff.set(falloff);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setLowSource(NoiseBuilderModuleBase b) {
		mLow.set(b);
	}

	public void setHighSource(NoiseBuilderModuleBase b) {
		mHigh.set(b);
	}

	public void setControlSource(NoiseBuilderModuleBase b) {
		mControl.set(b);
	}

	public void setLowSource(float v) {
		mLow.set(v);
	}

	public void setHighSource(float v) {
		mHigh.set(v);
	}

	public void setControlSource(float v) {
		mControl.set(v);
	}

	public void setThreshold(float t) {
		mThreshold.set(t);
	}

	public void setFalloff(float f) {
		mFalloff.set(f);
	}

	public void setThreshold(NoiseBuilderModuleBase m) {
		mThreshold.set(m);
	}

	public void setFalloff(NoiseBuilderModuleBase m) {
		mFalloff.set(m);
	}

	@Override
	public float get(float x, float y) {
		float control = mControl.get(x, y);
		float falloff = mFalloff.get(x, y);
		float threshold = mThreshold.get(x, y);

		if (falloff > 0.0) {
			if (control < (threshold - falloff)) {
				return mLow.get(x, y);
			} else if (control > (threshold + falloff)) {
				return mHigh.get(x, y);
			} else {
				float lower = threshold - falloff;
				float upper = threshold + falloff;
				float blendAmount = quintic_blend((control - lower) / (upper - lower));
				return MathHelper.lerp(mLow.get(x, y), mHigh.get(x, y), blendAmount);
			}
		} else {
			if (control < threshold)
				return mLow.get(x, y);
			else
				return mHigh.get(x, y);
		}
	}

	private final float quintic_blend(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	@Override
	public float get(float x, float y, float z) {
		float control = mControl.get(x, y, z);
		float falloff = mFalloff.get(x, y, z);
		float threshold = mThreshold.get(x, y, z);

		if (falloff > 0.0) {
			if (control < (threshold - falloff)) {
				// Lies outside of falloff area below threshold, return first source
				return mLow.get(x, y, z);
			} else if (control > (threshold + falloff)) {
				// Lise outside of falloff area above threshold, return second source
				return mHigh.get(x, y, z);
			} else {
				// Lies within falloff area.
				float lower = threshold - falloff;
				float upper = threshold + falloff;
				float blendAmount = quintic_blend((control - lower) / (upper - lower));
				return MathHelper.lerp(mLow.get(x, y, z), mHigh.get(x, y, z), blendAmount);
			}
		} else {
			if (control < threshold)
				return mLow.get(x, y, z);
			else
				return mHigh.get(x, y, z);
		}
	}

}
