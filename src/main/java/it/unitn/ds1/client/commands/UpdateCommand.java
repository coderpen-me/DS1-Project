package it.unitn.ds1.client.commands;

import akka.actor.ActorSelection;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import it.unitn.ds1.messages.client.ClientOperationErrorResponse;
import it.unitn.ds1.messages.client.ClientUpdateRequest;
import it.unitn.ds1.messages.client.ClientUpdateResponse;
import org.jetbrains.annotations.NotNull;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

import static it.unitn.ds1.SystemConstants.CLIENT_TIMEOUT_SECONDS;

/**
 * Command used to update a record in the system.
 */
public final class UpdateCommand implements Command {

	// internal variables
	private final int key;
	private final String value;

	/**
	 * Create a new command to update the value of a given key.
	 *
	 * @param key   Key to update.
	 * @param value New value for the key.
	 */
	public UpdateCommand(int key, String value) {
		this.key = key;
		this.value = value;
	}

	@NotNull
	@Override
	public CommandResult run(ActorSelection actor, String remote, LoggingAdapter logger) throws Exception {
		logger.info("[CLIENT] Update key [{}] with value \"{}\" on node [{}]...", key, value, remote);

		// send the command to the actor
		final Timeout timeout = new Timeout(CLIENT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		final Future<Object> future = Patterns.ask(actor, new ClientUpdateRequest(key, value), timeout);

		// wait for an acknowledgement
		final Object message = Await.result(future, timeout.duration());
		assert message instanceof ClientUpdateResponse || message instanceof ClientOperationErrorResponse;

		// an error has occurred
		if (message instanceof ClientOperationErrorResponse) {

			// log the cause of the error
			final ClientOperationErrorResponse result = (ClientOperationErrorResponse) message;
			logger.error("Actor [{}] replies... update operation has failed. Reason: \"{}\"",
				result.getSenderID(), result.getMessage());

			// something went wrong... maybe the quorum was not reached
			return new CommandResult(false, null);
		}

		// success
		else {

			// log the result
			final ClientUpdateResponse result = (ClientUpdateResponse) message;
			logger.info("[CLIENT] Actor [{}] replies: key [{}] has been updated (value: \"{}\", version: {})",
				result.getSenderID(), result.getKey(), result.getVersionedItem().getValue(), result.getVersionedItem().getVersion());

			// update was successful
			return new CommandResult(true, result.getVersionedItem());
		}
	}

}
