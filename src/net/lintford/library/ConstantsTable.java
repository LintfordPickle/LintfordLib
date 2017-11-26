package net.lintford.library;

import java.util.HashMap;
import java.util.Map;

// TODO: This needs to be read from a settings file (otherwise it isn't game agnostic)
// TODO: Remove non game-agnostic values
// TODO: Even consider moving this to the MasterConfig (which is read / saved)
public class ConstantsTable {

	public static final String APP_NAME = "APPLICATION_NAME";
	public static final String DEBUG_APP = "DEBUG_APP";

	public static void setAppConstants(String pAppName) {
		ConstantsTable.registerValue(APP_NAME, pAppName);
	}

	public static void setDebugConstants() {
		ConstantsTable.registerValue(DEBUG_APP, "true");

	}

	private static Map<String, String> constTab = new HashMap<>();

	public static String getStringValueDef(String pName, String pDef) {
		if (constTab.containsKey(pName)) {
			return constTab.get(pName);
		}

		return pDef;

	}

	public static float getFloatValueDef(String pName, float pDef) {
		if (constTab.containsKey(pName)) {
			try {
				float lValue = Float.valueOf(constTab.get(pName));
				return lValue;
			} catch (Exception e) {

			}
		}

		return pDef;

	}

	public static int getFloatValueDef(String pName, int pDef) {
		if (constTab.containsKey(pName)) {
			try {
				int lValue = Integer.valueOf(constTab.get(pName));
				return lValue;
			} catch (Exception e) {

			}
		}

		return pDef;

	}

	public static boolean getBooleanValueDef(String pName, boolean pDef) {
		if (constTab.containsKey(pName)) {
			try {
				boolean lValue = Boolean.valueOf(constTab.get(pName));
				return lValue;
			} catch (Exception e) {

			}
		}

		return pDef;

	}

	public static void registerValue(String pName, String pValue) {
		if (!constTab.containsKey(pName)) {
			constTab.put(pName, pValue);

		}

	}

	// --------------------------------------
	// Physics Constants to be moved
	// --------------------------------------

	public static final int BLOCK_SIZE_PIXELS = 16;

	public static final float EPSILON = 0.001f;
	public static final float SKIN_WIDTH = 0.025f;
	public static final float MOVEMENT_EPSILON = 0.003f;
	public static final float GRAVITY = 40f;
	public static final float FRICTION_X = 0.96f;
	public static final float FRICTION_Y = 0.96f;

	// --------------------------------------
	// Appplication / Window
	// --------------------------------------

}