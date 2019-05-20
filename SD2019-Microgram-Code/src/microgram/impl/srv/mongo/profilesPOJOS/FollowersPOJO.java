/**
 * 
 */
package microgram.impl.srv.mongo.profilesPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class FollowersPOJO {

	public final String followedBy;
	public final String following;
	
	@BsonCreator
	public FollowersPOJO (@BsonProperty("followedBy") final String followedBy,
			@BsonProperty("following") final String following) {
		this.followedBy = followedBy;
		this.following = following;
	}
	
}
