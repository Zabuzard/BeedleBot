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
		this.mChat = chat;
		this.mWasThereADelivery = false;
		this.mLastMessagesChallenge = new LinkedList<>();
		this.mDeliveryMessage = new Message(MESSAGE_DELIVERY_CONTENT, EChatType.DIRECT);
		this.mLogger = LoggerFactory.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#interrupt()
	 */
	@Override
	public void interrupt() {
		this.mInterrupted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#isInterrupted()
	 */
	@Override
	public boolean isInterrupted() {
		return this.mInterrupted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.beedlebot.service.routine.tasks.ITask#start()
	 */
	@Override
	public void start() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Starting WaitForDeliveryTask");
		}

		this.mWasThereADelivery = false;
		final ArrayList<Message> messages = this.mChat.getMessages();

		boolean succeededLastMessagesChallenge = false;
		boolean reachedMessageFromChallenge = true;
		Message messageFromChallenge = null;
		for (final Message message : messages) {
			if (!succeededLastMessagesChallenge && reachedMessageFromChallenge) {
				if (this.mLastMessagesChallenge.isEmpty()) {
					// Challenge succeeded
					succeededLastMessagesChallenge = true;
				} else {
					// Poll the next challenge
					messageFromChallenge = this.mLastMessagesChallenge.poll();
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
				if (message.equals(this.mDeliveryMessage)) {
					this.mWasThereADelivery = true;
					break;
				}
			}
		}

		// Update the last messages challenge
		this.mLastMessagesChallenge.clear();
		final int lastIndex = messages.size() - 1;
		final int challengeSize = Math.min(lastIndex + 1, LAST_MESSAGES_CHALLENGE_SIZE);
		for (int i = challengeSize - 1; i >= 0; i--) {
			this.mLastMessagesChallenge.add(messages.get(lastIndex - i));
		}
	}

	public boolean wasThereADelivery() {
		return this.mWasThereADelivery;
	}

}
