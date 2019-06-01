package microgram.impl.rest.replication;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import microgram.api.java.Result;
import microgram.impl.rest.replication.kafka.KafkaClient;
import microgram.impl.rest.replication.kafka.KafkaOrder;
import utils.Queues;

public class OrderedExecutor {

	private static final String DEFAULT_KEY = "_";
	
	final KafkaClient kafka;
	final MicrogramTopic topic;
	final Map<Object, BlockingQueue<Result<?>>> queues;


	/**
	 * Offset of the last operation executed
	 */
	private KafkaOrder readOrder;
	private KafkaOrder publishOrder;

	final List<ReadMicrogramOperation> readWaitingList;
	MicrogramOperationExecutor executor;

	public OrderedExecutor(MicrogramTopic topic, int partitions) {
		this.topic = topic;
		this.kafka = new KafkaClient();
		kafka.createTopic(topic, partitions);
		this.queues = new ConcurrentHashMap<>();

		this.readWaitingList = new LinkedList<>();

	}

	public OrderedExecutor init(MicrogramOperationExecutor executor) {
		this.executor = executor;
		kafka.subscribe((t, k, v, ko) -> {
			System.err.printf("%s %s %s - %d\n", k, v, ko, System.currentTimeMillis());
			
			MicrogramOperation op = new MicrogramOperation(v);

			processOperation(op);
			this.readOrder = ko;

			decreaseOffset();
		}, topic);

		return this;
	}

	private void processOperation(MicrogramOperation op){
		Result<?> result = executor.execute(op);

		BlockingQueue<Result<?>> q = queues.remove(op.id);
		if (q != null)
			Queues.putInto(q, result);

	}

	private void decreaseOffset() {
		for (Iterator<ReadMicrogramOperation> operations = readWaitingList.iterator(); operations.hasNext();) {
			ReadMicrogramOperation op = operations.next();
			op.setCurrentOrder(readOrder.offset);
			if (op.isReady()) {
				processOperation(op);
				operations.remove();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Result<T> replicate(MicrogramOperation op) {
		System.out.println("Replicating " + op.type);
		try {
			BlockingQueue<Result<?>> q;
			queues.put(op.id, q = new SynchronousQueue<>());

				publishOrder = kafka.publish(topic, DEFAULT_KEY, op.encode());

			return (Result<T>) Queues.takeFrom(q);

		}
		catch(Exception e ){
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
		finally {

		}
	}

	@SuppressWarnings("unchecked")
	public <T> Result<T> queueForRead(ReadMicrogramOperation op) {
		BlockingQueue<Result<?>> q;
		queues.put(op.id, q = new SynchronousQueue<>());

		op.setInvocation(Long.parseLong(Version.jsonVersionIn.get()));
		op.setCurrentOrder(readOrder.offset);

		if(op.isReady())
			Queues.putInto(q, executor.execute(op));
		else
			addToReadWaitingList(op);

		return (Result<T>) Queues.takeFrom(q);
	}

	private void addToReadWaitingList(ReadMicrogramOperation op){
		synchronized (readWaitingList) {
			readWaitingList.add(op);
		}
	}

}
