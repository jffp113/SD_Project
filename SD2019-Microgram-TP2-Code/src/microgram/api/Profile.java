package microgram.api;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import utils.JSON;

/**
 * Represents a user Profile
 * 
 * A user Profile has an unique userId; a comprises: the user's full name; and, a photo, stored at some photourl. This information is immutable.
 * The profile also gathers the user's statistics: ie., the number of posts made, the number of profiles the user is following, the number of profiles following this user. 
 * All these are mutable.
 * 
 * @author smd
 *
 */
public class Profile {

	public static final String USER_ID_FIELD = "userId";

	public static final String FULL_NAME_FIELD = "fullName";

	public static final String PHOTO_URL_FIELD = "photoUrl";

	String userId;
	String fullName;
	String photoUrl;

	@BsonIgnore
	int posts;
	@BsonIgnore
	int following;
	@BsonIgnore
	int followers;

	@BsonCreator
	public Profile(@BsonProperty(USER_ID_FIELD) String userId, @BsonProperty(FULL_NAME_FIELD) String fullName,
				   @BsonProperty(PHOTO_URL_FIELD) String photoUrl) {
		this.userId = userId;
		this.fullName = fullName;
		this.photoUrl = photoUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getPosts() {
		return posts;
	}

	public void setPosts(int posts) {
		this.posts = posts;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}
	
	@Override
	public String toString() {
		return JSON.encode( this );
	}
}
