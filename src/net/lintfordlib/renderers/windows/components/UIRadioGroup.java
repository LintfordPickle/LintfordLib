package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

public class UIRadioGroup extends UIWidget implements EntryInteractions {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = -3191987692148151157L;

	private List<UIRadioButton> mButtons;
	private EntryInteractions mCallback;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIRadioGroup() {
		mButtons = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			final int lButtonCount = mButtons.size();
			for (int i = 0; i < lButtonCount; i++) {
				if (mButtons.get(i).handleInput(core)) {

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		updateLayout();

		float lYPos = mY;

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			final var lButton = mButtons.get(i);
			lButton.setPosition(mX, lYPos);
			lButton.width(50);

			lButton.update(core);

			lYPos += 35;
		}
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		textFont.begin(core.HUD());

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			mButtons.get(i).draw(core, sharedResources, coreSpritesheet, textFont, componentZDepth);
		}

		lSpriteBatch.end();
		textFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addButton(final UIRadioButton button) {
		if (!mButtons.contains(button)) {
			button.setClickListener(this);
			mButtons.add(button);
		}
	}

	public void removeButton(final UIRadioButton button) {
		if (mButtons.contains(button)) {
			button.removeClickListener(this);
			mButtons.remove(button);
		}
	}

	public void updateLayout() {

	}

	@Override
	public void menuEntryOnClick(InputManager inputState, int entryUid) {
		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			if (mButtons.get(i).buttonListenerID() == entryUid) {
				mButtons.get(i).isSelected(true);
			} else {
				mButtons.get(i).isSelected(false);
			}
		}

		if (mCallback != null) {
			mCallback.menuEntryOnClick(inputState, entryUid);
		}
	}

	public void setClickListener(final EntryInteractions callbackObject) {
		mCallback = callbackObject;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
		mCallback = null;
	}

	@Override
	public boolean isActionConsumed() {
		return false;
	}

}
