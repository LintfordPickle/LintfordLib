package net.lintfordlib;

public class ConstantsDisplay {

	public static final int REFERENCE_GAME_RESOLUTION_W = 960;
	public static final int REFERENCE_GAME_RESOLUTION_H = 540;

	/**
	 * All UiComponents define their widths in relation to this base width. You should calculate the scale factor based on the canvas size, using CANVAS_SIZE / BASE_UI_COMPONENT_RESOLUTION_W
	 */
	public static final int REFERENCE_UI_RESOLUTION_W = 1920;

	/**
	 * All UiComponents define their heights in relation to this base height. You should calculate the scale factor based on the canvas size, using CANVAS_SIZE / BASE_UI_COMPONENT_RESOLUTION_H
	 */
	public static final int REFERENCE_UI_RESOLUTION_H = 1080;

	public static final int MIN_UI_HUD_WIDTH = 800;
	public static final int MIN_UI_HUD_HEIGHT = 600;

	public static final int MAX_UI_HUD_WIDTH = 1920;
	public static final int MAX_UI_HUD_HEIGHT = 1080;

}
