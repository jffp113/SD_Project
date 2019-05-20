package microgram.impl.srv.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.srv.mongo.postsPOJOS.UserPostsPOJO;
import microgram.impl.srv.mongo.profilesPOJOS.FollowingPOJO;

import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;

import static microgram.api.Profile.*;
import static microgram.impl.srv.mongo.profilesPOJOS.FollowingPOJO.*;

public class MongoProfiles implements Profiles {
	
	public static final String PROFILES_COLLECTION = "profiles";
	public static final String FOLLOWING_COLLECTION = "following";
	public static final String FOLLOWERS_COLLECTION = "followers";
	
	private final MongoCollection<Profile> profiles;
	private final MongoCollection<FollowingPOJO> following;
	
	/**
	 * Mainly used in posts
	 */
	private final MongoCollection<UserPostsPOJO> userPosts;
	
	public MongoProfiles () {
		@SuppressWarnings("resource")
		final MongoClient mongo = new MongoClient(MongoProps.DEFAULT_MONGO_HOSTNAME);
		final CodecRegistry pojoCodecRegistry =
                fromRegistries(MongoClient.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        final MongoDatabase dbName = mongo.getDatabase(MongoProps.DB_NAME).withCodecRegistry(pojoCodecRegistry);
        this.profiles = dbName.getCollection(PROFILES_COLLECTION, Profile.class);
        this.following = dbName.getCollection(FOLLOWING_COLLECTION, FollowingPOJO.class);
        this.userPosts = dbName.getCollection(UserPostsPOJO.USER_ID_FIELD, UserPostsPOJO.class);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		final Profile foundProfile = this.profiles.find(Filters.eq(USER_ID_FIELD, userId)).first();
		
		if (foundProfile == null)
			return error(NOT_FOUND);
		
		setProfileAttributes(foundProfile);
		return ok(foundProfile);
	}
	
	/**
	 * Sets up the mutable values of a profile so it isn't inconsistent when returned.
	 * @param profile The profile to be changed.
	 */
	private void setProfileAttributes (final Profile profile) {
		final String userId = profile.getUserId();
		final int profilePosts = (int) this.userPosts.countDocuments(
				Filters.eq(UserPostsPOJO.USER_ID_FIELD, userId)); 	
		final int profileFollowing = (int) this.following.countDocuments(
				Filters.eq(FOLLOWING_FIELD, userId));
		final int profileFollowers = (int) this.following.countDocuments(
				Filters.eq(FOLLOWED_FIELD, userId));
		
		profile.setPosts(profilePosts);
		profile.setFollowing(profileFollowing);
		profile.setFollowers(profileFollowers);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		try {
			this.profiles.insertOne(profile);
		} catch (MongoWriteException e) {
			return error(CONFLICT);
		}
		return ok();
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		try {
			this.profiles.deleteOne(Filters.eq(USER_ID_FIELD, userId));
			this.following.deleteMany(Filters.or(Filters.eq(FOLLOWING_FIELD, userId),
					Filters.eq(FOLLOWED_FIELD, userId)));
		} catch (MongoWriteException e) {
			return error(NOT_FOUND);
		}
		return ok();
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		List<Profile> result = new LinkedList<>();
		for (Profile profile : this.profiles.find(Filters.regex(USER_ID_FIELD, prefix + ".*")))
			result.add(profile);
		return ok(result);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		final Result<Boolean> resultIsFollowing = this.isFollowing(userId1, userId2);
		if (!resultIsFollowing.isOK())
			return error(NOT_FOUND);
		
		if (resultIsFollowing.value() == isFollowing)
			if (isFollowing)
				return error(CONFLICT);
			else
				return error(NOT_FOUND);
		
		if (isFollowing)
			this.following.insertOne(new FollowingPOJO(userId1, userId2));
		else
			this.following.deleteOne(Filters.and(
					Filters.eq(FOLLOWING_FIELD, userId1),
					Filters.eq(FOLLOWED_FIELD, userId2)));
		
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		if(!this.profiles.find(Filters.eq(Profile.USER_ID_FIELD, userId1)).iterator().hasNext())
			return error(NOT_FOUND);
		if(!this.profiles.find(Filters.eq(Profile.USER_ID_FIELD, userId2)).iterator().hasNext())
			return error(NOT_FOUND);
		
		final boolean isFollowing = this.following.find(Filters.and(
				Filters.eq(FOLLOWING_FIELD, userId1),
				Filters.eq(FOLLOWED_FIELD, userId2)))
				.iterator().hasNext();
		return ok(isFollowing);
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		Collection<String> findFollowing = new LinkedList<>();
		for (Profile profile : this.profiles.find(Filters.eq(USER_ID_FIELD, userId)))
			findFollowing.add(profile.getUserId());
		if (findFollowing.size() == 0)
			return error(NOT_FOUND);
		Set<String> setOfFollowing = new HashSet<>(findFollowing);
		return ok(setOfFollowing);
	}

}
