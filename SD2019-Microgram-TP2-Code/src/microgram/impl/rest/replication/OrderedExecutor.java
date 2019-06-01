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


	final List<ReadMicrogramOperation> readWaitingList;
	MicrogramOperationExecutor executor;
	Long version = 0L;

	public OrderedExecutor(MicrogramTopic topic, int partitions) {
		this.topic = topic;
		this.kafka = new KafkaClient();
		kafka.createTopic(topic, partitions);
		this.queues = new ConcurrentHashMap<>();

		this.readWaitingList = new LinkedList<>();

	}

	public OrderedExecutor init(MicrogramOperationExecutor executor) {
		Version.set(0L);
		this.executor = executor;
		kafka.subscribe((t, k, v, ko) -> {
			System.err.printf("%s %s %s - %d\n", k, v, ko, System.currentTimeMillis());
			
			MicrogramOperation op = new MicrogramOperation(v);

			processOperation(op);
			System.out.println("Reading Order " + ko.offset);
			version = ko.offset;

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
			final ReadMicrogramOperation op = operations.next();
			op.setCurrentOrder(Version.getOrElse(0L,Long.class));
			if(op.isReady()){
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

				kafka.publish(topic, DEFAULT_KEY, op.encode());

			setVersion();
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

		op.setInvocation(Version.getOrElse(0L,Long.class));
		op.setCurrentOrder(version);

		if(op.isReady()) {
			System.out.println("READY");
			return (Result<T>) executor.execute(op);
		}else
			addToReadWaitingList(op);

		setVersion();
		return (Result<T>) Queues.takeFrom(q);
	}

	private void addToReadWaitingList(ReadMicrogramOperation op){
		synchronized (readWaitingList) {
			readWaitingList.add(op);
		}
	}

	private void setVersion(){
		Version.set(version);
	}

}
