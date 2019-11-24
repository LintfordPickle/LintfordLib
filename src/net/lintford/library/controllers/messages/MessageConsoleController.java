package net.lintford.library.controllers.messages;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.messaging.IMessageProvider;
import net.lintford.library.core.messaging.MessageManager;

public class MessageConsoleController extends BaseController implements IMessageController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "MessageConsoleController";

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

	@Override
	public boolean isinitialized() {
		return mMessagesProvider != null;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MessageConsoleController(ControllerManager pControllerManager, String pControllerName, IMessageProvider pIMessageProvider, int pEntityGroupID) {
		super(pControllerManager, pControllerName, pEntityGroupID);

		mMessagesProvider = pIMessageProvider;
	}

	public MessageConsoleController(ControllerManager pControllerManager, IMessageProvider pIMessageProvider, int pEntityGroupID) {
		this(pControllerManager, CONTROLLER_NAME, pIMessageProvider, pEntityGroupID);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
	}

	@Override
	public void unload() {

	}

	@Override
	public void addMessage(String pTag, String pMessage) {
		addMessage(pTag, pMessage, -1);
	}

	@Override
	public void addMessage(String pTag, String pMessage, final int pCharacterWrapAmount) {
		final var lMessage = mMessagesProvider.getMessageInstance();
		lMessage.setMessage(pTag, pMessage, MessageManager.timeStamp(), 0);
		mMessagesProvider.addMesage(lMessage);

	}

}
