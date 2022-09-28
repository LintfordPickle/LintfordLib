package net.lintford.library.core.messaging;

import java.util.List;

public interface IMessageProvider {

	public List<Message> messages();

	public void addMesage(Message message);

	public Message getMessageInstance();

	public void returnMessageInstance(Message returnInstance);

}
