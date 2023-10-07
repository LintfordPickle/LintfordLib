package net.lintfordlib.screenmanager.dialogs;

public interface ITimedDialog {

	public abstract void timeExpired();

	public abstract void confirmation();

	public abstract void decline();

}
