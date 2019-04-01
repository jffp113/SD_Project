package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import microgram.api.Profile;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.srv.rest.RestResource;

public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {

	private Map<String, Profile> users =
			new ConcurrentHashMap<>(new HashMap<>());
	private Map<String, Set<String>> followers = 
			new ConcurrentHashMap<>(new HashMap<>());
	private Map<String, Set<String>> following = 
			new ConcurrentHashMap<>(new HashMap<>());
	
	/*
	 * Should sets be concurrent too?
	 * Does ConcurrentMaps destroy parallelism?
	 * 
	 * */
	
	private Profiles[] profiles;
	private Posts[] posts;
	private Media[] media;
	
	public JavaProfiles() {
		super();
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
			return error(NOT_FOUND);
		
		followers.put( profile.getUserId(), new HashSet<>());
		following.put( profile.getUserId(), new HashSet<>());
		return ok();
	}
	
	@Override
	public Result<Void> deleteProfile(String userId) {		//<----- MADE THIS
		if (this.users.remove(userId) == null)
			return error(NOT_FOUND);
		
		this.followers.remove(userId);
		this.following.forEach((k, v) -> v.remove(userId));
		//TODO Remove Posts from user
		
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
			if( ! s1.add( userId2 ) || ! s2.add( userId1 ) )
				return error(CONFLICT);		
		} else {
			if( ! s1.remove( userId2 ) || ! s2.remove( userId1 ) )
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
		else
			return ok(followUser);
	}
	
}
