package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public abstract class MenuListBoxItem extends Rectangle {

	private static final long serialVersionUID = -1093948958243532531L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected MenuListBox mParentListBox;
	public final Color textColor = new Color();
	public final Color entryColor = new Color();
	protected int mEntityGroupID;
	protected float mDoubleClickTimer;
	protected int mDoubleClickLogicalCounter;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	protected MenuListBoxItem(ScreenManager screenManager, MenuListBox parentListBox, int entityGroupUid) {
		mScreenManager = screenManager;
		mParentListBox = parentListBox;

		mEntityGroupID = entityGroupUid;

		mW = 600;
		mH = 64;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public boolean handleInput(LintfordCore core) {
		final var lMouseMenuSpace = core.HUD().getMouseCameraSpace();

		final var intersectsUs = intersectsAA(lMouseMenuSpace);
		final var areWeFreeToUseMouse = core.input().mouse().isMouseOverThisComponent(hashCode());
		final var canWeAcquireLeftMouse = intersectsUs && core.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (canWeAcquireLeftMouse && mDoubleClickLogicalCounter == -1) {
			mDoubleClickLogicalCounter = core.input().mouse().mouseLeftButtonLogicalTimer();
			mDoubleClickTimer = 200; // 200 ms to double click
		}

		if (intersectsUs && areWeFreeToUseMouse && canWeAcquireLeftMouse) {
			mParentListBox.selectedItem(this);

			return true;
		}

		if (intersectsUs && mDoubleClickLogicalCounter != -1) {
			mDoubleClickTimer -= core.appTime().elapsedTimeMilli();

			if (mDoubleClickTimer < 0) {
				mDoubleClickLogicalCounter = -1;
				mDoubleClickTimer = 0.f;
			} else if (mDoubleClickLogicalCounter != core.input().mouse().mouseLeftButtonLogicalTimer()) {
				mDoubleClickLogicalCounter = core.input().mouse().mouseLeftButtonLogicalTimer();
				mParentListBox.itemDoubleClicked(this);

				return true;
			}

		} else {
			mDoubleClickLogicalCounter = -1;
			mDoubleClickTimer = 0.f;
		}

		return false;
	}

	public void update(LintfordCore core, MenuScreen screen) {
	}

	public abstract void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth, boolean isSelected, boolean isHighlighted);

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void renderHighlight(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, float zDepth) {
		final var lScreenOffset = screen.screenPositionOffset();

		spriteBatch.setColorWhite();

		final var spriteFrameTL = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_CORNER_TL);
		final var spriteFrameTR = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_CORNER_TR);
		final var spriteFrameBL = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_CORNER_BL);
		final var spriteFrameBR = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_CORNER_BR);

		final var highlightOverlapPx = 2;
		final var dimension = 32.f;
		spriteBatch.draw(coreSpritesheet, spriteFrameTL, lScreenOffset.x + centerX() - mW / 2 - highlightOverlapPx, lScreenOffset.y + centerY() - mH / 2 - highlightOverlapPx, dimension, dimension, zDepth);
		spriteBatch.draw(coreSpritesheet, spriteFrameTR, lScreenOffset.x + centerX() + mW / 2 - dimension + highlightOverlapPx, lScreenOffset.y + centerY() - mH / 2 - highlightOverlapPx, dimension, dimension, zDepth);

		spriteBatch.draw(coreSpritesheet, spriteFrameBL, lScreenOffset.x + centerX() - mW / 2 - highlightOverlapPx, lScreenOffset.y + centerY() + mH / 2 - dimension + highlightOverlapPx, dimension, dimension, zDepth);
		spriteBatch.draw(coreSpritesheet, spriteFrameBR, lScreenOffset.x + centerX() + mW / 2 - dimension + highlightOverlapPx, lScreenOffset.y + centerY() + mH / 2 - dimension + highlightOverlapPx, dimension, dimension, zDepth);

	}
}
