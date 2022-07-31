package net.lintford.library.core.noisebuilder;

public class NoiseBuilderCache extends NoiseBuilderModuleBase {

	public class ModuleCache {
		public float x, y, z;
		public float value;
		boolean valid;

		ModuleCache() {
			valid = false;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final NoiseBuilderScaler mSource = new NoiseBuilderScaler(0);
	private final ModuleCache mCache2 = new ModuleCache();
	private final ModuleCache mCache3 = new ModuleCache();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public NoiseBuilderCache() {

	}

	public NoiseBuilderCache(float v) {
		mSource.set(v);
	}

	public NoiseBuilderCache(NoiseBuilderModuleBase b) {
		mSource.set(b);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setSource(float v) {
		mSource.set(v);
	}

	public void setSource(NoiseBuilderModuleBase b) {
		mSource.set(b);
	}

	@Override
	public float get(float x, float y) {
		if (!mCache2.valid || mCache2.x != x || mCache2.y != y) {
			mCache2.x = x;
			mCache2.y = y;
			mCache2.valid = true;
			mCache2.value = mSource.get(x, y);
			return mCache2.value;
		}
		return mCache2.value;
	}

	@Override
	public float get(float x, float y, float z) {
		if (!mCache3.valid || mCache3.x != x || mCache3.y != y || mCache3.z != z) {
			mCache3.x = x;
			mCache3.y = y;
			mCache3.z = z;
			mCache3.valid = true;
			mCache3.value = mSource.get(x, y, z);
		}
		return mCache3.value;
	}

}
