package net.lintfordlib.core.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.BitmapFontManager;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.fonts.FontUnit.WrapType;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.Vector3f;
import net.lintfordlib.core.messaging.Message;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.UiInputText;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;

public class DebugConsole extends Rectangle implements IBufferedTextInputCallback, IScrollBarArea, IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7219958843491782625L;

	private static final boolean AUTO_CAPTURE_ON_OPEN = false;

	private static final String PROMT_CHAR = "> ";
	private static final String CARET_CHAR = "|";
	private static final float FOCUS_TIMER = 250;

	public static final Vector3f DEFAULT_MESSAGE_RGB = new Vector3f(0.95f, 0.96f, 0.94f);
	public static final Vector3f VERBOSE_MESSAGE_RGB = new Vector3f(0.44f, 0.44f, 0.40f);
	public static final Vector3f INFO_MESSAGE_RGB = new Vector3f(0.94f, 0.94f, 0.90f);
	public static final Vector3f WARN_MESSAGE_RGB = new Vector3f(0.93f, 0.85f, 0.13f);
	public static final Vector3f ERR_MESSAGE_RGB = new Vector3f(0.93f, 0f, 0f);
	public static final Vector3f USER_MESSAGE_RGB = new Vector3f(0.47f, 0.77f, 0.9f);
	public static final Vector3f SYS_MESSAGE_RGB = new Vector3f(0.83f, 0.27f, 0f);

	final static String CONSTANTS_TABLE_COMMAND_PATTERN = "(?!;)(.+?)=.(.*)";

	public static Vector3f getMessageRGB(final int pMessageType) {
		switch (pMessageType) {
		case Debug.LOG_LEVEL_SYSTEM:
			return SYS_MESSAGE_RGB;
		case Debug.LOG_LEVEL_USER:
			return USER_MESSAGE_RGB;
		case Debug.LOG_LEVEL_ERROR:
			return ERR_MESSAGE_RGB;
		case Debug.LOG_LEVEL_WARNING:
			return WARN_MESSAGE_RGB;
		case Debug.LOG_LEVEL_INFO:
			return INFO_MESSAGE_RGB;
		case Debug.LOG_LEVEL_VERBOSE:
			return VERBOSE_MESSAGE_RGB;

		default:
			return DEFAULT_MESSAGE_RGB;

		}

	}

	public enum CONSOLE_STATE {
		closed, open, minimal,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;
	private transient CONSOLE_STATE mConsoleState = CONSOLE_STATE.closed;
	private transient float mFocusTimer;
	private transient StringBuilder mInputText;
	private transient List<ConsoleCommand> mConsoleCommands;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient boolean mShowCaret;
	private transient float mCaretTimer;
	private transient SpriteBatch mSpriteBatch;
	private transient FontUnit mConsoleFont;
	public transient float mFPSDraw;
	public transient float mFPSUpdate;
	private transient boolean mHasFocus;
	private transient int mLowerBound;
	private transient int mUpperBound;
	private transient int mConsoleLineHeight;
	private float mInputTimer;
	private transient boolean mAutoScroll;
	private boolean mResourcesLoaded;
	private UiInputText mTAGFilterText;
	private int mTAGFilterLastSize;
	private UiInputText mMessageFilterText;
	private int mMessageFilterLastSize;
	private final Color mConsoleBackgroundColor = new Color(0f, 0f, 0f, 0.9f);
	private final Color mConsoleTextColor = new Color();
	protected boolean mFilterProcessed;
	protected List<Message> mProcessedMessages;
	protected List<Message> mUpdateMessageList;
	protected boolean mDirty;
	protected PrintStream mErrPrintStream;
	protected SpriteSheetDefinition mCoreSpritesheet;
	private transient Rectangle mAutoScrollIconRectangle;
	private float mOpenHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PrintStream err() {
		return mErrPrintStream;
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public boolean isOpen() {
		return mConsoleState == CONSOLE_STATE.open;
	}

	public float openHeight() {
		return mOpenHeight;
	}

	public void setConsoleState(CONSOLE_STATE newState) {
		if (newState == null)
			return;
		mConsoleState = newState;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	DebugConsole(final Debug debugManager) {
		mDebugManager = debugManager;

		if (debugManager.debugManagerEnabled()) {
			mErrPrintStream = new PrintStream(System.out) {
				public void print(String stringToPrint) {
					if (!stringToPrint.isEmpty()) {
						super.print(stringToPrint);
						updateConsole(stringToPrint);
					}
				};
			};

			System.setOut(mErrPrintStream);
			System.setErr(mErrPrintStream);

			mInputText = new StringBuilder();

			mConsoleState = CONSOLE_STATE.closed;

			mSpriteBatch = new SpriteBatch();

			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);

			mConsoleCommands = new ArrayList<>();
			mAutoScroll = true;

			mTAGFilterText = new UiInputText(null);
			mTAGFilterText.emptyString("Filter");
			mTAGFilterText.mouseClickBreaksInputTextFocus(true);

			mMessageFilterText = new UiInputText(null);
			mMessageFilterText.emptyString("Filter");
			mMessageFilterText.mouseClickBreaksInputTextFocus(true);

			mProcessedMessages = new ArrayList<>();
			mUpdateMessageList = new ArrayList<>();

			mAutoScrollIconRectangle = new Rectangle();
		}

		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mConsoleFont = resourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME);
		mSpriteBatch.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugConsole unloading GL content");

		mConsoleFont = null;
		mSpriteBatch.unloadResources();

		mResourcesLoaded = false;
	}

	public void handleInput(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (mConsoleState == CONSOLE_STATE.open) {
			if (mScrollBar.handleInput(core, null)) {
				if (mScrollBar.isAtBottomPosition()) {
					mAutoScroll = true;
				} else {
					mAutoScroll = false;
				}

				return;
			}

			if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
				if (mTAGFilterText.handleInput(core))
					return;
				if (mMessageFilterText.handleInput(core))
					return;

				if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DELETE, this)) {
					mScrollBar.AbsCurrentYPos(0);
					mAutoScroll = true;

					Debug.debugManager().logger().clearLogLines();
				}

				if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollBar.RelCurrentYPos(-mConsoleLineHeight);
					mAutoScroll = false;

					if (mScrollBar.currentYPos() < mScrollBar.getScrollYBottomPosition())
						mScrollBar.AbsCurrentYPos(mScrollBar.getScrollYBottomPosition() - mConsoleLineHeight);
				}

				if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_PAGE_DOWN, this)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollBar.RelCurrentYPos(-mConsoleLineHeight * 10);
					mAutoScroll = false;

					if (mScrollBar.currentYPos() < mScrollBar.getScrollYBottomPosition())
						mScrollBar.AbsCurrentYPos(mScrollBar.getScrollYBottomPosition() - mConsoleLineHeight);
				}

				if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollBar.RelCurrentYPos(mConsoleLineHeight);
					mAutoScroll = false;

					if (mScrollBar.currentYPos() > 0)
						mScrollBar.AbsCurrentYPos(0);
				}

				if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_PAGE_UP, this)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollBar.RelCurrentYPos(mConsoleLineHeight * 10);
					mAutoScroll = false;

					if (mScrollBar.currentYPos() > 0)
						mScrollBar.AbsCurrentYPos(0);
				}

				// capture the mouse wheel too
				final float lScrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(lScrollAccelerationAmt);
				if (mScrollBar.scrollAcceleration() != 0) {
					if (mScrollBar.isAtBottomPosition()) {
						mAutoScroll = true;
					} else {
						mAutoScroll = false;
					}
				}
			}

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE, this)) {
				if (mConsoleState == CONSOLE_STATE.open) {
					mConsoleState = CONSOLE_STATE.closed;

					mInputText.delete(0, mInputText.length());
					mHasFocus = false;
					core.input().keyboard().stopBufferedTextCapture();
				}
			}

			if (mTAGFilterText.intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseLeftClick(mTAGFilterText.hashCode())) {

			} else if (mMessageFilterText.intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseLeftClick(mMessageFilterText.hashCode())) {

			} else if (mFocusTimer > FOCUS_TIMER && core.input().mouse().mouseWindowCoords().y < openHeight() && core.input().mouse().tryAcquireMouseLeftClick(hashCode()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
				mHasFocus = !mHasFocus;
				resetCoolDownTimer();
				core.input().keyboard().stopBufferedTextCapture();
				mFocusTimer = 0;

				if (mHasFocus)
					core.input().keyboard().startBufferedTextCapture(this);

			}

		} else {
			if (mHasFocus) {
				core.input().keyboard().stopBufferedTextCapture();
				mHasFocus = false;
			}
		}

		if (mHasFocus && (core.input().mouse().isMouseLeftButtonDownTimed(this) || core.input().mouse().isMouseRightButtonDownTimed(this))) {
			core.input().keyboard().stopBufferedTextCapture();

			mHasFocus = false;
			mShowCaret = false;
		}

		// listen for opening and closing
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F1, this)) {
			switch (mConsoleState) {
			case closed:
				mConsoleState = CONSOLE_STATE.minimal;

				mInputText.delete(0, mInputText.length());

				if (AUTO_CAPTURE_ON_OPEN) {
					mHasFocus = true;
					core.input().keyboard().startBufferedTextCapture(this);
				}

				break;
			case minimal:
				mConsoleState = CONSOLE_STATE.open;

				mHasFocus = false;

				break;
			default:
				mConsoleState = CONSOLE_STATE.closed;
				mHasFocus = false;
			}
		}
	}

	public void update(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mOpenHeight = core.HUD().getHeight() * 0.3f;

		if (mConsoleState == CONSOLE_STATE.minimal)
			mAutoScroll = true;

		if (mInputTimer >= 0)
			mInputTimer -= core.appTime().elapsedTimeMilli();

		if (!mResourcesLoaded || mConsoleState == CONSOLE_STATE.closed)
			return;

		final float lDeltaTime = (float) core.appTime().elapsedTimeMilli() / 1000f;

		mFocusTimer += lDeltaTime * 1000f;
		mCaretTimer += lDeltaTime * 1000f;

		if (mCaretTimer > 250) {
			mCaretTimer = 0;
			mShowCaret = !mShowCaret;
		}

		mTAGFilterText.update(core);
		mMessageFilterText.update(core);

		doFilterText();

		// Update the window content
		mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 3);
		final var MAX_NUM_LINES = (int) ((openHeight() - mConsoleLineHeight * 2) / mConsoleLineHeight) - 2;

		final var lNumberLinesInConsole = mFilterProcessed ? mProcessedMessages.size() : Debug.debugManager().logger().logLines().size();
		fullContentArea().setCenter(mX, mY, mW - mScrollBar.width(), lNumberLinesInConsole * 25);

		final var lHudBounds = core.HUD().boundingRectangle();
		mX = lHudBounds.left();
		mY = lHudBounds.top();
		mW = lHudBounds.width();
		mH = openHeight();

		mLowerBound = (int) -((mScrollBar.currentYPos()) / mConsoleLineHeight) + 1;
		// Lower bound should not be lower than the last item (occurs when filtering texture and number of lines decreases).
		if (mFilterProcessed && mLowerBound > mProcessedMessages.size()) {
			mLowerBound = mProcessedMessages.size() - MAX_NUM_LINES;
			if (mLowerBound < 0)
				mLowerBound = 0;

			mScrollBar.AbsCurrentYPos(mScrollBar.getScrollYBottomPosition());
		}

		mUpperBound = mLowerBound + MAX_NUM_LINES;

		mContentRectangle.height((lNumberLinesInConsole + 2) * mConsoleLineHeight);

		// mAutoScroll = true;
		if (mAutoScroll) {
			int lNumLines = mFilterProcessed ? mProcessedMessages.size() : Debug.debugManager().logger().logLines().size();
			mUpperBound = lNumLines;
			mLowerBound = mUpperBound - MAX_NUM_LINES;

			mScrollBar.AbsCurrentYPos(mScrollBar.getScrollYBottomPosition());
		}

		mScrollBar.update(core);

		var lConsoleVisible = mConsoleState == CONSOLE_STATE.open;

		final int lIconSize = 32;
		mAutoScrollIconRectangle.set(right() - lIconSize - 5.f + (lConsoleVisible && mScrollBar.isActive() ? -30.f : 0.f), bottom() - lIconSize - 5.f, lIconSize, lIconSize);
	}

	public void draw(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mResourcesLoaded || mConsoleState == CONSOLE_STATE.closed)
			return;

		final var lDisplayConfig = core.config().display();

		final float Z_DEPTH = ZLayers.LAYER_DEBUG;

		final float POSITION_OFFSET_TAG = 170;
		final float POSITION_OFFSET_MESSAGE = 400;

		final float PADDING_LEFT = 5;
		final float lInputTextXOffset = 14;

		final float lTextHeight = 20f;

		mTAGFilterText.set(mX + POSITION_OFFSET_TAG, mY + 4, 200, 25);
		mMessageFilterText.set(mX + POSITION_OFFSET_MESSAGE, mY + 4, 200, 25);

		final var lScreenBB = core.HUD().boundingRectangle();

		if (mConsoleState == CONSOLE_STATE.open) {
			mConsoleBackgroundColor.setRGBA(0.f, 0.f, 0.f, .8f);

			mScrollBar.draw(core, mSpriteBatch, mCoreSpritesheet, Z_DEPTH + 0.1f, 1.f);

			mSpriteBatch.begin(core.HUD());

			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, lScreenBB.left(), lScreenBB.top(), lScreenBB.width(), lScreenBB.height(), Z_DEPTH, mConsoleBackgroundColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, mX, mY, mW, mH, Z_DEPTH, ColorConstants.MenuPanelPrimaryColor);

			final var lBackgroundInputPanelColor = ColorConstants.getBlackWithAlpha(0.35f);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY + 50 - lTextHeight, mW, 2, Z_DEPTH, lBackgroundInputPanelColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX + 157, mY + 50 - lTextHeight, 2, mH - 50, Z_DEPTH, lBackgroundInputPanelColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY + mH - lTextHeight, mW, lTextHeight, Z_DEPTH, lBackgroundInputPanelColor);
			mSpriteBatch.end();

			mTAGFilterText.draw(core, mSpriteBatch, mCoreSpritesheet, mConsoleFont, -0.001f);
			mMessageFilterText.draw(core, mSpriteBatch, mCoreSpritesheet, mConsoleFont, Z_DEPTH + 0.01f);

			// the input line from the user will always be visible at the bottom of the console.
			if (mInputText != null) {
				final float INPUT_Y_OFFSET = 0;

				mConsoleFont.begin(core.HUD());
				mConsoleFont.drawText(PROMT_CHAR, -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT, mY + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, ColorConstants.WHITE, 1f);
				mConsoleFont.drawText(mInputText.toString(), -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT + lInputTextXOffset, mY + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, ColorConstants.WHITE, 1f);
				if (mShowCaret && mHasFocus)
					mConsoleFont.drawText(CARET_CHAR, -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT + lInputTextXOffset + mConsoleFont.getStringWidth(mInputText.toString()), mY + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, ColorConstants.WHITE, 1f);

				mConsoleFont.end();
			}
		}

		var lAutoScrollIconColor = ColorConstants.WHITE;
		if (mAutoScroll == false)
			lAutoScrollIconColor = ColorConstants.getWhiteWithAlpha(.1f);

		mSpriteBatch.begin(core.HUD());
		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_AUTOSCROLL, mAutoScrollIconRectangle, Z_DEPTH, lAutoScrollIconColor);
		mSpriteBatch.end();

		if (mFilterProcessed == false) {
			synchronized (Debug.debugManager().logger()) {
				drawMessages(core, Debug.debugManager().logger().logLines());
			}
		} else {
			drawMessages(core, mProcessedMessages);
		}
	}

	private void drawMessages(LintfordCore core, List<Message> messages) {
		float lTextPosition = mConsoleState == CONSOLE_STATE.minimal ? 10 : -20;

		final float POSITION_OFFSET_TIME = 5;
		final float POSITION_OFFSET_TAG = 170;
		final float POSITION_OFFSET_MESSAGE = 400;

		final var lHudBb = core.HUD().boundingRectangle();

		final int lMessageCount = messages.size();

		mConsoleFont.begin(core.HUD());
		if (messages != null && lMessageCount > 0) {
			for (int i = mLowerBound; i < mUpperBound; i++) {
				if (i >= 0 && i < lMessageCount) {
					final var lMessage = messages.get(i);
					if (lMessage == null)
						continue;

					lTextPosition -= mConsoleLineHeight;

					final var lColorRgb = getMessageRGB(lMessage.type());
					final float lR = lColorRgb.x;
					final float lG = lColorRgb.y;
					final float lB = lColorRgb.z;
					mConsoleTextColor.setRGBA(lR, lG, lB, 1.0f);

					// Draw Timestamp
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					mConsoleFont.drawText(lMessage.timestamp(), mX + POSITION_OFFSET_TIME, lHudBb.top() - lTextPosition, ZLayers.LAYER_DEBUG + 0.1f, mConsoleTextColor, 1f, 18);

					// Draw TAG
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					mConsoleFont.drawText(lMessage.tag(), mX + POSITION_OFFSET_TAG, lHudBb.top() - lTextPosition, ZLayers.LAYER_DEBUG + 0.1f, mConsoleTextColor, 1f, 18);

					// Draw MESSAGE
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					final float lCharWidth = mConsoleFont.getStringWidth("e");
					final float lHorizontalSpace = core.HUD().getWidth() - POSITION_OFFSET_MESSAGE;
					mConsoleFont.drawText(lMessage.message(), mX + POSITION_OFFSET_MESSAGE, lHudBb.top() - lTextPosition, ZLayers.LAYER_DEBUG + 0.1f, mConsoleTextColor, 1f, lHorizontalSpace / lCharWidth - 3);
				}
			}
		}
		mConsoleFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void doFilterText() {
		if (mTAGFilterLastSize != mTAGFilterText.inputString().length()) {
			mDirty = true;
			mTAGFilterLastSize = mTAGFilterText.inputString().length();
		}

		if (mMessageFilterLastSize != mMessageFilterText.inputString().length()) {
			mDirty = true;
			mMessageFilterLastSize = mMessageFilterText.inputString().length();
		}

		if (mDirty) {
			mAutoScroll = true;
			mFilterProcessed = false;
			mScrollBar.resetBarTop();

			mProcessedMessages.clear();
			mFilterProcessed = !mTAGFilterText.isEmpty() || !mMessageFilterText.isEmpty();

			final var lLogLines = Debug.debugManager().logger().logLines();
			final var lLogLineCount = lLogLines.size();
			for (int i = 0; i < lLogLineCount; i++) {
				mProcessedMessages.add(lLogLines.get(i));
			}

			if (mFilterProcessed) {
				if (!mTAGFilterText.isEmpty()) {
					mUpdateMessageList.clear();

					final int lFilteredLogCount = mProcessedMessages.size();
					for (int i = 0; i < lFilteredLogCount; i++) {
						mUpdateMessageList.add(mProcessedMessages.get(i));
					}

					mProcessedMessages.clear();
					final String lFilterText = mTAGFilterText.inputString().toString().toUpperCase();
					final int lUpdateMessageCount = mUpdateMessageList.size();
					for (int i = 0; i < lUpdateMessageCount; i++) {
						final var lStringA = mUpdateMessageList.get(i).tag().toString().toUpperCase();
						final var lStringB = lFilterText;
						if (lStringA.contains(lStringB)) {
							mProcessedMessages.add(mUpdateMessageList.get(i));
						}
					}
				}

				if (!mMessageFilterText.isEmpty()) {
					mUpdateMessageList.clear();

					// First copy the whole LogMessages to the linkedlist, unfettered
					final int lFilteredLogCount = mProcessedMessages.size();
					for (int i = 0; i < lFilteredLogCount; i++) {
						mUpdateMessageList.add(mProcessedMessages.get(i));
					}

					mProcessedMessages.clear();
					final String lFilterText = mMessageFilterText.inputString().toString().toUpperCase();
					final int lUpdateMessageCount = mUpdateMessageList.size();
					for (int i = 0; i < lUpdateMessageCount; i++) {
						String lStringA = mUpdateMessageList.get(i).message().toString().toUpperCase();
						String lStringB = lFilterText;
						if (lStringA.contains(lStringB)) {
							mProcessedMessages.add(mUpdateMessageList.get(i));
						}
					}
				}
			}

			mDirty = false;
		}
	}

	/**
	 * Used to pass the console output stream to the debugconsole (character-by-character).
	 * 
	 * @param newChar
	 */
	protected void updateConsole(final char newChar) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (newChar == '\n' || newChar == '\r') {
			Debug.debugManager().logger().u("User", mInputText.toString());

			if (mInputText.length() > 0) {
				mInputText.delete(0, mInputText.length());
			}
		} else {
			mInputText.append(newChar);
		}
	}

	protected void updateConsole(String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v("Console", message);
	}

	// --------------------------------------
	// Implements
	// --------------------------------------

	@Override
	public boolean onEscapePressed() {
		mHasFocus = false;
		mConsoleState = CONSOLE_STATE.closed;

		if (mInputText.length() > 0) {
			mInputText.delete(0, mInputText.length());
		}

		return true;
	}

	@Override
	public boolean onEnterPressed() {
		if (mInputText.length() == 0) {
			mHasFocus = false;
			return true; // finish keyboard capture
		}

		final String lInputString = mInputText.toString();

		if (lInputString != null) {
			if (Pattern.matches(CONSTANTS_TABLE_COMMAND_PATTERN, lInputString)) {
				String[] lResultArray = lInputString.split("([\\=])");

				if (lResultArray != null && lResultArray.length == 2) {
					ConstantsApp.registerValue(lResultArray[0], lResultArray[1]);

					Debug.debugManager().logger().u("Settings Changed", lInputString);

					mAutoScroll = true;
					mDirty = true;

					mInputText.delete(0, mInputText.length());

					return getEnterFinishesInput();
				}
			} else {
				boolean lResult = false;
				final int CONSOLE_COMMANDS = mConsoleCommands.size();
				for (int i = 0; i < CONSOLE_COMMANDS; i++) {
					if (lInputString.equals(mConsoleCommands.get(i).Command)) {
						lResult = mConsoleCommands.get(i).doCommand();
						Debug.debugManager().logger().u("", "  completed " + (lResult ? "successfully" : "with errors"));

						mInputText.delete(0, mInputText.length());

						mAutoScroll = true;
						mDirty = true;

						return getEnterFinishesInput();
					}
				}
			}
		}

		Debug.debugManager().logger().u("User", lInputString);

		mInputText.delete(0, mInputText.length());

		mAutoScroll = true;
		mDirty = true;

		return getEnterFinishesInput();
	}

	@Override
	public boolean getEnterFinishesInput() {
		return false;
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;
	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputText;
	}

	@Override
	public void onKeyPressed(int codePoint) {
		if (codePoint == GLFW.GLFW_KEY_BACKSPACE) {
			if (mInputText.length() > 0) {
				mInputText.delete(mInputText.length() - 1, mInputText.length());
			}
		} else {
			mInputText.append((char) codePoint);
		}

	}

	public void addConsoleCommand(ConsoleCommand consoleCommand) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (mConsoleCommands != null && !mConsoleCommands.contains(consoleCommand)) {
			mConsoleCommands.add(consoleCommand);
		}
	}

	public void removeConsoleCommand(ConsoleCommand consoleCommand) {
		if (mConsoleCommands != null && mConsoleCommands.contains(consoleCommand)) {
			mConsoleCommands.remove(consoleCommand);
		}
	}

	public void listConsoleCommands() {
		final int lConsoleCommandCount = mConsoleCommands.size();
		for (int i = 0; i < lConsoleCommandCount; i++) {
			Debug.debugManager().logger().i(mConsoleCommands.get(i).Command, mConsoleCommands.get(i).Description);
		}
	}

	@Override
	public void onCaptureStarted() {

	}

	@Override
	public void onCaptureStopped() {
		mHasFocus = false;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		resetCoolDownTimer(IInputProcessor.INPUT_COOLDOWN_TIME);
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return false;
	}
}