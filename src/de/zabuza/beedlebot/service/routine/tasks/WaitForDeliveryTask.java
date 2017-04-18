package de.zabuza.beedlebot.service.routine.tasks;

import java.util.ArrayList;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.chat.EChatType;
import de.zabuza.sparkle.freewar.chat.IChat;
import de.zabuza.sparkle.freewar.chat.Message;

public final class WaitForDeliveryTask implements ITask {

	private static final String MESSAGE_DELIVERY_CONTENT = "Eine Händler-Karawane kommt aus dem Süden und liefert neue Waren an.";
	private IChat mChat;
	private Message mDeliveryMessage;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private Message mLastMessage;
	private ILogger mLogger;
	private Message mNextToLastMessage;
	private boolean mWasThereADelivery;

	public WaitForDeliveryTask(final IChat chat) {
		mChat = chat;
		mWasThereADelivery = false;
		mLastMessage = null;
		mNextToLastMessage = null;
		mDeliveryMessage = new Message(MESSAGE_DELIVERY_CONTENT, EChatType.DIRECT);
		mLogger = LoggerFactory.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#interrupt()
	 */
	@Override
	public void interrupt() {
		mInterrupted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#isInterrupted()
	 */
	@Override
	public boolean isInterrupted() {
		return mInterrupted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#start()
	 */
	@Override
	public void start() {
		if (mLogger.isDebugEnabled()) {
			mLogger.logDebug("Starting WaitForDeliveryTask");
		}

		mWasThereADelivery = false;
		final ArrayList<Message> messages = mChat.getMessages();

		// All messages are unknown if there is no last message
		boolean reachedLastMessages = mLastMessage == null;
		boolean reachedNextToLastMessage = mNextToLastMessage == null;
		for (final Message message : messages) {
			// Skip already known messages by using a two message challenge
			if (!reachedLastMessages) {
				if (reachedNextToLastMessage) {
					if (message.equals(mLastMessage)) {
						// Challenge succeeded
						reachedLastMessages = true;
						continue;
					} else if (mNextToLastMessage != null) {
						// Only use challenge if a next to last message is known
						reachedNextToLastMessage = false;
					}
				}
				if (!reachedNextToLastMessage) {
					if (message.equals(mNextToLastMessage)) {
						reachedNextToLastMessage = true;
						continue;
					}
				}
			}

			// Check message against the delivery message
			if (reachedLastMessages && message.equals(mDeliveryMessage)) {
				mWasThereADelivery = true;
				break;
			}
		}

		// Update the last known messages
		final int nextToLastIndex = messages.size() - 2;
		final int lastIndex = nextToLastIndex + 1;
		if (nextToLastIndex >= 0) {
			mNextToLastMessage = messages.get(nextToLastIndex);
		} else {
			mNextToLastMessage = null;
		}
		if (lastIndex >= 0) {
			mLastMessage = messages.get(lastIndex);
		} else {
			mLastMessage = null;
		}
	}

	public boolean wasThereADelivery() {
		return mWasThereADelivery;
	}

}
