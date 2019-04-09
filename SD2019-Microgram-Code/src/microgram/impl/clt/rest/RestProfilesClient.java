package microgram.impl.clt.rest;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;

public class RestProfilesClient extends RestClient implements Profiles {

	public RestProfilesClient(URI serverUri) {
		super(serverUri, RestProfiles.PATH);
	}

	private static Logger Log = Logger.getLogger(JavaProfiles.class.getName());

	static{
		Log.setLevel( Level.FINER );
		Log.info("Initiated RestProfilesClient class\n");
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		Response r = target.path(userId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		Log.info("Client: Finding User STATUS: "+ r.getStatus() + "\n");
		return super.responseContents(r, Status.OK, new GenericType<Profile>() {});
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		Response r = target
				.request()
				.post(Entity.entity(profile, MediaType.APPLICATION_JSON));
		
		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		Response r = target.path(userId)
				.request()
				.delete();
		
		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		Response r = target
				.queryParam("name", prefix)
				.request()
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<List<Profile>>() {});
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		Response r = target.path(userId1)
				.path("following")
				.path(userId2)
				.request()
				.put(Entity.entity(isFollowing, MediaType.APPLICATION_JSON));
		
		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		Response r = target.path(userId1)
				.path("following")
				.path(userId2)
				.request()
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {});
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		Log.info("RestProfilesClient: getFollowing");
		Response r = target
				.path("following")
				.path(userId)
				.request()
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<Set<String>>() {});
	}
	
}
