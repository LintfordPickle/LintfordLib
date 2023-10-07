package net.lintfordlib.options;

import net.lintfordlib.core.maths.MathHelper;

public class GraphicsSettings {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	static final GraphicsSettings createBasicTemplate() {
		final var lBasic = new GraphicsSettings();

		lBasic.mUiUserScale = 1f;
		lBasic.mUiUserTextScale = 1f;
		lBasic.mUiUserTransparency = .8f;

		return lBasic;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Normalized values only
	private float mUiUserScale;
	private float mUiUserTextScale;
	private float mUiUserTransparency;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float UiUserScale() {
		return mUiUserScale;
	}

	public void setUiUserScale(float newValue) {
		mUiUserScale = MathHelper.clamp(newValue, 0.75f, 1.5f);
	}

	public float UiUserTextScale() {
		return mUiUserTextScale;
	}

	public void setUiUserTextScale(float newValue) {
		mUiUserTextScale = MathHelper.clamp(newValue, 0.75f, 1.5f);
	}

	public float UiUserTransparencyScale() {
		return mUiUserTransparency;
	}

	public void setUiUserTransparencyScale(float newValue) {
		mUiUserTransparency = MathHelper.clamp(newValue, 0.75f, 1.5f);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GraphicsSettings() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public GraphicsSettings(GraphicsSettings graphicsSettingsToCopy) {
		this.copy(graphicsSettingsToCopy);
	}

	public void copy(GraphicsSettings graphicsSettingsToCopy) {
		this.mUiUserScale = graphicsSettingsToCopy.UiUserScale();
		this.mUiUserTextScale = graphicsSettingsToCopy.UiUserTextScale();
		this.mUiUserTransparency = graphicsSettingsToCopy.UiUserTransparencyScale();
	}

	public boolean isDifferent(GraphicsSettings otherGrpahicsSettingsToCheckAgainst) {
		if (otherGrpahicsSettingsToCheckAgainst == null)
			return true;

		// @formatter:off
		return mUiUserScale != otherGrpahicsSettingsToCheckAgainst.UiUserScale() 
			|| mUiUserTextScale != otherGrpahicsSettingsToCheckAgainst.UiUserTextScale() 
			|| mUiUserTransparency != otherGrpahicsSettingsToCheckAgainst.UiUserTransparencyScale();
	}
}
