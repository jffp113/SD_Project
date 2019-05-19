package microgram.impl.srv.rest.media;

import microgram.api.java.Media;
import microgram.api.rest.RestMediaStorage;
import microgram.impl.srv.java.JavaMedia;
import microgram.impl.srv.rest.RestResource;

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
		super.resultOrThrow(impl.delete(id));
	}
	
}
