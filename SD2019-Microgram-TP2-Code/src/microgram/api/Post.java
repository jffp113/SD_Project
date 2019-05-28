package microgram.api;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import utils.Hash;
import utils.JSON;

/**
 * Represents a Post.
 * 
 * A post has an unique postId and a single ownerId profile; comprises of an image, taken at some location and stored at some mediaurl; it is timestamped.
 * A post also has a number of likes, which can increase or decrease over time. It is the only piece of information that is mutable.
 *  
 * @author smd
 *
 */
public class Post {
	
	String postId;
	String ownerId;
	String mediaUrl;
	String location;
	long timestamp;
	
	int likes;

	public Post() {}

	@BsonCreator
	public Post(@BsonProperty("postId") String postId, @BsonProperty("ownerId") String ownerId,
				@BsonProperty("mediaUrl") String mediaUrl, @BsonProperty("location") String location,
				@BsonProperty("timestamp") long timestamp) {
		this.postId = postId;
		this.ownerId = ownerId;
		this.mediaUrl = mediaUrl;
		this.location = location;
		this.timestamp = timestamp;
	}
	
	public String getMediaUrl() {
		return mediaUrl;
	}

	public String getLocation() {
		return location;
	}

	public int getLikes() {
		return likes;
	}
	
	public String getPostId() {
		if( postId == null ) 
			postId = generatePostId();
		return postId;
	}
	
	public String getOwnerId() {
		return ownerId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	@Override
	public String toString() {
		return JSON.encode( this );
	}
	
	private String generatePostId() {
		return getOwnerId() + DELIMITER + Hash.of( this.mediaUrl );		
	}
	
	public static String ownerIdFromPostId( String postId ) {
		int i = postId.indexOf(DELIMITER);
		return i >= 0 ? postId.substring(0, i) : postId;
	}
	
	private static final char DELIMITER = '-';
}