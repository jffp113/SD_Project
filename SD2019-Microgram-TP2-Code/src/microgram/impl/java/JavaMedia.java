package microgram.impl.java;

import static microgram.api.java.Result.ok;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import microgram.impl.dropbox.DropboxClient;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";


	final String baseUri;
	private DropboxClient client;
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

	public JavaMedia(String baseUri ) {
		this.baseUri = baseUri;
		//new File( ROOT_DIR ).mkdirs();
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
			//client.upload(ROOT_DIR + id + MEDIA_EXTENSION,bytes);
			executor.execute(() -> client.upload(ROOT_DIR + id + MEDIA_EXTENSION,bytes));
			return ok(baseUri + "/" + id);

	}

	@Override
	public Result<byte[]> download(String id) {
			System.out.println(id);
			System.out.println( client.download(ROOT_DIR + id + MEDIA_EXTENSION).value().length);
			System.out.println(id);
		return client.download(ROOT_DIR + id + MEDIA_EXTENSION);
 	}

	@Override
	public  Result<Void> delete(String id) {
		return client.delete(ROOT_DIR + id + MEDIA_EXTENSION);
	}
	
}
