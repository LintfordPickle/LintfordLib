package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderScaleOffset extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final NoiseBuilderScaler mSource = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mScale = new NoiseBuilderScaler(0);
	protected final NoiseBuilderScaler mOffset = new NoiseBuilderScaler(0);

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderScaleOffset() {
		mSource.set(0);
		mScale.set(0);
		mOffset.set(0);
	}

	public NoiseBuilderScaleOffset(float source, float scale) {
		this(source, scale, 0);
	}

	public NoiseBuilderScaleOffset(float source, float scale, float offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(float source, float scale, NoiseBuilderModuleBase offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(float source, NoiseBuilderModuleBase scale) {
		this(source, scale, 0);
	}

	public NoiseBuilderScaleOffset(float source, NoiseBuilderModuleBase scale, float offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(float source, NoiseBuilderModuleBase scale, NoiseBuilderModuleBase offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, float scale) {
		this(source, scale, 0);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, float scale, float offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, float scale, NoiseBuilderModuleBase offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, NoiseBuilderModuleBase scale) {
		this(source, scale, 0);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, NoiseBuilderModuleBase scale, float offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
	}

	public NoiseBuilderScaleOffset(NoiseBuilderModuleBase source, NoiseBuilderModuleBase scale, NoiseBuilderModuleBase offset) {
		mSource.set(source);
		mScale.set(scale);
		mOffset.set(offset);
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

	public void setScale(float v) {
		mScale.set(v);
	}

	public void setScale(NoiseBuilderModuleBase b) {
		mScale.set(b);
	}

	public void setOffset(float v) {
		mOffset.set(v);
	}

	public void setOffset(NoiseBuilderModuleBase b) {
		mOffset.set(b);
	}

	@Override
	public float get(float x, float y) {
		return mSource.get(x, y) * mScale.get(x, y) + mOffset.get(x, y);
	}

	@Override
	public float get(float x, float y, float z) {
		return mSource.get(x, y, z) * mScale.get(x, y, z) + mOffset.get(x, y, z);
	}

}