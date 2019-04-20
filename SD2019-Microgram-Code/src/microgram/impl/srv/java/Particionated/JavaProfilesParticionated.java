package microgram.impl.srv.java.Particionated;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.srv.java.JavaPosts;
import microgram.impl.srv.java.JavaProfiles;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;

public class JavaProfilesParticionated extends JavaParticionated implements Profiles {

    private static final String POST_REPEATED_REGEX = "^\\?(.*)\\?$";
    private static final Pattern r = Pattern.compile(POST_REPEATED_REGEX);
    private static final String POST_ID_MARSHALLER = "?%s?";

    private Profiles imp;

    public JavaProfilesParticionated(URI uri){
        super(Math.abs(uri.hashCode()));
        imp = new JavaProfiles();
    }

    @Override
    public Result<Profile> getProfile(String userId) {
        int userLocation = super.calculateResourceLocation(userId);
        if(userLocation == super.calculateServerLocation())
            return imp.getProfile(userId);

        return si.profiles(userLocation).getProfile(userId);
    }

    @Override
    public Result<Void> createProfile(Profile profile) {
        int userLocation = super.calculateResourceLocation(profile.getUserId());
        if(userLocation == super.calculateServerLocation())
            return imp.createProfile(profile);

        return si.profiles(userLocation).createProfile(profile);
    }

    @Override
    public Result<Void> deleteProfile(String userId) {
        int userLocation = super.calculateResourceLocation(userId);
        if(userLocation == super.calculateServerLocation())
            return imp.deleteProfile(userId);

        return si.profiles(userLocation).deleteProfile(userId);
    }

    private String addServerPattern(String userId) {
        return String.format(POST_ID_MARSHALLER,userId);
    }

    @Override
    public Result<List<Profile>> search(String prefix) {
        Matcher m = r.matcher(prefix);

        if (!m.matches()){
            prefix = m.group(1);
            return imp.search(prefix);
        }

        Set<Profile> res = new TreeSet<>();

        int numPostServers = si.getNumPostsServers();
        boolean foundUser = false;
        Result<List<Profile>> serverUsers;

        for (int i = 0; i < numPostServers; i++) {

            if(serverId == i)
                serverUsers = imp.search(prefix);
            else
                serverUsers = si.profiles(i).search(addServerPattern(prefix));

            if (serverUsers.isOK()) {
                foundUser = true;
                res.addAll(serverUsers.value());
            }
        }

        if (!foundUser)
            return error(NOT_FOUND);

        return ok (new ArrayList<>(res));
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
        throw new NotImplementedException();
    }

    @Override
    public Result<Boolean> isFollowing(String userId1, String userId2) {
        throw new NotImplementedException();
    }

    @Override
    public Result<Set<String>> getFollowing(String userId) {
        throw new NotImplementedException();
    }
}
