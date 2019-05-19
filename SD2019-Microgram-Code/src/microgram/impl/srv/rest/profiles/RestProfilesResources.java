package microgram.impl.srv.rest.profiles;

import java.net.URI;
import java.util.List;
import java.util.Set;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;
import microgram.impl.srv.rest.RestResource;

public class RestProfilesResources extends RestResource implements RestProfiles {

	final Profiles impl;

	public RestProfilesResources(URI uri) {
		this.impl = new JavaProfiles();
	}
	
	@Override
	public Profile getProfile(String userId) {
		return super.resultOrThrow(this.impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) {
		super.resultOrThrow(this.impl.createProfile(profile));
		
	}
	
	@Override
	public void deleteProfile(String userId) {
		super.resultOrThrow(this.impl.deleteProfile(userId));	
	}

	@Override
	public List<Profile> search(String name) {
		return super.resultOrThrow(this.impl.search(name));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) {
		super.resultOrThrow(this.impl.follow(userId1, userId2, isFollowing));
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) {
		return super.resultOrThrow(this.impl.isFollowing(userId1, userId2));
	}

	@Override
	public Set<String> getFollowing(String userId) {
		return super.resultOrThrow(this.impl.getFollowing(userId));
	}
}