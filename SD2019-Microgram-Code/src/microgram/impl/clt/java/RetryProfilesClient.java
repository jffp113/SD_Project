package microgram.impl.clt.java;

import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class RetryProfilesClient extends RetryClient implements Profiles {

	private final Profiles impl;

	public RetryProfilesClient( Profiles impl ) {
		this.impl = impl;	
	}
	
	@Override
	public Result<Profile> getProfile(String userId) {
		return super.reTry( () -> impl.getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		return super.reTry(() -> impl.createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		return super.reTry(() -> impl.deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return super.reTry(() -> impl.search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		return super.reTry(() -> impl.follow(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		return super.reTry(() -> impl.isFollowing(userId1, userId2));
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		return super.reTry(() -> impl.getFollowing(userId));
	}
}
