package net.lintford.library.controllers.messages;

public interface IMessageController {

	public void addMessage(String pTag, String pMessage);

	public void addMessage(String pTag, String pMessage, int pCharacterWrapAmount);

}
