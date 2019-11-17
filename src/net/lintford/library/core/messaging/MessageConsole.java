package net.lintford.library.core.messaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lintford.library.core.entity.BaseData;

public class MessageConsole extends BaseData implements IMessageProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8246052843503897030L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<Message> mMessages;
	protected transient List<Message> mMessagesPool;
	protected final int mCapacity;
	protected boolean mMirrorLogToConsole;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public List<Message> messages() {
		return mMessages;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MessageConsole() {
		this(100);

	}

	public MessageConsole(int pCapacity) {
		mCapacity = pCapacity;

		mMessages = new ArrayList<>(mCapacity);
		mMessagesPool = new ArrayList<>(mCapacity);

		mMirrorLogToConsole = true;

		for (int i = 0; i < mCapacity; i++) {
			mMessagesPool.add(new Message());

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void addMesage(String pMessage) {
		addMesage(getClass().getSimpleName(), pMessage);

	}

	@Override
	public void addMesage(String pTag, String pMessage) {
		if (pTag.equals("") || pMessage.equals("")) {
			return;

		}

		Message lLogMessage = null;
		if (mMessagesPool.size() > 0) {
			// Remove from the pool until empty
			lLogMessage = mMessagesPool.remove(0);

		} else {
			// Non free, get the first from the linked list
			lLogMessage = mMessages.remove(0);

		}

		if (lLogMessage == null) {
			System.err.println("DebugLogger: Unable to write to Debug log");
			return;
		}

		String lTimeStamp = new SimpleDateFormat("HH.mm.ss.SSS", Locale.US).format(new Date());
		lLogMessage.setMessage(pTag, pMessage, lTimeStamp, 0);

		mMessages.add(lLogMessage);

		if (mMirrorLogToConsole)
			System.out.printf("[%s] %s: %s\n", padRight(lLogMessage.timestamp, 12), padRight(lLogMessage.tag, 25), lLogMessage.message);

	}

	private static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);

	}

}