package net.lintfordlib.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFWGamepadState;

public class GamepadInputMap {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_MAPPING = -1;

	public static final GamepadInputMap empty = new GamepadInputMap();

	public enum GameInputType {
		None, button, axis,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameInputType mMappedToType;
	private int mMappedTo;
	private float mMappedToSignum; // -/0/+

	private boolean mIsInitialized;
	private float mMinValue;
	private float mMaxValue;
	private float mValue;
	private float mDefaultValue;
	private boolean mIsInverted;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public GameInputType mappedToType() {
		return mMappedToType;
	}

	public int mappedTo() {
		return mMappedTo;
	}

	public float mappedToSignum() {
		return mMappedToSignum;
	}

	public void mapToButton(int buttonIndex) {
		mMappedToType = GameInputType.button;
		mMappedTo = buttonIndex;

	}

	public void mapToAxis(int axisIndex, float signum) {
		mMappedToType = GameInputType.axis;
		mMappedTo = axisIndex;
		mMappedToSignum = Math.signum(signum);
	}

	public boolean isInvert() {
		return mIsInverted;
	}

	public void isInvert(boolean isInverted) {
		mIsInverted = isInverted;
	}

	public float min() {
		return mMinValue;
	}

	public float max() {
		return mMaxValue;
	}

	public float defaultValue() {
		return mDefaultValue;
	}

	public float value() {
		return mValue;
	}

	public boolean isDown() {
		return mMappedToSignum < 0 ? (mValue < -0.5f) : (mValue > 0.5f);
	}

	public float valueAdjusted() {
		// TODO: Adjust for inversion

		if (mMappedToSignum == 0)
			return 0; // TODO: Float equality with zero
		if (mMappedToSignum < 0) {
			if (mValue > 0)
				return 0.f;
			return mValue;
		} else {
			if (mValue < 0.f)
				return 0.f;
			return mValue;
		}

	}

	public boolean isValueSet() {
		return Math.abs(mValue - mDefaultValue) > 0.005f;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	GamepadInputMap() {
		mMappedToType = GameInputType.None;
		mIsInitialized = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialize(float min, float max, float defaultValue) {
		mIsInitialized = true;
		mMinValue = min;
		mMaxValue = max;
		mDefaultValue = defaultValue;
	}

	public void reset() {

	}

	public void updateSdl(GLFWGamepadState gamepadState) {

		// TODO: update input state (sdl)

	}

	public void updateRaw(ByteBuffer buttons, FloatBuffer axisBuffer) {
		// TODO: update input state (raw)
		switch (mMappedToType) {
		default:
		case None:
			mValue = mDefaultValue;

			break;

		case button:
			final var mappedToButtonIndex = mappedTo();
			if (mappedToButtonIndex == NO_MAPPING)
				return;

			if (mappedToButtonIndex < 0 || mappedToButtonIndex >= buttons.limit())
				return; // maybe set the mappedToButtonIndex back to NO_MAPPING?

			mValue = buttons.get(mappedToButtonIndex);

			break;

		case axis:
			final var mappedToAxisIndex = mappedTo();
			if (mappedToAxisIndex == NO_MAPPING)
				return;

			if (mappedToAxisIndex < 0 || mappedToAxisIndex >= axisBuffer.limit())
				return; // maybe set the mappedToButtonIndex back to NO_MAPPING?

			final var axisValue = axisBuffer.get(mappedToAxisIndex);

			if (Math.abs(axisValue) < 0.02f) {
				mValue = 0.f;
				return;
			}

			mValue = Math.signum(axisValue);
			break;
		}

	}

}
