package microgram.impl.rest.replication;

import microgram.impl.rest.replication.MicrogramOperation;
import microgram.impl.rest.replication.kafka.KafkaOrder;

import java.util.concurrent.atomic.AtomicLong;

public class ReadMicrogramOperation extends MicrogramOperation {

    public Long invocationOrder;
    public Long currentOrder;

    public ReadMicrogramOperation(Operation type, Object args){
        super(type, args);

    }
    public ReadMicrogramOperation(String encoding){
        super(encoding);
    }

    public void setInvocation(Long order) {
        this.invocationOrder = order;
    }

    public void setCurrentOrder(Long order){
        this.currentOrder = order;
    }

    public boolean isReady(){
        if(invocationOrder == null || currentOrder == null)
            return false;

        return (invocationOrder - currentOrder) <= 0;
    }


}
