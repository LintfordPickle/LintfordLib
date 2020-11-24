package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

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

	public UIRadioGroup(final UiWindow pUIWindow) {
		super(pUIWindow);

		mButtons = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			final int lButtonCount = mButtons.size();
			for (int i = 0; i < lButtonCount; i++) {
				if (mButtons.get(i).handleInput(pCore)) {

					return true;
				}

			}

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		updateLayout();

		float lYPos = y + mParentWindow.getTitleBarHeight();

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			final var lButton = mButtons.get(i);
			lButton.setPosition(x, lYPos);
			lButton.w(50);

			lButton.update(pCore);

			lYPos += 35;

		}

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			mButtons.get(i).draw(pCore, pTextureBatch, pUITexture, pTextFont, pComponentZDepth);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addButton(final UIRadioButton pButton) {
		if (!mButtons.contains(pButton)) {
			pButton.setClickListener(this);

			mButtons.add(pButton);

		}

	}

	public void removeButton(final UIRadioButton pButton) {
		if (mButtons.contains(pButton)) {
			pButton.removeClickListener(this);

			mButtons.remove(pButton);

		}

	}

	public void updateLayout() {

	}

	@Override
	public void menuEntryOnClick(InputManager pInputState, int pEntryID) {
		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			if (mButtons.get(i).buttonListenerID() == pEntryID) {
				// Was clicked
				mButtons.get(i).isSelected(true);

			}

			else {
				// was not clicked
				mButtons.get(i).isSelected(false);

			}

		}

		if (mCallback != null) {
			mCallback.menuEntryOnClick(pInputState, pEntryID);

		}

	}

	public void setClickListener(final EntryInteractions pCallbackObject) {
		mCallback = pCallbackObject;
	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

	@Override
	public boolean isActionConsumed() {
		return false;
	}

}
