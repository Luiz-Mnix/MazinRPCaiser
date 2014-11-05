package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;

import static org.apache.commons.lang3.Validate.*;

import static org.junit.Assert.*;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class SessionTest {
	@Before
	public void Setup() {
		HazelcastUtils.raiseHazelcast();
	}

	@After
	public void Shutdown() {
		HazelcastUtils.shutdowHazelcast();
	}

	@Test
	public void testOpen_BasicWorkflow_ShouldWorkJustFine() throws Exception {
		// Arrange
		final Session session = new Session("127.0.0.1");
		final Runnable openHandler = new Runnable() {
			@Override
			public void run() {
				BlockingQueue queue = HazelcastUtils.getQueue(ActionDataUtils.getActionType(OpenSessionData.class));
				try {
					InputAction action = (InputAction) queue.take();
					HazelcastUtils.postMessage(
							action.getTopicId(),
							new OutputAction(action.getTopicId(), action.getSessionMetadata(), null, null)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		final Runnable closeHandler = new Runnable() {
			@Override
			public void run() {
				BlockingQueue queue = HazelcastUtils.getQueue(ActionDataUtils.getActionType(CloseSessionData.class));
				try {
					InputAction action = (InputAction) queue.take();
					HazelcastUtils.postMessage(
							action.getTopicId(),
							new OutputAction(action.getTopicId(), action.getSessionMetadata(), null, null)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};

		// Act
		new Thread(openHandler).start();
		new Thread(closeHandler).start();

		// Assert
		assertFalse(session.isOpened());
		session.open(false);
		assertTrue(session.isOpened());
		session.invalidate();
		assertFalse(session.isOpened());
	}
}
