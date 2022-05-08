package net.lintford.library;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.debug.Debug;

public class ConstantsApp {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String TAG = "ConstantsApp";

	public static final String APP_NAME = "D284";
	public static final String DEBUG_APP = "_D284 DEBUG";

	public static final int BASE_RESOLUTION_WIDTH = 800;
	public static final int BASE_RESOLUTION_HEIGHT = 500;

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static void setAppConstants(String pAppName) {
		ConstantsApp.registerValue(APP_NAME, pAppName);
	}

	public static void setDebugConstants() {
		ConstantsApp.registerValue(DEBUG_APP, "true");
	}

	private static Map<String, String> constTab = new HashMap<>();

	public static String getStringValueDef(String pName, String pDef) {
		if (constTab.containsKey(pName)) {
			return constTab.get(pName);
		}

		Debug.debugManager().logger().w(TAG, "String value was not found: " + pName);

		return pDef;
	}

	public static float getFloatValueDef(String pName, float pDef) {
		if (constTab.containsKey(pName)) {
			try {
				float lValue = Float.valueOf(constTab.get(pName));
				return lValue;
			} catch (Exception e) {
				Debug.debugManager().logger().w(TAG, "Float value was not found: " + pName);
			}
		}

		return pDef;
	}

	public static int getIntValueDef(String pName, int pDef) {
		if (constTab.containsKey(pName)) {
			try {
				int lValue = Integer.valueOf(constTab.get(pName));
				return lValue;
			} catch (Exception e) {
				Debug.debugManager().logger().w(TAG, "Integer value was not found: " + pName);
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
				Debug.debugManager().logger().w(TAG, "Boolean value was not found: " + pName);
			}
		}

		return pDef;
	}

	public static void registerValue(String pName, String pValue) {
		Debug.debugManager().logger().i(TAG, "Registered value: " + pName + " : " + pValue);

		// Automagically replaces values which already exist
		constTab.put(pName.toUpperCase(), pValue);
	}
}
