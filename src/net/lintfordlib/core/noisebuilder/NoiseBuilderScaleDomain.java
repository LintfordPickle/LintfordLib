package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderScaleDomain extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final NoiseBuilderScaler mSource = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mSx = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mSy = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mSz = new NoiseBuilderScaler(0);

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderScaleDomain() {
		mSource.set(1);
		mSx.set(1);
		mSy.set(1);
		mSz.set(1);
	}

	public NoiseBuilderScaleDomain(float source, float x, float y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, float x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, float x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, float x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, NoiseBuilderModuleBase x, float y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, NoiseBuilderModuleBase x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(float source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, float x, float y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, float x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, float x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, float x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, float y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
	}

	public NoiseBuilderScaleDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mSx.set(x);
		mSy.set(y);
		mSz.set(z);
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

	public void setScale(float x, float y) {
		mSx.set(x);
		mSy.set(y);
	};

	public void setXScale(float x) {
		mSx.set(x);
	}

	public void setYScale(float y) {
		mSy.set(y);
	}

	public void setZScale(float z) {
		mSz.set(z);
	}

	public void setXScale(NoiseBuilderModuleBase x) {
		mSx.set(x);
	}

	public void setYScale(NoiseBuilderModuleBase y) {
		mSy.set(y);
	}

	public void setZScale(NoiseBuilderModuleBase z) {
		mSz.set(z);
	}

	@Override
	public float get(float x, float y) {
		return mSource.get(x * mSx.get(x, y), y * mSy.get(x, y));
	}

	@Override
	public float get(float x, float y, float z) {
		return mSource.get(x * mSx.get(x, y, z), y * mSy.get(x, y, z), z * mSz.get(x, y, z));
	}

}