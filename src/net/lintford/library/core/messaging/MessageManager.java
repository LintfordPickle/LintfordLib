package net.lintford.library.core.messaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.BaseData;

public class MessageManager extends BaseData implements IMessageProvider {

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

	public MessageManager(int pCapacity) {
		mCapacity = pCapacity;

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
	public void addMesage(Message pMessage) {
		if (pMessage.message.equals("")) {
			return;

		}

		// TODO: Make this optional: Make sure there are no special characters contained in the string
		pMessage.message = pMessage.message.replaceAll("[^a-zA-Z0-9\\s+]", "");

		// TODO: Make this optional: Remove new line and caridge return
		pMessage.message = pMessage.message.replaceAll("(\\r\\n|\\r|\\n)", " ");

		if (!mMessages.contains(pMessage)) {
			mMessages.add(pMessage);

		}

		if (mMirrorLogToConsole) {
			System.out.printf("[%s] %s: %s\n", padRight(pMessage.timestamp, 12), padRight(pMessage.tag, 25), pMessage.message);

		}

	}

	public static String timeStamp() {
		return TimeStampFormat.format(new Date());

	}

	public static String timeStampWithMilli() {
		return TimeStampFormatWithMilli.format(new Date());

	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);

	}

	@Override
	public Message getMessageInstance() {
		Message lLogMessage = null;
		if (mMessagesPool.size() > 0) {
			// Remove from the pool until empty
			lLogMessage = mMessagesPool.remove(0);

		} else {
			// Non free, get the first from the linked list
			lLogMessage = mMessages.remove(0);

		}

		if (lLogMessage == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to write to Debug log");
			return null;

		}

		return lLogMessage;

	}

	@Override
	public void returnMessageInstance(Message pReturnInstance) {
		if (pReturnInstance == null)
			return;

		if (mMessages.contains(pReturnInstance)) {
			mMessages.remove(pReturnInstance);

		}

		if (!mMessagesPool.contains(pReturnInstance)) {
			mMessagesPool.add(pReturnInstance);

		}

	}

}