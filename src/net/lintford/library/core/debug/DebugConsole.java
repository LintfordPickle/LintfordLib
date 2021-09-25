package net.lintford.library.core.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.fonts.FontUnit.WrapType;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IBufferedTextInputCallback;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.core.messaging.Message;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.renderers.windows.components.UIInputText;

public class DebugConsole extends Rectangle implements IBufferedTextInputCallback, IScrollBarArea, IProcessMouseInput {

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

	// This is the extent of all the lines of the debug console
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;

	private transient boolean mShowCaret;
	private transient float mCaretTimer;
	private transient SpriteBatch mSpriteBatch;

	private transient FontUnit mConsoleFont;

	public transient float mFPSDraw;
	public transient float mFPSUpdate;
	private transient boolean mHasFocus;

	// Because we always need to display a range of text (e.g. lines 23-43), we
	// track that range in the following variables
	private transient int mLowerBound;
	private transient int mUpperBound;
	private transient int mConsoleLineHeight;

	private float mMouseTimer;
	private transient boolean mAutoScroll;
	private boolean mIsLoaded;

	private UIInputText mTAGFilterText;
	private int mTAGFilterLastSize;
	private UIInputText mMessageFilterText;
	private int mMessageFilterLastSize;

	private final Color mConsoleBackgroundColor = new Color(0f, 0f, 0f, 0.9f);
	private final Color mConsoleTextColor = new Color();

	protected boolean mProcessed; // is filter applied?
	protected List<Message> mProcessedMessages;
	protected List<Message> mUpdateMessageList;
	protected boolean mDirty;

