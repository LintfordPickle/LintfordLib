package net.lintford.library.options;

import net.lintford.library.core.maths.MathHelper;

public class GraphicsSettings {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	static final GraphicsSettings createBasicTemplate() {
		final var lBasic = new GraphicsSettings();

		lBasic.mUIScale = 1f;
		lBasic.mUITextScale = 1f;
		lBasic.mUITransparency = .8f;

		return lBasic;
	}

	/** These is the factor by which to scale UI elements and text by when in the 'Big' UI mode (i..e when the window dimensions are normal/high). */
	public static final float BIG_UI_SCALE_FACTOR = 1f;

	/** These is the factor by which to scale UI elements and text by when in the 'small' UI mode (i..e when the window dimensions are quite small). */
	public static final float SMALL_UI_SCALE_FACTOR = 0.8f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Normalized values only
	private float mUIScale;
	private float mUITextScale;
	private float mUITransparency;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float UIScale() {
		return mUIScale;
	}

	public void setUIScale(float newValue) {
		mUIScale = MathHelper.clamp(newValue, 0.75f, 1.5f);
	}

	public float UITextScale() {
		return mUITextScale;
	}

	public void setUITextScale(float newValue) {
		mUITextScale = MathHelper.clamp(newValue, 0.75f, 1.5f);
	}

	public float UITransparencyScale() {
		return mUITransparency;
	}

	public void setUITransparencyScale(float newValue) {
		mUITransparency = MathHelper.clamp(newValue, 0.75f, 1.5f);
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
		this.mUIScale = graphicsSettingsToCopy.mUIScale;
		this.mUITextScale = graphicsSettingsToCopy.mUITextScale;
		this.mUITransparency = graphicsSettingsToCopy.mUITransparency;
	}

	public boolean isDifferent(GraphicsSettings otherGrpahicsSettingsToCheckAgainst) {
		if (otherGrpahicsSettingsToCheckAgainst == null)
			return true;

		// @formatter:off
		return mUIScale != otherGrpahicsSettingsToCheckAgainst.mUIScale 
			|| mUITextScale != otherGrpahicsSettingsToCheckAgainst.mUITextScale 
			|| mUITransparency != otherGrpahicsSettingsToCheckAgainst.mUITransparency;
		// @formatter:on
	}
}
