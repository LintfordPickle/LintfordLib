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

	protected void renderHighlight(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, boolean renderFilled, float zDepth) {
		final var lScreenOffset = screen.screenPositionOffset();

		final var spriteFrameLT = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_TOP);
		final var spriteFrameLC = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_CENTER);
		final var spriteFrameLB = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_BOTTOM);

		final var spriteFrameRT = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_TOP);
		final var spriteFrameRC = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_CENTER);
		final var spriteFrameRB = coreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_BOTTOM);

		final var centerHeight = mH - 8 - 8;
		spriteBatch.draw(coreSpritesheet, spriteFrameLT, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 4, 8, zDepth);
		spriteBatch.draw(coreSpritesheet, spriteFrameLC, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2 + 8, 4, centerHeight, zDepth);
		spriteBatch.draw(coreSpritesheet, spriteFrameLB, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2 + centerHeight + 8, 4, 8, zDepth);

		if (renderFilled) {
			spriteBatch.draw(coreSpritesheet, spriteFrameRT, lScreenOffset.x + centerX() - mW / 2 + 4, lScreenOffset.y + centerY() - mH / 2, mW - 4, 8, zDepth);
			spriteBatch.draw(coreSpritesheet, spriteFrameRC, lScreenOffset.x + centerX() - mW / 2 + 4, lScreenOffset.y + centerY() - mH / 2 + 8, mW - 4, centerHeight, zDepth);
			spriteBatch.draw(coreSpritesheet, spriteFrameRB, lScreenOffset.x + centerX() - mW / 2 + 4, lScreenOffset.y + centerY() - mH / 2 + centerHeight + 8, mW - 4, 8, zDepth);
		}
	}
}
