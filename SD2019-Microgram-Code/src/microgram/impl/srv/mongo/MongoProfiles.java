package microgram.impl.srv.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.Set;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.srv.mongo.profilesPOJOS.FollowersPOJO;
import microgram.impl.srv.mongo.profilesPOJOS.FollowingPOJO;

import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;

public class MongoProfiles implements Profiles {
	
	public static final String PROFILES_COLLECTION = "profiles";
	public static final String FOLLOWING_COLLECTION = "following";
	public static final String FOLLOWERS_COLLECTION = "followers";
	
	private final MongoCollection<Profile> profiles;
	private final MongoCollection<FollowingPOJO> following;
	private final MongoCollection<FollowersPOJO> followers;	
	
	public MongoProfiles () {
		@SuppressWarnings("resource")
		final MongoClient mongo = new MongoClient(MongoProps.DEFAULT_MONGO_HOSTNAME);
		final CodecRegistry pojoCodecRegistry =
                fromRegistries(MongoClient.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        final MongoDatabase dbName = mongo.getDatabase(MongoProps.DB_NAME).withCodecRegistry(pojoCodecRegistry);
        this.profiles = dbName.getCollection(PROFILES_COLLECTION, Profile.class);
        this.following = dbName.getCollection(FOLLOWING_COLLECTION, FollowingPOJO.class);
        this.followers = dbName.getCollection(FOLLOWERS_COLLECTION, FollowersPOJO.class);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		final Profile foundProfile = this.profiles.find(Filters.eq(Profile.USER_ID_FIELD, userId)).first();
		
		if (foundProfile == null)
			return error(NOT_FOUND);
		
//		final int profilePosts = TODO stuff with the Posts
		final int profileFollowing = 
		
		return null;
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
