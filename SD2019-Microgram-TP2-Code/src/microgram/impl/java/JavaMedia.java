package microgram.impl.java;

import static microgram.api.java.Result.ok;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import microgram.impl.dropbox.DropboxClient;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;
import utils.Log;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";


	final String baseUri;
	private DropboxClient client;

	public JavaMedia(String baseUri ) {
		this.baseUri = baseUri;
		initializeDropBox();
	}

	private void initializeDropBox() {
		try {
			client = DropboxClient.createClientWithAccessToken();
			client.createDirectory(ROOT_DIR);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	
	@Override
	public Result<String> upload(byte[] bytes) {
			String id = Hash.of(bytes);
			System.out.println("Uploading " + id);
			Result<String> result = client.upload(ROOT_DIR + id + MEDIA_EXTENSION,bytes);
			System.out.println(result);
			return result;
	}

	@Override
	public Result<byte[]> download(String id) {
		System.out.println("Downloading " + id);
		return client.download(id);
 	}

	@Override
	public  Result<Void> delete(String id) {
		System.out.println("Deleting" + id);
		return client.delete(id);
	}
	
}
