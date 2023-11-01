package net.lintfordlib.core.noisebuilder;

import net.lintfordlib.core.maths.InterpolationHelper;

public class NoiseBuilderMath extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum MathOp {
		Bias,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final NoiseBuilderScaler mSource = new NoiseBuilderScaler(0);
	private final NoiseBuilderScaler mParameter = new NoiseBuilderScaler(0);
	private MathOp mMathOperation = MathOp.Bias;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderMath(MathOp mathOp, float source, float parameter) {
		mMathOperation = mathOp;
		mSource.set(source);
		mParameter.set(parameter);
	}

	public NoiseBuilderMath(MathOp mathOp, float source, NoiseBuilderModuleBase parameter) {
		mMathOperation = mathOp;
		mSource.set(source);
		mParameter.set(parameter);
	}

	public NoiseBuilderMath(MathOp mathOp, NoiseBuilderModuleBase source, float parameter) {
		mMathOperation = mathOp;
		mSource.set(source);
		mParameter.set(parameter);
	}

	public NoiseBuilderMath(MathOp mathOp, NoiseBuilderModuleBase source, NoiseBuilderModuleBase parameter) {
		mMathOperation = mathOp;
		mSource.set(source);
		mParameter.set(parameter);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float get(float x, float y) {
		final float value = mSource.get(x, y);
		final float para = mParameter.get(x, y);
		return applyMathOperation(value, para);
	}

	public float get(float x, float y, float z) {
		final float value = mSource.get(x, y, z);
		final float para = mParameter.get(x, y, z);
		return applyMathOperation(value, para);
	}

	private float applyMathOperation(float v, float p) {
		switch (mMathOperation) {
		case Bias:
			return InterpolationHelper.bias(v, p);
		default:
			return v;
		}
	}

}
