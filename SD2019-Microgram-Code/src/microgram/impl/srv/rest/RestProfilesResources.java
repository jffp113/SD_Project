package microgram.impl.srv.rest;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;

public class RestProfilesResources extends RestResource implements RestProfiles {

	final Profiles impl;

	private static Logger Log = Logger.getLogger(JavaProfiles.class.getName());

	static{
		Log.setLevel( Level.FINER );
		Log.info("Initiated RestProfilesResources class\n");
	}

	public RestProfilesResources(URI serverUri) {
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
		Log.info("RestProfilesResources: getFollowing=" + userId + "\n");
		Set<String> r = super.resultOrThrow(this.impl.getFollowing(userId));
		Log.info(r.size() + "\n");
		return r;
	}
}