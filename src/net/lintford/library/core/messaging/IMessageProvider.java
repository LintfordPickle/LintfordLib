package net.lintford.library.core.messaging;

import java.util.List;

public interface IMessageProvider {

	public List<Message> messages();
	public void addMesage(String pMessage);
	public void addMesage(String pTag, String pMessage);
	
}
