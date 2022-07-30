package net.lintford.library.core.noisebuilder;

public class NoiseBuilderCombine extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MaxSources = 4;

	public enum Function {
		Add, Mul, Max, Min, Avg,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private NoiseBuilderModuleBase[] mSources = new NoiseBuilderModuleBase[MaxSources];

	private Function mCombinerOp = Function.Add;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderCombine(Function type, NoiseBuilderModuleBase... sources) {
		mCombinerOp = type;

		final int numSources = Math.min(MaxSources, sources.length);
		for (int i = 0; i < numSources; i++) {
			mSources[i] = sources[i];
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float get(float x, float y) {
		switch (mCombinerOp) {
		case Add:
			return getAdd(x, y);
		case Mul:
			return getMult(x, y);
		case Max:
			return getMax(x, y);
		case Min:
			return getMin(x, y);
		case Avg:
			return getAvg(x, y);
		default:
			return 0.0f;
		}
	}

	public float get(float x, float y, float z) {
		switch (mCombinerOp) {
		case Add:
			return getAdd(x, y, z);
		case Mul:
			return getMult(x, y, z);
		case Max:
			return getMax(x, y, z);
		case Min:
			return getMin(x, y, z);
		case Avg:
			return getAvg(x, y, z);
		default:
			return 0.0f;
		}
	}

	// --------------------------------------

	private float getAdd(float x, float y) {
		float value = 0;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null)
				value += mSources[c].get(x, y);
		}
		return value;
	}

	private float getAdd(float x, float y, float z) {
		float value = 0;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null)
				value += mSources[c].get(x, y, z);
		}
		return value;
	}

	private float getMult(float x, float y) {
		float value = 1.0f;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null)
				value *= mSources[c].get(x, y);
		}
		return value;
	}

	private float getMult(float x, float y, float z) {
		float value = 1.0f;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null)
				value *= mSources[c].get(x, y, z);
		}
		return value;
	}

	private float getMin(float x, float y) {
		float mn;
		int c = 0;
		while (c < MaxSources && mSources[c] != null)
			++c;
		if (c == MaxSources)
			return 0.0f;
		mn = mSources[c].get(x, y);

		for (int d = c; d < MaxSources; ++d) {
			if (mSources[d] != null) {
				float v = mSources[d].get(x, y);
				if (v < mn)
					mn = v;
			}
		}

		return mn;
	}

	private float getMin(float x, float y, float z) {
		float mn;
		int c = 0;
		while (c < MaxSources && mSources[c] != null)
			++c;
		if (c == MaxSources)
			return 0.0f;
		mn = mSources[c].get(x, y, z);

		for (int d = c; d < MaxSources; ++d) {
			if (mSources[d] != null) {
				float v = mSources[d].get(x, y, z);
				if (v < mn)
					mn = v;
			}
		}

		return mn;
	}

	private float getMax(float x, float y) {
		float mn;
		int c = 0;
		while (c < MaxSources && mSources[c] != null)
			++c;
		if (c == MaxSources)
			return 0.0f;
		mn = mSources[c].get(x, y);

		for (int d = c; d < MaxSources; ++d) {
			if (mSources[d] != null) {
				float val = mSources[d].get(x, y);
				if (val > mn)
					mn = val;
			}
		}

		return mn;
	}

	private float getMax(float x, float y, float z) {
		float mn;
		int c = 0;
		while (c < MaxSources && mSources[c] != null)
			++c;
		if (c == MaxSources)
			return 0.0f;
		mn = mSources[c].get(x, y, z);

		for (int d = c; d < MaxSources; ++d) {
			if (mSources[d] != null) {
				float val = mSources[d].get(x, y, z);
				if (val > mn)
					mn = val;
			}
		}

		return mn;
	}

	private float getAvg(float x, float y) {
		float count = 0;
		float value = 0;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null) {
				value += mSources[c].get(x, y);
				count += 1.0;
			}
		}

		if (count == 0.0f)
			return 0.0f;

		return value / count;
	}

	private float getAvg(float x, float y, float z) {
		float count = 0;
		float value = 0;
		for (int c = 0; c < MaxSources; ++c) {
			if (mSources[c] != null) {
				value += mSources[c].get(x, y, z);
				count += 1.0;
			}
		}

		if (count == 0.0f)
			return 0.0f;

		return value / count;
	}

}
