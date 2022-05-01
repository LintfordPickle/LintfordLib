package net.lintford.library.screenmanager.layouts;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;

/**
 * The list layout lays out all the menu entries linearly down the layout.
 * 
 * @author Lintford Pickle
 */
public class ListLayout extends BaseLayout implements IProcessMouseInput {

	// --------------------------------------
	// COnstants
	// --------------------------------------

	private static final long serialVersionUID = -7568188688210642680L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mClickTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListLayout(MenuScreen pParentScreen) {
		super(pParentScreen);

		mMinWidth = 0.f;
		mMaxWidth = 900.f;
	}

	public ListLayout(MenuScreen pParentScreen, float pX, float pY) {
		this(pParentScreen);

		x = pX;
		y = pY;
	}

	public ListLayout(MenuScreen pParentScreen, float pX, float pY, float pW, float pH) {
		this(pParentScreen, pX, pY);

		w = pW;
		h = pH;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (super.handleInput(pCore) || pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				return true;
			}

		}

		// otherwise, defocus this and all children ??
		else {
			final int lEntryCount = mMenuEntries.size();
			for (int i = 0; i < lEntryCount; i++) {
				final var lEntry = mMenuEntries.get(i);

				lEntry.hoveredOver(false);
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mClickTimer >= 0) {
			mClickTimer -= pCore.appTime().elapsedTimeMilli();
		}
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		final var lUiStructureController = parentScreen.screenManager.UiStructureController();
		final float lTitleHeight = mShowTitle ? parentScreen.rendererManager.titleFontHeight() : 0.f;
		final float lWindowScaleFactorX = lUiStructureController.windowAutoScaleFactorX();

		float lYPos = y + mEntryOffsetFromTop + lTitleHeight + mScrollBar.currentYPos() + mCropPaddingTop + paddingTop();

		final int lEntryCount = mMenuEntries.size();

		final float lLayoutHeight = h - marginBottom() - marginTop() - lTitleHeight - mCropPaddingBottom - mCropPaddingTop + paddingTop() + paddingBottom();

		// If the height of the content is smaller than the height of this layout, disable the scroll bar
		if (mContentArea.h() < lLayoutHeight) {
			mScrollBar.AbsCurrentYPos(0);
			lYPos += 5f;
		}

		// See how many layouts only take what they need
		int lCountOfSharers = lEntryCount;
		int lCountOfTakers = 0;

		float lHeightTaken = marginTop() + marginBottom();

		for (int i = 0; i < lEntryCount; i++) {
			final var lEntry = mMenuEntries.get(i);
			if (lEntry.active() == false)
				continue;
			if (lEntry.verticalFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lCountOfTakers++;
				lHeightTaken += lEntry.paddingTop() + lEntry.height() + lEntry.paddingBottom();
			}
		}

		lCountOfSharers -= lCountOfTakers;

		float lSizeOfEachFillElement = ((lLayoutHeight - lHeightTaken) / lCountOfSharers);

		if (lSizeOfEachFillElement < 0)
			lSizeOfEachFillElement = 10;

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (lMenuEntry.active() == false)
				continue;
			float lScrollBarWidth = 0.f;
			if (mScrollBar.scrollBarEnabled())
				lScrollBarWidth = mScrollBar.width();

			final float lSpacingLeft = (mLeftPadding + lMenuEntry.marginLeft()) * lWindowScaleFactorX;
			final float lSpacingRight = (mRightPadding + lMenuEntry.marginRight()) * lWindowScaleFactorX;

			final float lNewEntryWidth = w - lSpacingLeft - lSpacingRight - lScrollBarWidth;

			if (lMenuEntry.horizontalFillType() == FILLTYPE.FILL_CONTAINER) {
				lMenuEntry.w(lNewEntryWidth);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.QUARTER_PARENT) {
				lMenuEntry.w(lNewEntryWidth * .25f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.HALF_PARENT) {
				lMenuEntry.w(lNewEntryWidth * .5f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.THREEQUARTER_PARENT) {
				lMenuEntry.w(lNewEntryWidth * .75f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.TAKE_DESIRED_SIZE) {
				lMenuEntry.w(MathHelper.clamp(lMenuEntry.desiredWidth() * lUiStructureController.windowAutoScaleFactorX(), lMenuEntry.minWidth(), lMenuEntry.maxWidth()));
			} else {
				lMenuEntry.w(lNewEntryWidth);
			}

			lMenuEntry.x(centerX() - lMenuEntry.w() / 2 - lScrollBarWidth / 2);

			// Assign the entry height here
			if (lMenuEntry.verticalFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lMenuEntry.h(MathHelper.clamp(lMenuEntry.desiredHeight(), lMenuEntry.minHeight(), lMenuEntry.maxHeight()));
			} else {
				lMenuEntry.h(lSizeOfEachFillElement);
			}

			lMenuEntry.y(lYPos);

			lYPos += lMenuEntry.marginTop();
			lYPos += lMenuEntry.height();
			lYPos += lMenuEntry.marginBottom();
		}
	}

	// --------------------------------------
	// IProcessMouseInput-Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mClickTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mClickTimer = 200;

	}

}
