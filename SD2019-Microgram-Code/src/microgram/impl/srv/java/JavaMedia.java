package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import kakfa.KafkaSubscriber;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";
	private static Logger Log = Logger.getLogger(JavaPosts.class.getName());

	final String baseUri;
	private KafkaSubscriber subscriber;

	public JavaMedia(String baseUri ) {
		Log.setLevel( Level.FINER );
		this.baseUri = baseUri;
		new File( ROOT_DIR ).mkdirs();
		initKafkaSubscriber();
	}

	private void initKafkaSubscriber() {
		subscriber = new KafkaSubscriber(Arrays.asList(JavaPosts.JAVA_POST_EVENTS));

		new Thread( () -> {
			System.out.println("Thread");
			subscriber.consume(((topic, key, value) ->  {
				String[] result = value.split(" ");
				Log.info("Deleting " + result[result.length - 1]);
				if(key.equals(JavaPosts.PostsEventKeys.DELETE)) {
					delete(result[result.length - 1]);
					Log.info("Deleting " + result[result.length - 1]);
				}
			}));
		}).start();
	}
	
	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			String id = Hash.of(bytes);
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);
			Files.write(filename.toPath(), bytes);
			return ok(baseUri + "/" + id);
		} catch( Exception x  ) { 
			x.printStackTrace();
			return error(INTERNAL_ERROR);
		}
	}

	@Override
	public Result<byte[]> download(String id) {
		try {
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);
			if( filename.exists())
				return ok(Files.readAllBytes( filename.toPath() ));
			else
				return error(NOT_FOUND);
		} catch( Exception x ) {
			x.printStackTrace();
			return error(INTERNAL_ERROR);
		}
 	}

	@Override
	public Result<Void> delete(String id) {
		File file = new File(ROOT_DIR + id + MEDIA_EXTENSION);

		if(!file.exists())
			return error(NOT_FOUND);
		if(!file.delete())
			return error(INTERNAL_ERROR);
		
		return ok();
	}
	
}
