package net.lintford.library.core.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugLogger.LogMessage;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IBufferedInputCallback;
import net.lintford.library.core.maths.Vector3f;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.renderers.windows.UIRectangle;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;

public class DebugConsole extends UIRectangle implements IBufferedInputCallback, IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final boolean CONSOLE_ENABLED = true;

	private static final float Z_DEPTH = -1f;

	private static final float OPEN_SPEED = 3.0f;
	private static final String PROMT_CHAR = "> ";
	private static final float FOCUS_TIMER = 250;

	public static final String CONSOLE_FONT_NAME = "Console Font";

	public static final Vector3f DEFAULT_MESSAGE_RGB = new Vector3f(0.95f, 0.96f, 0.94f);
	public static final Vector3f VERBOSE_MESSAGE_RGB = new Vector3f(0.44f, 0.44f, 0.40f);
	public static final Vector3f INFO_MESSAGE_RGB = new Vector3f(0.94f, 0.94f, 0.90f);
	public static final Vector3f WARN_MESSAGE_RGB = new Vector3f(0.93f, 0.85f, 0.13f);
	public static final Vector3f ERR_MESSAGE_RGB = new Vector3f(0.93f, 0f, 0f);
	public static final Vector3f USER_MESSAGE_RGB = new Vector3f(0.47f, 0.77f, 0.9f);
	public static final Vector3f SYS_MESSAGE_RGB = new Vector3f(0.83f, 0.27f, 0f);

	// TODO (John): RGB doesn't belong in the logger class. Move it
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
	private transient float mOpenHeight;

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

	// --------------------------------------
	// Properties
	// --------------------------------------

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
			PrintStream lPrintStream = new PrintStream(System.out) {
				public void print(String s) {
					if (!s.isEmpty()) {
						super.print(s);
						updateConsole(s);
					}
				};

			};

			System.setOut(lPrintStream);

			mInputText = new StringBuilder();

			mActive = true;
			mOpen = false;

			mSpriteBatch = new TextureBatch();

			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);

			mConsoleCommands = new ArrayList<>();
			mAutoScroll = true;

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

		mConsoleFont = pResourceManager.fontManager().systemFont();

		mSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!CONSOLE_ENABLED)
			return;

		DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "DebugConsole unloading GL content");

		mSpriteBatch.unloadGLContent();
		// TODO: Unload fonts when no longer needed?

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		if (!CONSOLE_ENABLED)
			return;

		if (mScrollBar.handleInput(pCore)) {
			mAutoScroll = false;

			final float FONT_HEIGHT = mConsoleFont.bitmap().getStringHeight(" ");
			final int MAX_NUM_LINES = (int) ((openHeight() - FONT_HEIGHT) / FONT_HEIGHT);

			mUpperBound = (int) -((mScrollYPosition) / FONT_HEIGHT) + MAX_NUM_LINES;

			if (mUpperBound == DebugManager.DEBUG_MANAGER.logger().logLines().size()) {
				mAutoScroll = true;
			} else {
				mAutoScroll = false;
			}

			return;
		}

		// listen for opening and closing
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F1)) {
			mOpen = !mOpen;
			mHasFocus = false;
			if (mOpen) {
				mInputText.delete(0, mInputText.length());
			}
		}

		if (mOpen) {

			if (mFocusTimer > FOCUS_TIMER && pCore.input().mouseWindowCoords().y < openHeight() && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
				mHasFocus = !mHasFocus;
				pCore.input().stopCapture();
				mFocusTimer = 0;

				// If the debug console is open and has 'consumed' this click, don't let other windows use it
				pCore.input().setLeftMouseClickHandled();

			}

			if (mHasFocus) {
				pCore.input().startCapture(this);
			}

			float lHUDMouseX = pCore.HUD().getMouseWorldSpaceX();
			float lHUDMouseY = pCore.HUD().getMouseWorldSpaceY();

			if (pCore.input().mouseLeftClick() && intersects(lHUDMouseX, lHUDMouseY)) {
				// Consume the left click
				pCore.input().tryAquireLeftClickOwnership(hashCode());

			}

		}
	}

	public void update(LintfordCore pCore) {
		if (!CONSOLE_ENABLED)
			return;

		if (!mActive)
			return;

		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;

		mFocusTimer += lDeltaTime;
		mCaretTimer += lDeltaTime;

		contentArea().set(x, y, width - mScrollBar.width, DebugManager.DEBUG_MANAGER.logger().logLines().size() * 25);

		if (mCaretTimer > 250) {
			mCaretTimer = 0;
			mShowCaret = !mShowCaret;
		}

		final int lOPEN_HEIGHT = 500;
		if (mOpen && mOpenHeight < lOPEN_HEIGHT) {
			mOpenHeight += OPEN_SPEED * lDeltaTime;

		} else if (mOpen && mOpenHeight > lOPEN_HEIGHT) {
			mOpenHeight = lOPEN_HEIGHT;
		}

		else if (!mOpen && mOpenHeight > 0.0f) {
			mOpenHeight -= OPEN_SPEED * lDeltaTime;
		}

		DisplayConfig lDisplay = pCore.config().display();
		// Update the bounds of the window view
		x = -lDisplay.windowSize().x * 0.5f;
		y = -lDisplay.windowSize().y * 0.5f;
		width = lDisplay.windowSize().x;
		height = openHeight();

		mConsoleLineHeight = (int) (mConsoleFont.bitmap().getStringHeight(" ") + 4);
		final int MAX_NUM_LINES = (int) ((openHeight() - mConsoleLineHeight * 2) / mConsoleLineHeight);
		mContentRectangle.height = (DebugManager.DEBUG_MANAGER.logger().logLines().size() + 2) * mConsoleLineHeight;

		mLowerBound = (int) -((mScrollYPosition) / mConsoleLineHeight) + 1;
		mUpperBound = mLowerBound + MAX_NUM_LINES;

		if (mAutoScroll) {
			mUpperBound = DebugManager.DEBUG_MANAGER.logger().logLines().size();
			mLowerBound = mUpperBound - MAX_NUM_LINES;
			mScrollYPosition = mScrollBar.getScrollYBottomPosition();
		}

		mScrollBar.update(pCore);
	}

	public void draw(LintfordCore pCore) {
		if (!CONSOLE_ENABLED)
			return;

		if (!mActive || !mOpen)
			return;

		final float PADDING_LEFT = 15;
		final float lInputTextXOffset = 14;
		float lTextPosition = 0;

		// Draw the console background (with a black border for the text input region)
		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(32, 0, 32, 32, x, y, Z_DEPTH, width, height, 1.0f, 0f, 0f, 0f, 0.85f, TextureManager.TEXTURE_CORE_UI);
		mSpriteBatch.end();

		// mContentRectangle.preDraw(pRenderState, mSpriteBatch);

		mConsoleFont.begin(pCore.HUD());

		final List<LogMessage> LOG_MESSAGES = DebugManager.DEBUG_MANAGER.logger().logLines();

		DisplayConfig lDisplay = pCore.config().display();

		// output the messages
		if (LOG_MESSAGES != null && LOG_MESSAGES.size() > 0) {
			for (int i = mLowerBound; i < mUpperBound; i++) {
				if (i > 0 && i < LOG_MESSAGES.size()) {
					final LogMessage MESSAGE = LOG_MESSAGES.get(i);
					lTextPosition -= mConsoleLineHeight;

					final float lR = getMessageRGB(MESSAGE.type).x;
					final float lG = getMessageRGB(MESSAGE.type).y;
					final float lB = getMessageRGB(MESSAGE.type).z;

					// Draw TAG
					mConsoleFont.draw(MESSAGE.tag, x + PADDING_LEFT, -lDisplay.windowSize().y * 0.5f - lTextPosition, Z_DEPTH + 0.1f, lR, lG, lB, 1.0f, 1f, -1, 18);

					// Draw MESSAGE
					final float MESSAGE_POSITION_OFFSET = 200;

					mConsoleFont.draw(MESSAGE.message, x + PADDING_LEFT + MESSAGE_POSITION_OFFSET, -lDisplay.windowSize().y * 0.5f - lTextPosition, Z_DEPTH + 0.1f, lR, lG, lB, 1.0f, 1f, -1, -1);

				}

			}

		}

		// the input line from the user will always be visible at the bottom of the console.
		if (mInputText != null) {
			final float INPUT_Y_OFFSET = 0;
			mConsoleFont.draw(PROMT_CHAR, -lDisplay.windowSize().x * 0.5f + PADDING_LEFT, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, 1f);
			mConsoleFont.draw(mInputText.toString(), -lDisplay.windowSize().x * 0.5f + PADDING_LEFT + lInputTextXOffset, y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, 1f);
			if (mShowCaret && mHasFocus)
				mConsoleFont.draw("|", -lDisplay.windowSize().x * 0.5f + PADDING_LEFT + lInputTextXOffset + mConsoleFont.bitmap().getStringWidth(mInputText.toString()), y + openHeight() - mConsoleLineHeight + INPUT_Y_OFFSET, Z_DEPTH + 0.1f, 1f);
		}

		mConsoleFont.end();

		mScrollBar.draw(pCore, mSpriteBatch, Z_DEPTH + 0.1f);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Used to pass the console output stream to the debugconsole (character-by-character).
	 * 
	 * @param pNewChar
	 */
	protected void updateConsole(final char pNewChar) {
		if (!CONSOLE_ENABLED)
			return;

		if (pNewChar == '\n' || pNewChar == '\r') {

			if (mInputText.equals("")) {
				if (mInputText.length() > 0) {
					mInputText.delete(0, mInputText.length());
				}
			} else {

				// Add this string to the log
				DebugManager.DEBUG_MANAGER.logger().u("User", mInputText.toString());

				if (mInputText.length() > 0) {
					mInputText.delete(0, mInputText.length());
				}
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

	public void open() {

	}

	public void close() {

	}

	// --------------------------------------
	// Implements
	// --------------------------------------

	@Override
	public void onEscapePressed() {
		mHasFocus = false;

		if (mInputText.length() > 0) {
			mInputText.delete(0, mInputText.length());
		}
	}

	@Override
	public void onEnterPressed() {

		if (mInputText.length() == 0) {
			mHasFocus = false;
			return;
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

	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputText;
	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
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
	public UIRectangle windowArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle contentArea() {
		return mContentRectangle;
	}

	@Override
	public void onKeyPressed(char pCh) {

	}

	public void addConsoleCommand(ConsoleCommand pConsoleCommand) {
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
}