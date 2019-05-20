/**
 * 
 */
package microgram.impl.srv.mongo.profilesPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class FollowingPOJO {

	public final String following;
	public final String followed;
	
	@BsonCreator
	public FollowingPOJO (@BsonProperty("following") final String following,
			@BsonProperty("followed") final String followed) {
		this.following = following;
		this.followed = followed;
	}
	
}
