package de.zabuza.beedlebot.service.routine.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.chat.EChatType;
import de.zabuza.sparkle.freewar.chat.IChat;
import de.zabuza.sparkle.freewar.chat.Message;

/**
 * Task that when calling {@link #start()} checks if there was an item delivery
 * at the central traders depot since the last call of the method. Use
 * {@link #wasThereADelivery()} to check the resulting state. The task needs to
 * be used multiple times instead of throwing it away after one use in order to
 * maintain a history.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class WaitForDeliveryTask implements ITask {

	/**
	 * The size of the last message challenge. Only after reading and matching
	 * those messages in the chat next messages will be considered as new.
	 */
	private static final int LAST_MESSAGES_CHALLENGE_SIZE = 3;
	/**
	 * The content of a correct item delivery message.
	 */
	private static final String MESSAGE_DELIVERY_CONTENT = "Eine Händler-Karawane kommt aus dem Süden und liefert neue Waren an.";
	/**
	 * The chat to use for accessing message contents.
	 */
	private final IChat mChat;
	/**
	 * The message object that matches a correct item delivery message.
	 */
	private final Message mDeliveryMessage;
	/**
	 * Whether interrupted flag of the task is set.
	 */
	private boolean mInterrupted;
	/**
	 * The queue of last messages that are used in the challenge. Only after
	 * reading and matching those messages in the chat next messages will be
	 * considered as new.
	 */
	private final Queue<Message> mLastMessagesChallenge;
	/**
	 * The logger to use for logging.
	 */
	private final ILogger mLogger;
	/**
	 * Whether there was an item delivery at the central traders depot after the
	 * last call of {@link #start()}.
	 */
	private boolean mWasThereADelivery;

	/**
	 * Creates a new wait for delivery task that when calling {@link #start()}
	 * checks if there was an item delivery at the central traders depot since
	 * the last call of the method. Use {@link #wasThereADelivery()} to check
	 * the resulting state. The task needs to be used multiple times instead of
	 * throwing it away after one use in order to maintain a history.
	 * 
	 * @param chat
	 *            The chat to use for accessing message contents
	 */
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

	/**
	 * Whether there was an item delivery at the central traders depot since the
	 * last call of {@link #start()}.
	 * 
	 * @return <tt>True</tt> if there was an item delivery, <tt>false</tt>
	 *         otherwise
	 */
	public boolean wasThereADelivery() {
		return this.mWasThereADelivery;
	}

}
