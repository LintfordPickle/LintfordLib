package net.lintford.library.core.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugLogger.LogMessage;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IBufferedInputCallback;
import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.renderers.windows.components.UIInputText;

public class DebugConsole extends AARectangle implements IBufferedInputCallback, IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7219958843491782625L;
	
	private static final boolean CONSOLE_ENABLED = true;
	private static final boolean AUTO_CAPTURE_ON_OPEN = false;

	private static final String PROMT_CHAR = "> ";
	private static final String CARET_CHAR = "|";
	private static final float FOCUS_TIMER = 250;

	public static final String CONSOLE_FONT_NAME = "Console Font";

	public static final Vector3f DEFAULT_MESSAGE_RGB = new Vector3f(0.95f, 0.96f, 0.94f);
	public static final Vector3f VERBOSE_MESSAGE_RGB = new Vector3f(0.44f, 0.44f, 0.40f);
	public static final Vector3f INFO_MESSAGE_RGB = new Vector3f(0.94f, 0.94f, 0.90f);
	public static final Vector3f WARN_MESSAGE_RGB = new Vector3f(0.93f, 0.85f, 0.13f);
	public static final Vector3f ERR_MESSAGE_RGB = new Vector3f(0.93f, 0f, 0f);
	public static final Vector3f USER_MESSAGE_RGB = new Vector3f(0.47f, 0.77f, 0.9f);
	public static final Vector3f SYS_MESSAGE_RGB = new Vector3f(0.83f, 0.27f, 0f);

	public static Vector3f getMessageRGB(final int pMessageType) {
		switch (pMessageType) {
		case DebugManager.LOG_LEVEL_SYSTEM:
			return SYS_MESSAGE_RGB;
		case DebugManager.LOG_LEVEL_USER:
			return USER_MESSAGE_RGB;
		case DebugManager.LOG_LEVEL_ERROR:
			return ERR_MESSAGE_RGB;
		case DebugManager.LOG_LEVEL_WARNING:
			return WARN_MESSAGE_RGB;
		case DebugManager.LOG_LEVEL_INFO:
			return INFO_MESSAGE_RGB;
		case DebugManager.LOG_LEVEL_VERBOSE:
			return VERBOSE_MESSAGE_RGB;

		default:
			return DEFAULT_MESSAGE_RGB;

		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient boolean mActive;
	private transient boolean mOpen;
	private transient float mFocusTimer;
	private transient StringBuilder mInputText;

	private transient List<ConsoleCommand> mConsoleCommands;

	// This is the extent of all the lines of the debug console
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;

	private transient boolean mShowCaret;
	private transient float mCaretTimer;
	private transient TextureBatch mSpriteBatch;

	private transient FontUnit mConsoleFont;

	public transient float mFPSDraw;
	public transient float mFPSUpdate;
	private transient boolean mHasFocus;

	// Because we always need to display a range of text (e.g. lines 23-43), we
	// track that range in the following variables
	private transient int mLowerBound;
	private transient int mUpperBound;
	private transient int mConsoleLineHeight;

	private transient boolean mAutoScroll;
	private boolean mIsLoaded;

	private UIInputText mTAGFilterText;
	private int mTAGFilterLastSize;
	private UIInputText mMessageFilterText;
	private int mMessageFilterLastSize;

	protected boolean mProcessed; // is filter applied?
	protected List<LogMessage> mProcessedMessages;
	protected List<LogMessage> mUpdateMessageList;
	protected boolean mDirty;

	protected PrintStream mErrPrintStream;

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
		return mOpen;
	}

	public float openHeight() {
		return (float) Math.pow(16, 2);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	DebugConsole() {
		if (CONSOLE_ENABLED) {

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

			mActive = true;
			mOpen = false;

			mSpriteBatch = new TextureBatch();

			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);

			mConsoleCommands = new ArrayList<>();
			mAutoScroll = true;

			mTAGFilterText = new UIInputText(null);
			mTAGFilterText.emptyString("Filter");
			mTAGFilterText.textureName(TextureManager.TEXTURE_CORE_UI_NAME);

			mMessageFilterText = new UIInputText(null);
			mMessageFilterText.emptyString("Filter");
			mMessageFilterText.textureName(TextureManager.TEXTURE_CORE_UI_NAME);

			mProcessedMessages = new ArrayList<>();
			mUpdateMessageList = new ArrayList<>();

		} else {
			mActive = false;

		}

		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (!CONSOLE_ENABLED)
			return;

		DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "DebugConsole loading GL content");

		if (!CONSOLE_ENABLED)
			return;

		mConsoleFont = pResourceManager.fontManager().loadNewFont(CONSOLE_FONT_NAME, "/res/fonts/OxygenMono-Regular.ttf", 14);
		mSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!CONSOLE_ENABLED)
			return;

		DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "DebugConsole unloading GL content");

		mSpriteBatch.unloadGLContent();

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		if (!CONSOLE_ENABLED)
			return;

		if (mOpen) {
			mTAGFilterText.handleInput(pCore);
			mMessageFilterText.handleInput(pCore);

		}

		// listen for opening and closing
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F1)) {
			mOpen = !mOpen;
			if (mOpen) {
				mInputText.delete(0, mInputText.length());
				// if default to capture on open

				if (AUTO_CAPTURE_ON_OPEN) {
					mHasFocus = true;
					pCore.input().startCapture(this);

				}

			} else {
				mHasFocus = false;

			}
		}

		if (mOpen) {
			if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
				if (mOpen) {
					mOpen = false;
					mInputText.delete(0, mInputText.length());
					mHasFocus = false;
					pCore.input().stopCapture();

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

			else if (mTAGFilterText.intersects(pCore.HUD().getMouseCameraSpace())) {

			}

			else if (mMessageFilterText.intersects(pCore.HUD().getMouseCameraSpace())) {

			}

			else if (mFocusTimer > FOCUS_TIMER && pCore.input().mouseWindowCoords().y < openHeight() && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
				mHasFocus = !mHasFocus;
				pCore.input().stopCapture();
				mFocusTimer = 0;

				// If the debug console is open and has 'consumed' this click, don't let other windows use it
				pCore.input().setLeftMouseClickHandled();

				if (mHasFocus) {
					pCore.input().startCapture(this);
				}

			}

		}
	}

	public void update(LintfordCore pCore) {
		if (!mActive || !CONSOLE_ENABLED)
			return;
		
		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;

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

		if (mConsoleFont.bitmap() == null)
			mConsoleFont = pCore.resources().fontManager().systemFont();

		// Update the window content
		mConsoleLineHeight = (int) (mConsoleFont.bitmap().getStringHeight(" ") + 1);
		final int MAX_NUM_LINES = (int) ((openHeight() - mConsoleLineHeight * 2) / mConsoleLineHeight) - 1;

		final int lNumberLinesInConsole = mProcessed ? mProcessedMessages.size() : DebugManager.DEBUG_MANAGER.logger().logLines().size();
		fullContentArea().set(x, y, w - mScrollBar.w, lNumberLinesInConsole * 25);

		DisplayConfig lDisplay = pCore.config().display();
		// Update the bounds of the window view
		x = -lDisplay.windowSize().x * 0.5f;
		y = -lDisplay.windowSize().y * 0.5f;
		w = lDisplay.windowSize().x;
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

		mContentRectangle.h = (lNumberLinesInConsole + 2) * mConsoleLineHeight;

		// mAutoScroll = true;
		if (mAutoScroll) {
			int lNumLines = mProcessed ? mProcessedMessages.size() : DebugManager.DEBUG_MANAGER.logger().logLines().size();
			mUpperBound = lNumLines;
			mLowerBound = mUpperBound - MAX_NUM_LINES;

			mScrollYPosition = mScrollBar.getScrollYBottomPosition();

		}

		mScrollBar.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		if (!CONSOLE_ENABLED)
			return;

		if (!mActive || !mIsLoaded || !mOpen)
			return;
		
		final float Z_DEPTH = ZLayers.LAYER_DEBUG;

		final float POSITION_OFFSET_TIME = 5;
		final float POSITION_OFFSET_TAG = 130;
		final float POSITION_OFFSET_MESSAGE = 350;

		final float PADDING_LEFT = 5;
		final float lInputTextXOffset = 14;
		float lTextPosition = -20;

		mTAGFilterText.set(x + POSITION_OFFSET_TAG, y + 5, 200, 25);
		mMessageFilterText.set(x + POSITION_OFFSET_MESSAGE, y + 5, 200, 25);

		// Draw the console background (with a black border for the text input region)
		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 32, 0, 32, 32, x, y, w, h, Z_DEPTH, 0f, 0f, 0f, 0.85f);
		mSpriteBatch.end();

		// mContentRectangle.preDraw(pRenderState, mSpriteBatch);

		mConsoleFont.begin(pCore.HUD());

		List<LogMessage> lMessages = mProcessed ? mProcessedMessages : DebugManager.DEBUG_MANAGER.logger().logLines();

		DisplayConfig lDisplay = pCore.config().display();

		// output the messages
		final int MESSAGE_COUNT = lMessages.size();
		if (lMessages != null && MESSAGE_COUNT > 0) {
			for (int i = mLowerBound; i < mUpperBound; i++) {
				if (i >= 0 && i < MESSAGE_COUNT) {
					final LogMessage MESSAGE = lMessages.get(i);
					if (MESSAGE == null)
						continue;

					lTextPosition -= mConsoleLineHeight;

					final float lR = getMessageRGB(MESSAGE.type).x;
					final float lG = getMessageRGB(MESSAGE.type).y;
					final float lB = getMessageRGB(MESSAGE.type).z;

					// Draw Timestamp
					mConsoleFont.draw(MESSAGE.timestamp, x + POSITION_OFFSET_TIME, -lDisplay.windowSize().y * 0.5f - lTextPosition, Z_DEPTH + 0.1f, lR, lG, lB, 1.0f, 1f, -1, 18);

					// Draw TAG
					mConsoleFont.draw(MESSAGE.tag, x + POSITION_OFFSET_TAG, -lDisplay.windowSize().y * 0.5f - lTextPosition, Z_DEPTH + 0.1f, lR, lG, lB, 1.0f, 1f, -1, 18);

					// Draw MESSAGE
					mConsoleFont.draw(MESSAGE.message, x + POSITION_OFFSET_MESSAGE, -lDisplay.windowSize().y * 0.5f - lTextPosition, Z_DEPTH + 0.1f, lR, lG, lB, 1.0f, 1f, -1, -1);

				}

			}

		}

		// the input line from the user will always be visible at the bottom of the console.
		if (mInputText != null) {
			final float INPUT_Y_OFFSET = 0;
			mConsoleFont.draw(PROMT_CHAR, -lDisplay.windowSize().x * 0.5f + PADDING_LEFT, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, 1f);
			mConsoleFont.draw(mInputText.toString(), -lDisplay.windowSize().x * 0.5f + PADDING_LEFT + lInputTextXOffset, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, 1f);
			if (mShowCaret && mHasFocus)
				mConsoleFont.draw(CARET_CHAR, -lDisplay.windowSize().x * 0.5f + PADDING_LEFT + lInputTextXOffset + mConsoleFont.bitmap().getStringWidth(mInputText.toString()), y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f,
						1f);
		}

		mTAGFilterText.draw(pCore, mSpriteBatch, mConsoleFont, Z_DEPTH+0.01f);
		mMessageFilterText.draw(pCore, mSpriteBatch, mConsoleFont, Z_DEPTH+0.01f);

		mConsoleFont.end();

		mScrollBar.draw(pCore, mSpriteBatch, Z_DEPTH + 0.1f);

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

			List<LogMessage> lLogLines = DebugManager.DEBUG_MANAGER.logger().logLines();
			// First, assume all LogMessages are accepted
			final int LOG_LINE_COUNT = lLogLines.size();
			for (int i = 0; i < LOG_LINE_COUNT; i++) {
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
		if (!CONSOLE_ENABLED)
			return;

		if (pNewChar == '\n' || pNewChar == '\r') {
			// Add this string to the log
			DebugManager.DEBUG_MANAGER.logger().u("User", mInputText.toString());

			if (mInputText.length() > 0) {
				mInputText.delete(0, mInputText.length());
			}

		} else {

			mInputText.append(pNewChar);

		}
	}

	protected void updateConsole(String pS) {
		if (!CONSOLE_ENABLED)
			return;

		DebugManager.DEBUG_MANAGER.logger().v("Console", pS);

	}

	// --------------------------------------
	// Implements
	// --------------------------------------

	@Override
	public boolean onEscapePressed() {
		mHasFocus = false;
		mOpen = false;

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

		final String INPUT_STRING = mInputText.toString();

		if (INPUT_STRING != null) {
			DebugManager.DEBUG_MANAGER.logger().u("User", INPUT_STRING);

			boolean lResult = false;
			final int CONSOLE_COMMANDS = mConsoleCommands.size();
			for (int i = 0; i < CONSOLE_COMMANDS; i++) {
				if (INPUT_STRING.equals(mConsoleCommands.get(i).Command)) {
					lResult = mConsoleCommands.get(i).doCommand();
					DebugManager.DEBUG_MANAGER.logger().u("", "  completed " + (lResult ? "successfully" : "with errors"));

				}

			}

		}

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
	public AARectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;
	}

	@Override
	public void onKeyPressed(char pCh) {
		// mDirty = true;

	}

	public void addConsoleCommand(ConsoleCommand pConsoleCommand) {
		if (!CONSOLE_ENABLED)
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
			DebugManager.DEBUG_MANAGER.logger().i("", mConsoleCommands.get(i).Command + ": " + mConsoleCommands.get(i).Description);

		}

	}

	@Override
	public void captureStopped() {
		mHasFocus = false;

	}

}