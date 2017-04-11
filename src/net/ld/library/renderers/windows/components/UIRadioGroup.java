package net.ld.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.windows.UIWindow;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;

public class UIRadioGroup extends UIWidget implements IMenuEntryClickListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<UIRadioButton> mButtons;
	private IMenuEntryClickListener mCallback;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIRadioGroup(final UIWindow pUIWindow) {
		super(pUIWindow);

		mButtons = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(final InputState pInputState) {
		if (intersects(pInputState.HUD().getMouseCameraSpace())) {
			final int lButtonCount = mButtons.size();
			for (int i = 0; i < lButtonCount; i++) {
				if (mButtons.get(i).handleInput(pInputState)) {

					return true;
				}

			}

		}

		return false;
	}

	@Override
	public void update(final GameTime pGameTime) {
		super.update(pGameTime);

		updateLayout();

		float lYPos = y + mParentWindow.getTitleBarHeight() + UIWindow.WINDOW_CONTENT_PADDING_Y;

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			mButtons.get(i).x = x;
			mButtons.get(i).y = lYPos;
			mButtons.get(i).width = 50;

			lYPos += 35;

			mButtons.get(i).update(pGameTime);

		}

	}

	@Override
	public void draw(final RenderState pRenderState) {

		final int lButtonCount = mButtons.size();
		for (int i = 0; i < lButtonCount; i++) {
			mButtons.get(i).draw(pRenderState);

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
	public void onClick(final int pEntryID) {
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
			mCallback.onClick(pEntryID);

		}

	}

	public void setClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = pCallbackObject;
	}

	public void removeClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = null;
	}

}
