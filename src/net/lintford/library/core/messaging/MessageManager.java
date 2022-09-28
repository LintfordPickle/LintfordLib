package net.lintford.library.core.messaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.BaseInstanceData;

public class MessageManager extends BaseInstanceData implements IMessageProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8246052843503897030L;

	private static SimpleDateFormat TimeStampFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
	private static SimpleDateFormat TimeStampFormatWithMilli = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

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

	public MessageManager() {
		this(100);
	}

	public MessageManager(int capacity) {
		mCapacity = capacity;

		mMessages = new ArrayList<>(mCapacity);
		mMessagesPool = new ArrayList<>(mCapacity);

		mMirrorLogToConsole = false;

		for (int i = 0; i < mCapacity; i++) {
			mMessagesPool.add(new Message());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void addMesage(Message message) {
		if (message.message().equals("")) {
			return;
		}

		// TODO: Make this optional: Make sure there are no special characters contained in the string
		// pMessage.message = pMessage.message.replaceAll("[^a-zA-Z0-9\\s+]", "");

		// TODO: Make this optional: Remove new line and caridge return
		message.message(message.message().replaceAll("(\\r\\n|\\r|\\n)", " "));

		if (!mMessages.contains(message)) {
			mMessages.add(message);
		}

		if (mMirrorLogToConsole) {
			System.out.printf("[%s] %s: %s\n", padRight(message.timestamp(), 12), padRight(message.tag(), 25), message.message());
		}
	}

	public static String timeStamp() {
		return TimeStampFormat.format(new Date());
	}

	public static String timeStampWithMilli() {
		return TimeStampFormatWithMilli.format(new Date());
	}

	public static String padRight(String stringToPad, int amount) {
		return String.format("%1$-" + amount + "s", stringToPad);
	}

	@Override
	public Message getMessageInstance() {
		Message lLogMessage = null;
		if (mMessagesPool.size() > 0) {
			lLogMessage = mMessagesPool.remove(0);
		} else {
			lLogMessage = mMessages.remove(0);
		}

		if (lLogMessage == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to write to Debug log");
			return null;
		}

		return lLogMessage;
	}

	@Override
	public void returnMessageInstance(Message returnInstance) {
		if (returnInstance == null)
			return;

		if (mMessages.contains(returnInstance)) {
			mMessages.remove(returnInstance);
		}

		if (!mMessagesPool.contains(returnInstance)) {
			mMessagesPool.add(returnInstance);
		}
	}
}