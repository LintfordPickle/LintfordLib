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

	public ListLayout(MenuScreen parentScreen) {
		super(parentScreen);

		mMinWidth = 0.f;
		mMaxWidth = 900.f;

		// inevitably, there is some portion of the background graphic which 
		// shouldn't have content rendered over it. that's this
		mCropPaddingBottom = 0.f;
		mCropPaddingTop = 0.f;
	}

	public ListLayout(MenuScreen parentScreen, float x, float y) {
		this(parentScreen);

		mX = x;
		mY = y;
	}

	public ListLayout(MenuScreen parentScreen, float x, float y, float width, float height) {
		this(parentScreen, x, y);

		mW = width;
		mH = height;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		final var lIntersectsAA = intersectsAA(core.HUD().getMouseCameraSpace());
		if (lIntersectsAA && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (super.handleInput(core) || core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
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
	public void update(LintfordCore core) {
		super.update(core);

		if (mClickTimer >= 0) {
			mClickTimer -= core.appTime().elapsedTimeMilli();
		}
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		final var lUiStructureController = parentScreen.screenManager().UiStructureController();
		final float lTitleHeight = mShowTitle ? parentScreen.rendererManager().headerFontHeight() : 0.f;
		final float lWindowScaleFactorX = lUiStructureController.windowAutoScaleFactorX();

		float lYPos = mY + mEntryOffsetFromTop + lTitleHeight + mScrollBar.currentYPos() + mCropPaddingTop + paddingTop();

		final int lEntryCount = mMenuEntries.size();
		final float lLayoutHeight = mH - marginBottom() - marginTop() - lTitleHeight - mCropPaddingBottom - mCropPaddingTop + paddingTop() + paddingBottom();

		// If the height of the content is smaller than the height of this layout, disable the scroll bar
		if (mContentArea.height() < lLayoutHeight) {
			mScrollBar.AbsCurrentYPos(0);
			lYPos += 5f;
		}

		// See how many layouts only take what they need
		int lCountOfSharers = lEntryCount;
		int lCountOfTakers = 0;

		float lHeightTaken = marginBottom() + marginTop() + paddingTop() + paddingBottom();

		for (int i = 0; i < lEntryCount; i++) {
			final var lEntry = mMenuEntries.get(i);
			if (lEntry.isDormant())
				continue;
			if (lEntry.verticalFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lCountOfTakers++;
				lHeightTaken += lEntry.marginTop() + lEntry.height() + lEntry.marginBottom();
			}
		}

		lCountOfSharers -= lCountOfTakers;

		float lSizeOfEachFillElement = lCountOfSharers == 0f ? 0f : ((lLayoutHeight - lHeightTaken) / lCountOfSharers);

		if (lSizeOfEachFillElement < 0)
			lSizeOfEachFillElement = 10;

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (lMenuEntry.isDormant())
				continue;
			float lScrollBarWidth = 0.f;
			if (mScrollBar.scrollBarEnabled())
				lScrollBarWidth = mScrollBar.width();

			final float lSpacingLeft = (mLeftPadding + lMenuEntry.marginLeft()) * lWindowScaleFactorX;
			final float lSpacingRight = (mRightPadding + lMenuEntry.marginRight()) * lWindowScaleFactorX;

			final float lNewEntryWidth = mW - lSpacingLeft - lSpacingRight - lScrollBarWidth;

			if (lMenuEntry.horizontalFillType() == FILLTYPE.FILL_CONTAINER) {
				lMenuEntry.width(lNewEntryWidth);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.QUARTER_PARENT) {
				lMenuEntry.width(lNewEntryWidth * .25f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.HALF_PARENT) {
				lMenuEntry.width(lNewEntryWidth * .5f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.THREEQUARTER_PARENT) {
				lMenuEntry.width(lNewEntryWidth * .75f);
			} else if (lMenuEntry.horizontalFillType() == FILLTYPE.TAKE_DESIRED_SIZE) {
				lMenuEntry.width(MathHelper.clamp(lMenuEntry.desiredWidth() * lUiStructureController.windowAutoScaleFactorX(), lMenuEntry.minWidth(), lMenuEntry.maxWidth()));
			} else {
				lMenuEntry.width(lNewEntryWidth);
			}

			lMenuEntry.x(centerX() - lMenuEntry.width() / 2 - lScrollBarWidth / 2);

			// Assign the entry height here
			if (lMenuEntry.verticalFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lMenuEntry.height(MathHelper.clamp(lMenuEntry.desiredHeight(), lMenuEntry.minHeight(), lMenuEntry.maxHeight()));
			} else {
				lMenuEntry.height(lSizeOfEachFillElement - lMenuEntry.marginBottom() - lMenuEntry.marginTop());
			}

			lMenuEntry.y(lYPos);

			lYPos += lMenuEntry.marginTop();
			lYPos += lMenuEntry.height();
			lYPos += lMenuEntry.marginBottom();
		}

		if (mScrollBar.scrollBarEnabled() == false) {
			mScrollBar.resetBarTop();
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
