package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuEnumImageEntry extends MenuEntry {

	private static final long serialVersionUID = -4902595949146396834L;

	public class MenuEnumEntryItem {
		public final int uid;
		public final String spriteFrameName;
		public final Color color = new Color(ColorConstants.WHITE());

		public MenuEnumEntryItem(String spriteFrameName) {
			this(spriteFrameName, -1);
		}

		public MenuEnumEntryItem(String spriteFrameName, int uid) {
			this(spriteFrameName, uid, ColorConstants.WHITE());
		}

		public MenuEnumEntryItem(String spriteFrameName, Color color) {
			this(spriteFrameName, -1, color);
		}

		public MenuEnumEntryItem(String spriteFrameName, int uid, Color color) {
			this.uid = uid;
			this.spriteFrameName = spriteFrameName;
			this.color.setFromColor(color);
		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = ":";
	private List<MenuEnumEntryItem> mItems;
	private int mSelectedIndex;
	private boolean mEnableScaleTextToWidth;
	private int mEntityGroupUid;
	private boolean mButtonsEnabled;
	private Rectangle mLeftButtonRectangle;
	private Rectangle mRightButtonRectangle;

	private String mSpriteSheetDefinitionName;
	private SpriteSheetDefinition mImageSpriteSheetDefinition;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String spriteSheetDefinitionName() {
		return mSpriteSheetDefinitionName;
	}

	public void spriteSheetDefinitionName(String newValue) {
		mSpriteSheetDefinitionName = newValue;
	}

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	public void setListener(EntryInteractions listener) {
		mClickListener = listener;
	}

	public void setButtonsEnabled(boolean newValue) {
		mButtonsEnabled = newValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public MenuEnumEntryItem selectedItem() {
		return mItems.get(mSelectedIndex);

	}

	public int selectedEntry() {
		return mSelectedIndex;
	}

	public String selectedEntryName() {
		return mItems.get(mSelectedIndex).spriteFrameName;
	}

	public void setSelectedEntry(int index) {
		if (index < 0)
			index = 0;
		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean newValue) {
		mIsChecked = newValue;
	}

	public void setNewSpriteSheetDefinition() {
		// TODO: if control already loaded, then we need to clean up first and reload the correct sprites ...
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEnumImageEntry(ScreenManager screenManager, MenuScreen parentScreen, int entityGroupUid) {
		this(screenManager, parentScreen, null, entityGroupUid);
	}

	public MenuEnumImageEntry(ScreenManager screenManager, MenuScreen parentScreen, String spritesheetName, int entityGroupUid) {
		super(screenManager, parentScreen, "");

		mItems = new ArrayList<>();
		mSelectedIndex = 0;
		mEntityGroupUid = entityGroupUid;
		mLeftButtonRectangle = new Rectangle(0, 0, 25, 25);
		mRightButtonRectangle = new Rectangle(0, 0, 25, 25);

		mSpriteSheetDefinitionName = spritesheetName;

		mHighlightOnHover = false;
		mDrawBackground = false;

		mEnableScaleTextToWidth = true;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		if (mSpriteSheetDefinitionName == null || mSpriteSheetDefinitionName.length() == 0) {
			mImageSpriteSheetDefinition = resourceManager.spriteSheetManager().coreSpritesheet();
		} else {

			mImageSpriteSheetDefinition = resourceManager.spriteSheetManager().getSpriteSheet(mSpriteSheetDefinitionName, mEntityGroupUid);

			if (mImageSpriteSheetDefinition == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "MenuEnumEntryImageIndxed couldn't load requested SpriteSheetDefinition: " + mSpriteSheetDefinitionName);
				mImageSpriteSheetDefinition = resourceManager.spriteSheetManager().coreSpritesheet();
			}
		}
	}

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mEnableUpdateDraw)
			return false;

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = true;
			core.input().mouse().isMouseMenuSelectionEnabled(true);

			if (mToolTipEnabled)
				mToolTipTimer += core.appTime().elapsedTimeMilli();

			if (mEnabled && !mHasFocus)
				mParentScreen.setFocusOnEntry(this);

			if (mEnabled && core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mLeftButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

					return true;
				}

				if (mRightButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

					return true;
				}

				onClick(core.input());
				return true;
			}

		} else {
			mIsMouseOver = false;
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		boolean b = false;
		if (mEnableUpdateDraw == b)
			return;

		super.update(core, screen);

		final var lScreenOffsetX = screen.screenPositionOffset().x;
		final var lScreenOffsetY = screen.screenPositionOffset().y;

		mLeftButtonRectangle.x(lScreenOffsetX + mX + mW / 2 + 16);
		mLeftButtonRectangle.y(lScreenOffsetY + mY);
		mLeftButtonRectangle.height(mH);

		mRightButtonRectangle.x(lScreenOffsetX + mX + mW - 32);
		mRightButtonRectangle.y(lScreenOffsetY + mY);
		mRightButtonRectangle.height(mH);
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float componentDepth) {
		if (!mEnableUpdateDraw)
			return;

		super.draw(core, screen, componentDepth);

		final var lScreenOffsetX = screen.screenPositionOffset().x;
		final var lScreenOffsetY = screen.screenPositionOffset().y;

		mZ = componentDepth;

		final var lTextureBatch = mParentScreen.spriteBatch();
		final float lUiTextScale = mParentScreen.uiTextScale();

		lTextureBatch.begin(core.HUD());

		textColor.a = mParentScreen.screenColor.a;
		entryColor.a = mParentScreen.screenColor.a;

		if (mButtonsEnabled) {
			final var lButtonSize = mH;

			lTextureBatch.setColorRGBA(1.f, 1.f, 1.f, (mEnabled ? 1.f : 0.5f) * mParentScreen.screenColor.a);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, mLeftButtonRectangle.x(), mLeftButtonRectangle.y(), lButtonSize, lButtonSize, mZ);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, mRightButtonRectangle.x(), mRightButtonRectangle.y(), lButtonSize, lButtonSize, mZ);
		}

		final var lTextBoldFont = mParentScreen.fontBold();

		final var lLabelWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lLabelWidth && lLabelWidth > 0)
			lAdjustedLabelScaleW = (mW * 0.4f) / lLabelWidth;

		final var lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.setTextColor(textColor);
		lTextBoldFont.drawText(mLabel, lScreenOffsetX + mX + mW / 2 - 10 - (lLabelWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, lScreenOffsetY + mY + mH / 2 - lFontHeight * 0.5f, mZ, lAdjustedLabelScaleW, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffsetX + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffsetY + mY + mH / 2 - lFontHeight * 0.5f, mZ, lUiTextScale, -1);

		final var lTileSize = mH - 2.f;
		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			final var lSelectedItem = mItems.get(mSelectedIndex);
			final var lSpriteFrameName = lSelectedItem.spriteFrameName;
			final var lSpriteFrame = mImageSpriteSheetDefinition.getSpriteFrame(lSpriteFrameName);

			lTextureBatch.setColor(lSelectedItem.color);
			lTextureBatch.draw(mImageSpriteSheetDefinition, lSpriteFrame, lScreenOffsetX + mX + (mW / 6 * 4.65f) - lTileSize / 2, lScreenOffsetY + mY + mH / 2 - lTileSize / 2, lTileSize, lTileSize, mZ);
		}

		lTextBoldFont.end();
		lTextureBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mHasFocus = !mHasFocus;

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);

		mSelectedIndex++;
		if (mSelectedIndex >= mItems.size()) {
			mSelectedIndex = 0;
		}

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);

	}

	public void addItem(MenuEnumEntryItem item) {
		if (item == null)
			return;

		if (!mItems.contains(item)) {
			mItems.add(item);
		}
	}

	public void addItems(MenuEnumEntryItem[] items) {
		if (items == null)
			return;

		int pSize = items.length;
		for (int i = 0; i < pSize; i++) {
			if (!mItems.contains(items[i])) {
				mItems.add(items[i]);
			}
		}
	}

	public void clearItems() {
		mItems.clear();
	}

}
