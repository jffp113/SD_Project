package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import utils.IP;

public class JavaProfiles implements Profiles {
	
	private static final int INCREASE = 1;
	private static final int DECREASE = -1;

	private static final String POST_REPEATED_REGEX = "^\\?(.*)\\?$";
	private static final Pattern r = Pattern.compile(POST_REPEATED_REGEX);

	public static final String JAVA_PROFILES_EVENTS = "Microgram-JavaProfiles";

	public enum ProfilesEventKeys {
		DELETE
	};

	private Map<String, Profile> users =
			new ConcurrentHashMap<>();
	
	/**
	 * User key is being followed by
	 */
	private Map<String, Set<String>> followers = 
			new ConcurrentHashMap<>();
	
	/**
	 * User key is following
	 */
	private Map<String, Set<String>> following = 
			new ConcurrentHashMap<>();

	private final ServerInstantiator si = new ServerInstantiator();

	private KafkaSubscriber subscriber;
	private KafkaPublisher publisher;

	public JavaProfiles() {
		initKafkaSubscriber();
	}

	private void initKafkaSubscriber() {
		publisher = new KafkaPublisher();
		subscriber = new KafkaSubscriber(Arrays.asList(JavaPosts.JAVA_POST_EVENTS));
		new Thread( () -> {
			subscriber.consume(((topic, key, value) ->  {
				String[] result = value.split(" ");
				if(key.equals(JavaPosts.PostsEventKeys.DELETE.name())) {
					changeUserPostsValue(DECREASE,result[result.length - 2]);
				}
				else if(key.equals(JavaPosts.PostsEventKeys.CREATE.name())) {
					changeUserPostsValue(INCREASE,result[result.length - 1]);
				}
			}));
		}).start();
	}

	private void changeUserPostsValue(int change,String user){
		Profile profile = users.get(user);
		if(profile != null) {
			profile.changeNumberOfPosts(change);
		}
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		Profile res = users.get( userId );
		if( res == null ) 
			return error(NOT_FOUND);

		res.setFollowers( followers.get(userId).size() );
		res.setFollowing( following.get(userId).size() );

		return ok(res);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		Profile res = users.putIfAbsent( profile.getUserId(), profile );
		if( res != null ) 
			return error(CONFLICT);

		followers.put( profile.getUserId(), Collections.synchronizedSet(new HashSet<>()));
		following.put( profile.getUserId(), Collections.synchronizedSet(new HashSet<>()));
		return ok();
	}

	private void deletePostsNotification(String userId) {
		String message = String.format("%s %s", IP.hostAddress(),userId);
		publisher.publish(JAVA_PROFILES_EVENTS, JavaProfiles.ProfilesEventKeys.DELETE.name(),message);
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		if (this.users.remove(userId) == null)
			return error(NOT_FOUND);

		Set<String> followers = this.followers.remove(userId);
		Set<String> following = this.following.remove(userId);


		for(String f : followers){
			boolean a = this.following.get(f).remove(userId);
		}
		for(String f : following){
			boolean a = this.followers.get(f).remove(userId);
		}

		deletePostsNotification(userId);

		return ok();
	}
	
	@Override
	public Result<List<Profile>> search(String prefix) {
		return ok(users.values().stream()
				.filter( p -> p.getUserId().startsWith( prefix ) )
				.collect( Collectors.toList()));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {		
		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);

		if( isFollowing ) {
			boolean added1 = s1.add(userId2 ), added2 = s2.add( userId1 );
			if( ! added1 || ! added2 )
				return error(CONFLICT);
		} else {
			boolean removed1 = s1.remove(userId2), removed2 = s2.remove( userId1);
			if( ! removed1 || ! removed2 )
				return error(NOT_FOUND);
		}
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);
		else
			return ok(s1.contains( userId2 ) && s2.contains( userId1 ));
	}
	
	public Result<Set<String>> getFollowing (String userId) {
		Set<String> followUser = this.following.get(userId);

		if (followUser == null)
			return error(NOT_FOUND);

		return ok(followUser);
	}

}
