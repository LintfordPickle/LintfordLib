package net.ld.library.core.graphics;

import net.ld.library.core.config.DisplayConfig;

public class ResourceManager {

	// =============================================
	// Variables
	// =============================================

	protected DisplayConfig mDisplayConfig;

	// =============================================
	// Properties
	// =============================================

	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	// =============================================
	// Constructor
	// =============================================

	public ResourceManager(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;
		
	}

	// =============================================
	// Core-Method
	// =============================================

	// =============================================
	// Methods
	// =============================================

}
