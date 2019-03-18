package microgram.impl.srv.rest;

import org.apache.commons.lang3.NotImplementedException;

import microgram.api.java.Media;
import microgram.api.rest.RestMediaStorage;
import microgram.impl.srv.java.JavaMedia;

public class RestMediaResources extends RestResource implements RestMediaStorage {

	final Media impl;
	
	public RestMediaResources(String baseUri ) {
		this.impl = new JavaMedia( baseUri + RestMediaStorage.PATH );
	}
	
	@Override
	public String upload(byte[] bytes) {
		return super.resultOrThrow( impl.upload(bytes));
	}

	@Override
	public byte[] download(String id) {
		return super.resultOrThrow( impl.download(id));
 	}

	@Override
	public void delete(String id) {
		throw new NotImplementedException("Not implemented");
	}
	
}
