package net.lintfordlib.screenmanager.toast;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.graphics.fonts.FontMetaData;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.options.DisplayManager;

public class ToastManager {

	public static final FontMetaData FONTS_META = new FontMetaData();

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FONT_TOAST_NAME = "FONT_TOAST";

	private static final float SCREEN_PADDING_X = 10.f;
	private static final float SCREEN_PADDING_Y = 50.f;

	private static final int MAX_TOASTPOOL_SIZE = 48;
	private static final int MIN_TIME_BETWEEN_ADD = 250;

	/** Specificies the amount of time, in milliseconds, that a message should be displayed in the tasot-message panel by default. */
	public static final int DEFAULT_TOAST_MESSAGE_DISPLAY_TIME_MS = 1000;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatchPCT mTextureBatch;
	private FontUnit mFontUnit;
	private List<ToastMessage> mToastMessages;
	private List<ToastMessage> mToastMessagePool;
	private List<ToastMessage> mToastMessageUpdate;
	private int mToastCounter;

	private float mAddTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private DisplayManager mDisplayManager;

	public ToastManager(DisplayManager displayManager) {
		mDisplayManager = displayManager;

		mToastMessages = new ArrayList<>();
		mToastMessagePool = new ArrayList<>();
		mToastMessageUpdate = new ArrayList<>();

		mTextureBatch = new TextureBatchPCT();

		mToastCounter = 0;
		mAddTimer = MIN_TIME_BETWEEN_ADD;

		allocateNewMesssages(8);

		FONTS_META.AddIfNotExists(FONT_TOAST_NAME, "/res/fonts/fontCore.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mTextureBatch.loadResources(resourceManager);
		mFontUnit = resourceManager.fontManager().getFontUnit(FONT_TOAST_NAME);
	}

	public void unloadResources() {
		mTextureBatch.unloadResources();
		mFontUnit = null;
	}

	public void update(LintfordCore pCore) {

		mAddTimer += pCore.appTime().elapsedTimeMilli();

		mToastMessageUpdate.clear();
		final int SIZE_T = mToastMessages.size();
		for (int i = 0; i < SIZE_T; i++) {
			mToastMessageUpdate.add(mToastMessages.get(i));

		}

		float lFinalX = -mDisplayManager.windowWidth() / 2 + SCREEN_PADDING_X;
		float lFinalY = mDisplayManager.windowHeight() / 2 - SCREEN_PADDING_Y;

		for (int i = 0; i < SIZE_T; i++) {
			final var toastMessage = mToastMessageUpdate.get(i);

			toastMessage.liveLeft -= pCore.appTime().elapsedTimeMilli();

			if (toastMessage.liveLeft < 0) {
				toastMessage.reset();
				mToastMessages.remove(toastMessage);
				continue;
			}

			toastMessage.x = toastMessage.xx = lFinalX;
			toastMessage.yy = lFinalY;

			if (toastMessage.y < toastMessage.yy)
				toastMessage.y += 500f * pCore.appTime().elapsedTimeMilli() / 1000f;

			lFinalY -= 25;
		}
	}

	public void draw(LintfordCore pCore) {
		mToastMessageUpdate.clear();
		final int SIZE_T = mToastMessages.size();
		for (int i = 0; i < SIZE_T; i++) {
			mToastMessageUpdate.add(mToastMessages.get(i));

		}

		mTextureBatch.begin(pCore.HUD());
		mFontUnit.begin(pCore.HUD());

		final int TOAST_SIZE = mToastMessageUpdate.size();
		for (int i = 0; i < TOAST_SIZE; i++) {
			mToastMessageUpdate.get(i).draw(pCore, mFontUnit, mTextureBatch);

		}

		mFontUnit.end();
		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addMessage(String title, String message) {
		addMessage(title, message, DEFAULT_TOAST_MESSAGE_DISPLAY_TIME_MS);
	}

	public void addMessage(String title, String message, int messageLifeTimeInMs) {
		if (mAddTimer < MIN_TIME_BETWEEN_ADD)
			return;

		if (message == null || message.length() == 0)
			return;

		mAddTimer = 0;

		final var lToastMessage = getFreeToast();
		mToastMessages.add(lToastMessage);

		if (lToastMessage == null)
			return;

		// Place the message a little higher than its resting place -.^.-
		final var lNumMessagesInQueue = mToastMessageUpdate.size();
		final var lQueueHeight = lNumMessagesInQueue * 25.f;

		final var lDropHeight = 50.f;
		lToastMessage.y = mDisplayManager.windowHeight() / 2 - SCREEN_PADDING_Y - lQueueHeight - lDropHeight;

		lToastMessage.init(title, message, messageLifeTimeInMs);
	}

	private ToastMessage getFreeToast() {
		if (mToastMessagePool.size() > 0) {
			final var lToastMessage = mToastMessagePool.get(mToastMessagePool.size() - 1);
			mToastMessagePool.remove(mToastMessagePool.size() - 1);
			return lToastMessage;
		}

		return allocateNewMesssages(8);
	}

	private ToastMessage allocateNewMesssages(int amountToPreAllocate) {
		if (amountToPreAllocate < 1)
			amountToPreAllocate = 2;

		if (mToastCounter + amountToPreAllocate > MAX_TOASTPOOL_SIZE) {
			amountToPreAllocate = MAX_TOASTPOOL_SIZE - mToastCounter;
		}

		final var lReturnMessage = new ToastMessage();
		mToastCounter++;

		for (int i = 0; i < amountToPreAllocate - 1; i++) {
			mToastCounter++;
			mToastMessagePool.add(new ToastMessage());
		}

		return lReturnMessage;
	}

}
