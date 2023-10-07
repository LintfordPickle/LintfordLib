package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderTranslateDomain extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final NoiseBuilderScaler mSource = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAX = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAY = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAZ = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAW = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAU = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mAV = new NoiseBuilderScaler(0);

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public NoiseBuilderTranslateDomain() {

	}

	public NoiseBuilderTranslateDomain(float source, float x, float y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, float x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, float x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, float x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, NoiseBuilderModuleBase x, float y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, NoiseBuilderModuleBase x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(float source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, float x, float y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, float x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, float x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, float x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, float y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, float y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, float z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	public NoiseBuilderTranslateDomain(NoiseBuilderModuleBase source, NoiseBuilderModuleBase x, NoiseBuilderModuleBase y, NoiseBuilderModuleBase z) {
		mSource.set(source);
		mAX.set(x);
		mAY.set(y);
		mAZ.set(z);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setXAxisSource(float v) {
		mAX.set(v);
	}

	public void setXAxisSource(NoiseBuilderModuleBase b) {
		mAX.set(b);
	}

	public void setYAxisSource(float v) {
		mAY.set(v);
	}

	public void setYAxisSource(NoiseBuilderModuleBase b) {
		mAY.set(b);
	}

	public void setZAxisSource(float v) {
		mAZ.set(v);
	}

	public void setZAxisSource(NoiseBuilderModuleBase b) {
		mAZ.set(b);
	}

	public void setWAxisSource(float v) {
		mAW.set(v);
	}

	public void setWAxisSource(NoiseBuilderModuleBase b) {
		mAW.set(b);
	}

	public void setUAxisSource(float v) {
		mAU.set(v);
	}

	public void setUAxisSource(NoiseBuilderModuleBase b) {
		mAU.set(b);
	}

	public void setVAxisSource(float v) {
		mAV.set(v);
	}

	public void setVAxisSource(NoiseBuilderModuleBase b) {
		mAV.set(b);
	}

	// --------------------------------------

	@Override
	public float get(float x, float y) {
		return mSource.get(x + mAX.get(x, y), y + mAY.get(x, y));
	}

	@Override
	public float get(float x, float y, float z) {
		return mSource.get(x + mAX.get(x, y, z), y + mAY.get(x, y, z), z + mAZ.get(x, y, z));
	}

}
