package microgram.impl.rest.replication;

import microgram.impl.rest.replication.MicrogramOperation;

import java.util.concurrent.atomic.AtomicLong;

public class ReadMicrogramOperation extends MicrogramOperation {

    public AtomicLong offSet;

    public ReadMicrogramOperation(Operation type, Object args){
        super(type, args);
        this.offSet = new AtomicLong(0);

    }
    public ReadMicrogramOperation(String encoding){
        super(encoding);
        this.offSet = new AtomicLong(0);
    }



}
