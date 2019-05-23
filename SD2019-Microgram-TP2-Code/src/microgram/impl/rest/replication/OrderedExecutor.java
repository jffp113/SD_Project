package microgram.impl.rest.replication;

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

	AtomicLong currentOffset; //Implemented By me
	AtomicLong currentPointer;

	final List<ReadMicrogramOperation> readWaitingList;
	MicrogramOperationExecutor executor;

	public OrderedExecutor(MicrogramTopic topic, int partitions) {
		this.topic = topic;
		this.kafka = new KafkaClient();
		kafka.createTopic(topic, partitions);
		this.queues = new ConcurrentHashMap<>();

		this.currentOffset = new AtomicLong(0L);
		this.currentPointer = new AtomicLong(0L);
		this.readWaitingList = new LinkedList<>();
	}

	public OrderedExecutor init(MicrogramOperationExecutor executor) {
		this.executor = executor;
		kafka.subscribe((t, k, v, ko) -> {
			System.err.printf("%s %s %s - %d\n", k, v, ko, System.currentTimeMillis());
			
			MicrogramOperation op = new MicrogramOperation(v);

			Result<?> result = executor.execute(op);

			BlockingQueue<Result<?>> q = queues.remove(op.id);
			if (q != null)
				Queues.putInto(q, result);

			decreaseOffset();
		}, topic);

		return this;
	}

	private void decreaseOffset() {
		currentPointer.incrementAndGet();
		synchronized (readWaitingList) {
			for (ReadMicrogramOperation op : readWaitingList) {
				if(op.offSet.decrementAndGet() == 0){
					processReadOperation(op);
					readWaitingList.remove(op); //Remove is not syncronous
				}
			}
		}
	}

	private void processReadOperation(ReadMicrogramOperation op){

	}

	@SuppressWarnings("unchecked")
	public <T> Result<T> replicate(MicrogramOperation op) {
		KafkaOrder order;
		try {
			BlockingQueue<Result<?>> q;
			queues.put(op.id, q = new SynchronousQueue<>());

				order = kafka.publish(topic, DEFAULT_KEY, op.encode());
				currentOffset.set(order.offset);


			return (Result<T>) Queues.takeFrom(q);
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public <T> Result<T> queueForRead(ReadMicrogramOperation op) {

		BlockingQueue<Result<?>> q;
		queues.put(op.id, q = new SynchronousQueue<>());

		op.offSet.set(this.currentOffset.get() - this.currentPointer.get());

		if(op.offSet.get() == 0)
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
