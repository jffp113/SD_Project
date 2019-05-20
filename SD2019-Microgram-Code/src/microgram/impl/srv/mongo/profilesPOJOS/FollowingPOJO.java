/**
 * 
 */
package microgram.impl.srv.mongo.profilesPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class FollowingPOJO {
	
	public static final String FOLLOWING_FIELD = "following";
	
	public static final String FOLLOWED_FIELD = "followed";

	public final String following;
	public final String followed;
	
	@BsonCreator
	public FollowingPOJO (@BsonProperty(FOLLOWING_FIELD) final String following,
			@BsonProperty(FOLLOWED_FIELD) final String followed) {
		this.following = following;
		this.followed = followed;
	}
	
}
