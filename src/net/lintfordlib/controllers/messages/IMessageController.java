package net.lintfordlib.controllers.messages;

public interface IMessageController {

	public void addMessage(String tag, String message);

	public void addMessage(String tag, String message, int characterWrapAmount);

}
