package br.com.mnix.mazinrpcaiser.client;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class HazelcastUtils {
	private static HazelcastInstance mHazelcastConnection = null;
	private Map<String, Semaphore> mSemaphores = new HashMap<>();

	public static void raiseHazelcast() {
		if(mHazelcastConnection == null) {
			Config config = new Config();
			config.getNetworkConfig().getInterfaces().addInterface("127.0.0.1");
			mHazelcastConnection = Hazelcast.newHazelcastInstance(config);
		}
	}

	public static <T extends Serializable> BlockingQueue<T> getQueue(String id) {
		if(mHazelcastConnection != null) {
			return mHazelcastConnection.getQueue(id);
		}

		return null;
	}

	public static void shutdowHazelcast() {
		if(mHazelcastConnection != null) {
//			mHazelcastConnection.shutdown();
		}
	}

	public static String addListener(String topicId, MessageListener listener) {
		if(mHazelcastConnection != null) {
			return mHazelcastConnection.getTopic(topicId).addMessageListener(listener);
		}

		return null;
	}

	public static boolean removeListener(String topicId, String listenerId) {
		if(mHazelcastConnection != null) {
			return mHazelcastConnection.getTopic(topicId).removeMessageListener(listenerId);
		}
		return false;
	}

	public static void postMessage(String topicId, Serializable data) {
		if(mHazelcastConnection != null) {
			mHazelcastConnection.getTopic(topicId).publish(data);
		}
	}
}
