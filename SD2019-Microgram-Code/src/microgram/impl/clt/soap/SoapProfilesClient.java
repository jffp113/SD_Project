package microgram.impl.clt.soap;

import java.net.URI;
import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.soap.SoapProfiles;

public class SoapProfilesClient extends SoapClient implements Profiles {

	SoapProfiles prf;

	public SoapProfilesClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		return super.tryCatchResult(() -> this.prf.getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		return super.tryCatchVoid(() -> this.prf.createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		return super.tryCatchVoid(() -> this.prf.deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return super.tryCatchResult(() -> this.prf.search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		return super.tryCatchVoid(() -> this.prf.follow(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		return super.tryCatchResult(() -> this.prf.isFollowing(userId1, userId2));
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		return super.tryCatchResult(() -> this.prf.getFollowing(userId));
	}
}
