package net.lintford.library.core.messaging;

import java.util.List;

public interface IMessageProvider {

	public List<Message> messages();

	public void addMesage(Message pMessage);

	public Message getMessageInstance();

	public void returnMessageInstance(Message pReturnInstance);

}
