package microgram.impl.srv.rest.replication;

import microgram.api.java.Result;

public interface MicrogramOperationExecutor {

	Result<?> execute( MicrogramOperation op );
	
}
