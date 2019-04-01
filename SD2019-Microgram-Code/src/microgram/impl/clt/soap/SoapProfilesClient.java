package microgram.impl.clt.soap;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.*;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.soap.SoapProfiles;

public class SoapProfilesClient extends SoapClient implements Profiles {

	SoapProfiles impl;

	public SoapProfilesClient(URI serverUri) {
		super(serverUri);
	}
	
	private SoapProfiles impl()  {
		if(impl == null ) {
			synchronized(this) {
				if(impl == null) {
					QName QNAME = new QName(SoapProfiles.NAMESPACE ,SoapProfiles.NAME);
					Service service = Service.create(urlOrNull(uri),QNAME);
					impl = service.getPort(SoapProfiles.class);
					super.setTimeout((BindingProvider) impl);
				}
			}
		}
		return impl;
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		return super.tryCatchResult(() -> this.impl().getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		return super.tryCatchVoid(() -> this.impl().createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		return super.tryCatchVoid(() -> this.impl().deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return super.tryCatchResult(() -> this.impl().search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		return super.tryCatchVoid(() -> this.impl().follow(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		return super.tryCatchResult(() -> this.impl().isFollowing(userId1, userId2));
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {
		return super.tryCatchResult(() -> this.impl().getFollowing(userId));
	}
}
