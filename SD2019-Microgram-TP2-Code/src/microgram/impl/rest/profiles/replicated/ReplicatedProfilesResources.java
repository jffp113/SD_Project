package microgram.impl.rest.profiles.replicated;

import java.util.List;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.java.JavaProfiles;
import microgram.impl.mongo.MongoProfiles;
import microgram.impl.rest.RestResource;
import microgram.impl.rest.replication.MicrogramTopic;
import microgram.impl.rest.replication.TotalOrderExecutor;

public class ReplicatedProfilesResources extends RestResource implements RestProfiles {
	final Profiles localDB;
	final ProfilesReplicator replicator;
	
	public ReplicatedProfilesResources() {
		this.localDB = new MongoProfiles() ;
		this.replicator = new ProfilesReplicator(localDB, new TotalOrderExecutor(MicrogramTopic.MicrogramEvents));
	}

	@Override
	public Profile getProfile(String userId) {
		System.out.println("Get Profile " + userId);
		return super.resultOrThrow( replicator.getProfile( userId ));
	}

	@Override
	public void createProfile(Profile profile) {
		System.out.println("Creating Profile " + profile.getUserId());
		super.resultOrThrow( replicator.createProfile(profile));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) {
		super.resultOrThrow( replicator.follow(userId1, userId2, isFollowing));
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) {
		return super.resultOrThrow( replicator.isFollowing(userId1, userId2));
	}

	@Override
	public void deleteProfile(String userId) {
		super.resultOrThrow( replicator.deleteProfile(userId));
	}

	@Override
	public List<Profile> search(String prefix) {
		return super.resultOrThrow( replicator.search(prefix));
	}

}
