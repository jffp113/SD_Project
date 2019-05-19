package microgram.impl.srv.java;

import static microgram.api.java.Result.ok;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import microgram.impl.srv.dropbox.DropboxClient;
import kakfa.KafkaSubscriber;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";


	final String baseUri;
	private KafkaSubscriber subscriber;
	private DropboxClient client;
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

	public JavaMedia(String baseUri ) {
		this.baseUri = baseUri;
		//new File( ROOT_DIR ).mkdirs();
		initializeDropBox();
		initKafkaSubscriber();
	}

	private void initializeDropBox() {
		try {
			client = DropboxClient.createClientWithAccessToken();
			client.createDirectory(ROOT_DIR);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void initKafkaSubscriber() {
		subscriber = new KafkaSubscriber(Arrays.asList(JavaPosts.JAVA_POST_EVENTS));
		new Thread( () -> {
			subscriber.consume(((topic, key, value) ->  {
				String[] result = value.split(" ");
				if(key.equals(JavaPosts.PostsEventKeys.DELETE.name()) ||
						key.equals(JavaPosts.PostsEventKeys.CREATE_FAIL.name())){
					delete(result[result.length - 1]);
				}
			}));
		}).start();
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
