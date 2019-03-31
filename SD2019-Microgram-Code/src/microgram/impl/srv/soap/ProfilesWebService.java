package microgram.impl.srv.soap;


import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapProfiles;
import microgram.impl.srv.java.JavaProfiles;

public class ProfilesWebService extends SoapService implements SoapProfiles {

	final Profiles impl;
	
	protected ProfilesWebService() {
		this.impl = new JavaProfiles();
	}
	
	@Override
	public Profile getProfile( String userId ) throws MicrogramException {
		return super.resultOrThrow( impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) throws MicrogramException {
		super.resultOrThrow(impl.createProfile(profile));
	}

	@Override
	public void deleteProfile(String userId) throws MicrogramException {
		super.resultOrThrow(impl.deleteProfile(userId));		
	}

	@Override
	public List<Profile> search(String prefix) throws MicrogramException {
		return super.resultOrThrow(impl.search(prefix));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) throws MicrogramException {
		super.resultOrThrow(impl.follow(userId1, userId2, isFollowing));		
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) throws MicrogramException {
		return super.resultOrThrow(impl.isFollowing(userId1, userId2));
	}

	@Override
	public Set<String> getFollowing(String userId) throws MicrogramException {
		return super.resultOrThrow(impl.getFollowing(userId));
	}
	
}
