package net.lintford.library.controllers.messages;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.messaging.IMessageProvider;
import net.lintford.library.core.messaging.MessageManager;

public class MessageConsoleController extends BaseController implements IMessageController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Message Console Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private IMessageProvider mMessagesProvider;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public IMessageProvider messageProvider() {
		return mMessagesProvider;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MessageConsoleController(ControllerManager controllerManager, String controllerName, IMessageProvider iMessageProvider, int entityGroupUid) {
		super(controllerManager, controllerName, entityGroupUid);

		mMessagesProvider = iMessageProvider;
	}

	public MessageConsoleController(ControllerManager controllerManager, IMessageProvider iMessageProvider, int entityGroupUid) {
		this(controllerManager, CONTROLLER_NAME, iMessageProvider, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unload() {

	}

	@Override
	public void addMessage(String tag, String message) {
		addMessage(tag, message, -1);
	}

	@Override
	public void addMessage(String tag, String message, final int characterWrapAmount) {
		final var lMessage = mMessagesProvider.getMessageInstance();
		lMessage.setMessage(tag, message, MessageManager.timeStamp(), 0);
		mMessagesProvider.addMesage(lMessage);
	}
}