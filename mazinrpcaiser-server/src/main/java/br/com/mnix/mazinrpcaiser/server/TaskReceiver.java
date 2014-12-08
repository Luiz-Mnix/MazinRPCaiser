package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import com.hazelcast.core.HazelcastInstanceNotActiveException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.*;

import static br.com.mnix.mazinrpcaiser.server.TaskUtils.returnToRequester;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class TaskReceiver implements Runnable {
	public static final int CONCURRENT_THREADS = 20;
	public static final int MAXIMUM_CONCURRENT_THREADS = 100;

	@Nonnull private static final Map<Integer, TaskReceiver> sRunningReceivers = new WeakHashMap<>();

	@Nonnull
	public static TaskReceiver setUpTaskReceiver(@Nonnull IDataGrid dataGrid) {
		return setUpTaskReceiver(dataGrid, CONCURRENT_THREADS, MAXIMUM_CONCURRENT_THREADS);
	}

	@Nonnull
	public static synchronized TaskReceiver setUpTaskReceiver(@Nonnull IDataGrid dataGrid, int concurrentProcessors,
															  int maximumProcessors) {
		int gridHash = dataGrid.hashCode();

		if(!sRunningReceivers.containsKey(gridHash)) {
			TaskReceiver receiver = new TaskReceiver(dataGrid, concurrentProcessors, maximumProcessors);
			sRunningReceivers.put(dataGrid.hashCode(), receiver);
			receiver.start();
			return receiver;

		} else {
			return sRunningReceivers.get(gridHash);
		}
	}

	@Nonnull private final IDataGrid mDataGrid;
	@Nonnull private final ThreadPoolExecutor mThreadPool;
	@Nullable private Thread mRunningThread = null;

	private TaskReceiver(@Nonnull IDataGrid dataGrid, int concurrentProcessors, int maximumProcessors) {
		mDataGrid = dataGrid;
		mThreadPool = new ThreadPoolExecutor(
				concurrentProcessors,
				maximumProcessors,
				1,
				TimeUnit.DAYS,
				new ArrayBlockingQueue<Runnable>(maximumProcessors)
		);
	}

	public String getThreadName() {
		return toString() + "_" + mDataGrid.toString();
	}

	public synchronized void start() {
		if(mRunningThread == null) {
			mRunningThread = new Thread(this, getThreadName());
			mRunningThread.start();
		} else {
			throw new IllegalStateException("TaskReceiver is already running.");
		}
	}

	public synchronized void finish() {
		if(mRunningThread != null) {
			synchronized (sRunningReceivers) {
				sRunningReceivers.remove(mDataGrid.hashCode());
			}

			Thread runningThread = mRunningThread;
			mRunningThread = null;
			runningThread.interrupt();
		} else {
			throw new IllegalStateException("TaskReceiver is not running, so it can't be stopped.");
		}
	}

	@Override
	public void run() {
		BlockingQueue<RequestEnvelope> commands;

		try {
			commands = mDataGrid.getCommandQueue(MazinRPCaiserConstants.COMMAND_QUEUE_ID);
		} catch(Exception ignored) {
			finish();
			return;
		}

		while (mDataGrid.isOn()) {
			RequestEnvelope requestEnvelope;

			try {
				requestEnvelope = commands.take();
			} catch (InterruptedException ignored) {
				continue;
			} catch (HazelcastInstanceNotActiveException ignored) {
				break;
			}

			TaskProcessor processor = new TaskProcessor(requestEnvelope, mDataGrid);

			try {
				mThreadPool.execute(processor);
			} catch (RejectedExecutionException e) {
				returnToRequester(
						mDataGrid, requestEnvelope.getTopicId(), requestEnvelope.getSessionData(), null, e
				);
			}
		}

		finish();
	}
}
