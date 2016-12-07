package net.ld.library.core.input;

public interface IBufferedInputCallback {

	public abstract void onEscapePressed();
	public abstract void onKeyPressed(char pCh);
	public abstract void onEnterPressed();
	
	public abstract StringBuilder getStringBuilder();
	
	public abstract boolean getEnterFinishesInput();
	public abstract boolean getEscapeFinishesInput();
	
}
