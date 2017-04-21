package de.zabuza.beedlebot.service.routine.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.chat.EChatType;
import de.zabuza.sparkle.freewar.chat.IChat;
import de.zabuza.sparkle.freewar.chat.Message;

public final class WaitForDeliveryTask implements ITask {

	private static final int LAST_MESSAGES_CHALLENGE_SIZE = 3;
	private static final String MESSAGE_DELIVERY_CONTENT = "Eine Händler-Karawane kommt aus dem Süden und liefert neue Waren an.";
	private IChat mChat;
	private Message mDeliveryMessage;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	private final Queue<Message> mLastMessagesChallenge;
	private ILogger mLogger;
	private boolean mWasThereADelivery;

	public WaitForDeliveryTask(final IChat chat) {
		mChat = chat;
		mWasThereADelivery = false;
		mLastMessagesChallenge = new LinkedList<>();
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

		boolean succeededLastMessagesChallenge = false;
		boolean reachedMessageFromChallenge = true;
		Message messageFromChallenge = null;
		for (final Message message : messages) {
			if (!succeededLastMessagesChallenge && reachedMessageFromChallenge) {
				if (mLastMessagesChallenge.isEmpty()) {
					// Challenge succeeded
					succeededLastMessagesChallenge = true;
				} else {
					// Poll the next challenge
					messageFromChallenge = mLastMessagesChallenge.poll();
					reachedMessageFromChallenge = false;
				}
			}

			if (!succeededLastMessagesChallenge) {
				if (messageFromChallenge == null) {
					throw new AssertionError();
				}

				// Try to match the challenge
				reachedMessageFromChallenge = message.equals(messageFromChallenge);
			} else {
				// Try to match the delivery message
				if (message.equals(mDeliveryMessage)) {
					mWasThereADelivery = true;
					break;
				}
			}
		}

		// Update the last messages challenge
		mLastMessagesChallenge.clear();
		final int lastIndex = messages.size() - 1;
		final int challengeSize = Math.min(lastIndex + 1, LAST_MESSAGES_CHALLENGE_SIZE);
		for (int i = challengeSize - 1; i >= 0; i--) {
			mLastMessagesChallenge.add(messages.get(lastIndex - i));
		}
	}

	public boolean wasThereADelivery() {
		return mWasThereADelivery;
	}

}
