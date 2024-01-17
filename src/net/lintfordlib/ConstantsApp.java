package net.lintfordlib;

import java.util.HashMap;
import java.util.Map;

import net.lintfordlib.core.debug.Debug;

public class ConstantsApp {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String TAG = "ConstantsApp";

	public static final String CONSTANT_APP_NAME_TAG = "APP_NAME";
	public static final String CONSTANT_IS_DEBUG_TAG = "DEBUG_APP";

	public static final String WORKSPACE_PROPERTY_NAME = "LintfordWorkspace";

	public static final int BASE_RESOLUTION_WIDTH = 800;
	public static final int BASE_RESOLUTION_HEIGHT = 500;

	private static Map<String, String> constTab = new HashMap<>();

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static String getStringValueDef(String name, String defaultValue) {
		if (constTab.containsKey(name)) {
			return constTab.get(name);
		}

		Debug.debugManager().logger().w(TAG, "String value was not found: " + name);

		return defaultValue;
	}

	public static float getFloatValueDef(String name, float defaultValue) {
		if (constTab.containsKey(name)) {
			try {
				float lValue = Float.valueOf(constTab.get(name));
				return lValue;
			} catch (Exception e) {
				Debug.debugManager().logger().w(TAG, "Float value was not found: " + name);
			}
		}

		return defaultValue;
	}

	public static int getIntValueDef(String name, int defaultValue) {
		if (constTab.containsKey(name)) {
			try {
				int lValue = Integer.valueOf(constTab.get(name));
				return lValue;
			} catch (Exception e) {
				Debug.debugManager().logger().w(TAG, "Integer value was not found: " + name);
			}
		}

		return defaultValue;
	}

	public static boolean getBooleanValueDef(String name, boolean defaultValue) {
		if (constTab.containsKey(name)) {
			try {
				boolean lValue = Boolean.valueOf(constTab.get(name));
				return lValue;
			} catch (Exception e) {
				Debug.debugManager().logger().w(TAG, "Boolean value was not found: " + name);
			}
		}

		return defaultValue;
	}

	public static void registerValue(String name, String value) {
		Debug.debugManager().logger().i(TAG, "Registered value: " + name + " : " + value);

		// Automagically replaces values which already exist
		constTab.put(name.toUpperCase(), value);
	}

}
