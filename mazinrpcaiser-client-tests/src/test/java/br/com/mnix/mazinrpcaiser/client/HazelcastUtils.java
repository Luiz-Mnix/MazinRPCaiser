package br.com.mnix.mazinrpcaiser.client;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public final class HazelcastUtils {
	private static HazelcastInstance sHazelcastConnection = null;

	@SuppressWarnings("NonThreadSafeLazyInitialization")
	public static void raiseHazelcast() {
		if(sHazelcastConnection == null) {
			Config config = new Config();
			config.getNetworkConfig().getInterfaces().addInterface("127.0.0.1");
			sHazelcastConnection = Hazelcast.newHazelcastInstance(config);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public static <T extends Serializable> BlockingQueue<T> getQueue(String id) {
		if(sHazelcastConnection != null) {
			return sHazelcastConnection.getQueue(id);
		}

		return null;
	}

	public static void shutdownHazelcast() {
//		if(sHazelcastConnection != null) {
//			sHazelcastConnection.shutdown();
//		}
	}

// --Commented out by Inspection START (11/5/14, 1:53 PM):
//	public static String addListener(String topicId, MessageListener listener) {
//		if(sHazelcastConnection != null) {
//			return sHazelcastConnection.getTopic(topicId).addMessageListener(listener);
//		}
//
//		return null;
//	}
// --Commented out by Inspection STOP (11/5/14, 1:53 PM)

	public static boolean removeListener(String topicId, String listenerId) {
		return sHazelcastConnection != null && sHazelcastConnection.getTopic(topicId).removeMessageListener(listenerId);
	}

	public static void postMessage(String topicId, Serializable data) {
		if(sHazelcastConnection != null) {
			sHazelcastConnection.getTopic(topicId).publish(data);
		}
	}
}
