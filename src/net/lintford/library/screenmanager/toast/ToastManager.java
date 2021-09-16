package net.lintford.library.screenmanager.toast;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontMetaData;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

public class ToastManager {

	public static final FontMetaData FONTS_META = new FontMetaData();

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FONT_TOAST_NAME = "FONT_TOAST";

	private static final int MAX_TOASTPOOL_SIZE = 48;
	private static final int MIN_TIME_BETWEEN_ADD = 250;

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

	public ToastManager() {
		mToastMessages = new ArrayList<>();
		mToastMessagePool = new ArrayList<>();
		mToastMessageUpdate = new ArrayList<>();

		mTextureBatch = new TextureBatchPCT();

		mToastCounter = 0;
		allocateNewMesssages(8);

		FONTS_META.AddIfNotExists(FONT_TOAST_NAME, "/res/fonts/fontCore.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mTextureBatch.loadGLContent(pResourceManager);
		mFontUnit = pResourceManager.fontManager().getFontUnit(FONT_TOAST_NAME);
	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();
		mFontUnit = null;

	}

	public void update(LintfordCore pCore) {

		mAddTimer += pCore.appTime().elapsedTimeMilli();

		mToastMessageUpdate.clear();
		final int SIZE_T = mToastMessages.size();
		for (int i = 0; i < SIZE_T; i++) {
			mToastMessageUpdate.add(mToastMessages.get(i));

		}

		float lFinalX = -pCore.config().display().windowWidth() / 2;
		float lFinalY = pCore.config().display().windowHeight() / 2 - 30;

		for (int i = 0; i < SIZE_T; i++) {
			ToastMessage lTM = mToastMessageUpdate.get(i);

			lTM.liveLeft -= pCore.appTime().elapsedTimeMilli();

			if (lTM.liveLeft < 0) {
				lTM.reset();
				mToastMessages.remove(lTM);
				continue;
			}

			lTM.x = lTM.xx = lFinalX;
			lTM.yy = lFinalY;

			if (lTM.y < lTM.yy)
				lTM.y += 500f * pCore.appTime().elapsedTimeMilli() / 1000f;

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

	public void addMessage(String pTitle, String pMessage) {
		if (mAddTimer < MIN_TIME_BETWEEN_ADD)
			return;

		if (pMessage == null || pMessage.length() == 0)
			return;

		mAddTimer = 0;

		ToastMessage tm = getFreeToast();
		mToastMessages.add(tm);

		if (tm == null)
			return;

		tm.y = 0;

		tm.init(pTitle, pMessage, 3000);

	}

	private ToastMessage getFreeToast() {
		if (mToastMessagePool.size() > 0) {
			ToastMessage lTM = mToastMessagePool.get(mToastMessagePool.size() - 1);
			mToastMessagePool.remove(mToastMessagePool.size() - 1);
			return lTM;
		}

		return allocateNewMesssages(8);

	}

	private ToastMessage allocateNewMesssages(int pAmt) {
		if (pAmt < 1)
			pAmt = 2;

		if (mToastCounter + pAmt > MAX_TOASTPOOL_SIZE) {
			pAmt = MAX_TOASTPOOL_SIZE - mToastCounter;

		}

		ToastMessage lReturn = new ToastMessage();
		mToastCounter++;

		for (int i = 0; i < pAmt - 1; i++) {
			mToastCounter++;
			mToastMessagePool.add(new ToastMessage());

		}

		return lReturn;

	}

}