	protected PrintStream mErrPrintStream;
	private SpriteSheetDefinition mCoreSpritesheet;

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
		return (float) Math.pow(14, 2);

	}

	public void setConsoleState(CONSOLE_STATE newState) {
		if (newState == null)
			return;
		mConsoleState = newState;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	DebugConsole(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (pDebugManager.debugManagerEnabled()) {

			// Intercept the system out and copy any strings into our debug console so we can see it in the game.
			mErrPrintStream = new PrintStream(System.out) {
				public void print(String s) {
					if (!s.isEmpty()) {
						super.print(s);
						updateConsole(s);
					}
				};

			};

			// TODO: There is something not quite right here. LWJGL error out is not being redirected/captured by our console logger.
			System.setOut(mErrPrintStream);
			System.setErr(mErrPrintStream);

			mInputText = new StringBuilder();

			mConsoleState = CONSOLE_STATE.closed;

			mSpriteBatch = new SpriteBatch();

			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);

			mConsoleCommands = new ArrayList<>();
			mAutoScroll = true;

			mTAGFilterText = new UIInputText(null);
			mTAGFilterText.emptyString("Filter");
			mTAGFilterText.mouseClickBreaksInputTextFocus(true);

			mMessageFilterText = new UIInputText(null);
			mMessageFilterText.emptyString("Filter");
			mMessageFilterText.mouseClickBreaksInputTextFocus(true);

			mProcessedMessages = new ArrayList<>();
			mUpdateMessageList = new ArrayList<>();

		}

		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mConsoleFont = pResourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME);
		mSpriteBatch.loadGLContent(pResourceManager);

		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugConsole unloading GL content");

		mConsoleFont = null;
		mSpriteBatch.unloadGLContent();

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (mConsoleState == CONSOLE_STATE.open) {
			if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
				if (mTAGFilterText.handleInput(pCore))
					return;
				if (mMessageFilterText.handleInput(pCore))
					return;

				if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DELETE)) {
					mScrollYPosition = 0;
					mAutoScroll = true;

					Debug.debugManager().logger().clearLogLines();

				}

				if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollYPosition -= mConsoleLineHeight;
					mAutoScroll = false;

					if (mScrollYPosition < mScrollBar.getScrollYBottomPosition())
						mScrollYPosition = mScrollBar.getScrollYBottomPosition() - mConsoleLineHeight;
				}

				if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_PAGE_DOWN)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollYPosition -= mConsoleLineHeight * 10;
					mAutoScroll = false;

					if (mScrollYPosition < mScrollBar.getScrollYBottomPosition())
						mScrollYPosition = mScrollBar.getScrollYBottomPosition() - mConsoleLineHeight;

				}

				if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollYPosition += mConsoleLineHeight;
					mAutoScroll = false;

					if (mScrollYPosition > 0)
						mScrollYPosition = 0;

				}

				if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_PAGE_UP)) {
					mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 1);
					mScrollYPosition += mConsoleLineHeight * 10;
					mAutoScroll = false;

					if (mScrollYPosition > 0)
						mScrollYPosition = 0;

				}

				// capture the mouse wheel too
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;
				if (mZScrollAcceleration != 0) {
					mAutoScroll = false;
				}

			}

			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
				if (mConsoleState == CONSOLE_STATE.open) {
					mConsoleState = CONSOLE_STATE.closed;

					mInputText.delete(0, mInputText.length());
					mHasFocus = false;
					pCore.input().keyboard().stopBufferedTextCapture();

				}

			}

			if (mScrollBar.handleInput(pCore)) {
				if (mScrollBar.isAtBottomPosition()) {
					mAutoScroll = true;

				} else {
					mAutoScroll = false;

				}

				return;

			}

			else if (mTAGFilterText.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseLeftClick(mTAGFilterText.hashCode())) {

			}

			else if (mMessageFilterText.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseLeftClick(mMessageFilterText.hashCode())) {

			}

			else if (mFocusTimer > FOCUS_TIMER && pCore.input().mouse().mouseWindowCoords().y < openHeight() && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
				mHasFocus = !mHasFocus;
				resetCoolDownTimer();
				pCore.input().keyboard().stopBufferedTextCapture();
				mFocusTimer = 0;

				if (mHasFocus) {
					pCore.input().keyboard().startBufferedTextCapture(this);
				}

			}

		} else {
			if (mHasFocus) {
				pCore.input().keyboard().stopBufferedTextCapture();
				mHasFocus = false;
			}
		}

		if (mHasFocus && (pCore.input().mouse().isMouseLeftButtonDownTimed(this) || pCore.input().mouse().isMouseRightButtonDownTimed(this))) {
			pCore.input().keyboard().stopBufferedTextCapture();

			mHasFocus = false;
			mShowCaret = false;

		}

		// listen for opening and closing
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F1)) {
			switch (mConsoleState) {
			case closed:
				mConsoleState = CONSOLE_STATE.minimal;

				mInputText.delete(0, mInputText.length());

				if (AUTO_CAPTURE_ON_OPEN) {
					mHasFocus = true;
					pCore.input().keyboard().startBufferedTextCapture(this);

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

	public void update(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (mConsoleState == CONSOLE_STATE.minimal) {
			mAutoScroll = true;

		}

		if (!mIsLoaded || mConsoleState == CONSOLE_STATE.closed)
			return;

		final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;

		if (mMouseTimer >= 0) {
			mMouseTimer -= pCore.appTime().elapsedTimeMilli();

		}

		// Update timers
		mFocusTimer += lDeltaTime * 1000f;
		mCaretTimer += lDeltaTime * 1000f;

		if (mCaretTimer > 250) {
			mCaretTimer = 0;
			mShowCaret = !mShowCaret;
		}

		// Update text filters
		mTAGFilterText.update(pCore);
		mMessageFilterText.update(pCore);

		doFilterText();

		// Update the window content
		mConsoleLineHeight = (int) (mConsoleFont.fontHeight() + 3);
		final var MAX_NUM_LINES = (int) ((openHeight() - mConsoleLineHeight * 2) / mConsoleLineHeight) - 2;

		final var lNumberLinesInConsole = mProcessed ? mProcessedMessages.size() : Debug.debugManager().logger().logLines().size();
		fullContentArea().setCenter(x, y, w - mScrollBar.w(), lNumberLinesInConsole * 25);

		final var lDisplay = pCore.config().display();
		// Update the bounds of the window view
		x = -lDisplay.windowWidth() * 0.5f;
		y = -lDisplay.windowHeight() * 0.5f;
		w = lDisplay.windowWidth();
		h = openHeight();

		mLowerBound = (int) -((mScrollYPosition) / mConsoleLineHeight) + 1;
		// Lower bound should not be lower than the last item (occurs when filtering texture and number of lines decreases).
		if (mProcessed && mLowerBound > mProcessedMessages.size()) {
			mLowerBound = mProcessedMessages.size() - MAX_NUM_LINES;
			if (mLowerBound < 0)
				mLowerBound = 0;

			mScrollYPosition = mScrollBar.getScrollYBottomPosition();

		}
		mUpperBound = mLowerBound + MAX_NUM_LINES;

		mContentRectangle.h((lNumberLinesInConsole + 2) * mConsoleLineHeight);

		var lScrollSpeedFactor = mScrollYPosition;

		mZScrollVelocity += mZScrollAcceleration;
		lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
		mZScrollVelocity *= 0.85f;
		mZScrollAcceleration = 0.0f;

		// Constrain
		mScrollYPosition = lScrollSpeedFactor;
		if (mScrollYPosition > 0)
			mScrollYPosition = 0;
		if (mScrollYPosition < -(mContentRectangle.h() - this.h)) {
			mScrollYPosition = -(mContentRectangle.h() - this.h);
			mAutoScroll = true;
		}

		// mAutoScroll = true;
		if (mAutoScroll) {
			int lNumLines = mProcessed ? mProcessedMessages.size() : Debug.debugManager().logger().logLines().size();
			mUpperBound = lNumLines;
			mLowerBound = mUpperBound - MAX_NUM_LINES;

			mScrollYPosition = mScrollBar.getScrollYBottomPosition();

		}

		mScrollBar.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mIsLoaded || mConsoleState == CONSOLE_STATE.closed)
			return;

		final var lDisplayConfig = pCore.config().display();

		final float Z_DEPTH = ZLayers.LAYER_DEBUG;

		final float POSITION_OFFSET_TIME = 5;
		final float POSITION_OFFSET_TAG = 170;
		final float POSITION_OFFSET_MESSAGE = 400;

		final float PADDING_LEFT = 5;
		final float lInputTextXOffset = 14;
		float lTextPosition = mConsoleState == CONSOLE_STATE.minimal ? 10 : -20;
		final float lTextHeight = 20f;

		mTAGFilterText.set(x + POSITION_OFFSET_TAG, y + 4, 200, 25);
		mMessageFilterText.set(x + POSITION_OFFSET_MESSAGE, y + 4, 200, 25);

		final var lScreenBB = pCore.HUD().boundingRectangle();

		mSpriteBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());

		if (mConsoleState == CONSOLE_STATE.open) {
			mConsoleBackgroundColor.setRGBA(0.f, 0.f, 0.f, .8f);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, lScreenBB.left(), lScreenBB.top(), lScreenBB.width(), lScreenBB.height(), Z_DEPTH, mConsoleBackgroundColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, Z_DEPTH, ColorConstants.MenuPanelPrimaryColor);

			final var lBackgroundInputPanelColor = ColorConstants.getBlackWithAlpha(0.35f);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y + 50 - lTextHeight, w, 2, Z_DEPTH, lBackgroundInputPanelColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x + 157, y + 50 - lTextHeight, 2, h - 50, Z_DEPTH, lBackgroundInputPanelColor);
			mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y + h - lTextHeight, w, lTextHeight, Z_DEPTH, lBackgroundInputPanelColor);

			mScrollBar.draw(pCore, mSpriteBatch, mCoreSpritesheet, Z_DEPTH + 0.1f);

			mTAGFilterText.draw(pCore, mSpriteBatch, mCoreSpritesheet, mConsoleFont, -0.001f);
			mMessageFilterText.draw(pCore, mSpriteBatch, mCoreSpritesheet, mConsoleFont, Z_DEPTH + 0.01f);

			// the input line from the user will always be visible at the bottom of the console.
			if (mInputText != null) {
				final float INPUT_Y_OFFSET = 0;
				mConsoleFont.drawText(PROMT_CHAR, -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, ColorConstants.WHITE, 1f);
				mConsoleFont.drawText(mInputText.toString(), -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT + lInputTextXOffset, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, ColorConstants.WHITE, 1f);
				if (mShowCaret && mHasFocus)
					mConsoleFont.drawText(CARET_CHAR, -lDisplayConfig.windowWidth() * 0.5f + PADDING_LEFT + lInputTextXOffset + mConsoleFont.getStringWidth(mInputText.toString()), y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f,
							ColorConstants.WHITE, 1f);
			}

		}

		final var lMessages = mProcessed ? mProcessedMessages : Debug.debugManager().logger().logLines();

		// output the messages
		final int lMessageCount = lMessages.size();
		if (lMessages != null && lMessageCount > 0) {
			for (int i = mLowerBound; i < mUpperBound; i++) {
				if (i >= 0 && i < lMessageCount) {
					final var lMessage = lMessages.get(i);
					if (lMessage == null)
						continue;

					lTextPosition -= mConsoleLineHeight;

					final float lR = getMessageRGB(lMessage.type).x;
					final float lG = getMessageRGB(lMessage.type).y;
					final float lB = getMessageRGB(lMessage.type).z;
					mConsoleTextColor.setRGBA(lR, lG, lB, 1.0f);

					// Draw Timestamp
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					mConsoleFont.drawText(lMessage.timestamp, x + POSITION_OFFSET_TIME, -lDisplayConfig.windowHeight() * 0.5f - lTextPosition, Z_DEPTH + 0.1f, mConsoleTextColor, 1f, 18);

					// Draw TAG
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					mConsoleFont.drawText(lMessage.tag, x + POSITION_OFFSET_TAG, -lDisplayConfig.windowHeight() * 0.5f - lTextPosition, Z_DEPTH + 0.1f, mConsoleTextColor, 1f, 18);

					// Draw MESSAGE
					mConsoleFont.setWrapType(WrapType.LetterCountTrim);
					final float lCharWidth = mConsoleFont.getStringWidth("e");
					final float lHorizontalSpace = pCore.HUD().getWidth() - POSITION_OFFSET_MESSAGE;
					mConsoleFont.drawText(lMessage.message, x + POSITION_OFFSET_MESSAGE, -lDisplayConfig.windowHeight() * 0.5f - lTextPosition, Z_DEPTH + 0.1f, mConsoleTextColor, 1f, lHorizontalSpace / lCharWidth - 3);
				}
			}
		}

		mSpriteBatch.end();
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
			mProcessed = false;
			mScrollBar.resetBarTop();

			mProcessedMessages.clear();
			mProcessed = !mTAGFilterText.isEmpty() || !mMessageFilterText.isEmpty();

			final var lLogLines = Debug.debugManager().logger().logLines();
			final var lLogLineCount = lLogLines.size();
			for (int i = 0; i < lLogLineCount; i++) {
				mProcessedMessages.add(lLogLines.get(i));

			}

			if (mProcessed) {
				if (!mTAGFilterText.isEmpty()) {
					mUpdateMessageList.clear();

					// First copy the whole LogMessages to the linkedlist, unfettered
					final int FILTER_LOG_COUNT = mProcessedMessages.size();
					for (int i = 0; i < FILTER_LOG_COUNT; i++) {
						mUpdateMessageList.add(mProcessedMessages.get(i));

					}

					mProcessedMessages.clear();
					final String lFilterText = mTAGFilterText.inputString().toString().toUpperCase();
					final int lUpdateMessageCount = mUpdateMessageList.size();
					for (int i = 0; i < lUpdateMessageCount; i++) {
						String lStringA = mUpdateMessageList.get(i).tag.toString().toUpperCase();
						String lStringB = lFilterText;
						if (lStringA.contains(lStringB)) {
							mProcessedMessages.add(mUpdateMessageList.get(i));

						}

					}

				}

				if (!mMessageFilterText.isEmpty()) {
					mUpdateMessageList.clear();

					// First copy the whole LogMessages to the linkedlist, unfettered
					final int FILTER_LOG_COUNT = mProcessedMessages.size();
					for (int i = 0; i < FILTER_LOG_COUNT; i++) {
						mUpdateMessageList.add(mProcessedMessages.get(i));

					}

					mProcessedMessages.clear();
					final String lFilterText = mMessageFilterText.inputString().toString().toUpperCase();
					final int lUpdateMessageCount = mUpdateMessageList.size();
					for (int i = 0; i < lUpdateMessageCount; i++) {
						String lStringA = mUpdateMessageList.get(i).message.toString().toUpperCase();
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
	 * @param pNewChar
	 */
	protected void updateConsole(final char pNewChar) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (pNewChar == '\n' || pNewChar == '\r') {
			// Add this string to the log
			Debug.debugManager().logger().u("User", mInputText.toString());

			if (mInputText.length() > 0) {
				mInputText.delete(0, mInputText.length());
			}

		} else {

			mInputText.append(pNewChar);

		}
	}

	protected void updateConsole(String pS) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v("Console", pS);

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
			// First check for ConstantsTable changes
			if (Pattern.matches(CONSTANTS_TABLE_COMMAND_PATTERN, lInputString)) {
				String[] lResultArray = lInputString.split("([\\=])");

				if (lResultArray != null && lResultArray.length == 2) {
					ConstantsApp.registerValue(lResultArray[0], lResultArray[1]);

					Debug.debugManager().logger().u("Settings Changed", lInputString);

					// Automatically scroll to the bottom when the user enters some text
					mAutoScroll = true;
					mDirty = true;

					// empty the current line
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

						// empty the current line
						mInputText.delete(0, mInputText.length());

						// Automatically scroll to the bottom when the user enters some text
						mAutoScroll = true;
						mDirty = true;

						return getEnterFinishesInput();

					}

				}

			}

		}

		Debug.debugManager().logger().u("User", lInputString);

		// empty the current line
		mInputText.delete(0, mInputText.length());

		// Automatically scroll to the bottom when the user enters some text
		mAutoScroll = true;
		mDirty = true;

		return getEnterFinishesInput();

	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputText;
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
	public float currentYPos() {
		return mScrollYPosition;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mScrollYPosition += pAmt;
	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mScrollYPosition = pValue;

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
	public void onKeyPressed(int pCodePoint) {

	}

	public void addConsoleCommand(ConsoleCommand pConsoleCommand) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mConsoleCommands.contains(pConsoleCommand)) {
			mConsoleCommands.add(pConsoleCommand);

		}

	}

	public void removeConsoleCommand(ConsoleCommand pConsoleCommand) {
		if (mConsoleCommands.contains(pConsoleCommand)) {
			mConsoleCommands.remove(pConsoleCommand);

		}

	}

	/** Writes a list of all console commands and their descriptions to the console. */
	public void listConsoleCommands() {
		final int CONSOLE_COMMAND_COUNT = mConsoleCommands.size();
		for (int i = 0; i < CONSOLE_COMMAND_COUNT; i++) {
			Debug.debugManager().logger().i(mConsoleCommands.get(i).Command, mConsoleCommands.get(i).Description);

		}

	}

	@Override
	public void captureStopped() {
		mHasFocus = false;

	}

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;

	}

}