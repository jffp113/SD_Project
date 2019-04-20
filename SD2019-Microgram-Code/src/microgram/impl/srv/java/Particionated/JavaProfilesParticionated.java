package microgram.impl.srv.java.Particionated;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.srv.java.JavaProfiles;


import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
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
        System.out.println("Getting profile " + userId + "On server " + userLocation);
            if (userLocation == super.calculateServerLocation())
                return imp.getProfile(userId);

            return si.profiles(userLocation).getProfile(userId);

    }

    @Override
    public Result<Void> createProfile(Profile profile) {

            int userLocation = super.calculateResourceLocation(profile.getUserId());
        System.out.println("Getting profile " + profile.getUserId() + "On server " + userLocation);
            if (userLocation == super.calculateServerLocation())
                return imp.createProfile(profile);

            return si.profiles(userLocation).createProfile(profile);
    }

    @Override
    public Result<Void> deleteProfile(String userId) {
            int userLocation = super.calculateResourceLocation(userId);
            if (userLocation == super.calculateServerLocation())
                return imp.deleteProfile(userId);

            return si.profiles(userLocation).deleteProfile(userId);
    }

    private String addServerPattern(String userId) {
        return String.format(POST_ID_MARSHALLER,userId);
    }

    @Override
    public Result<List<Profile>> search(String prefix) {
        System.out.println("Start Searching for " + prefix);

            Matcher m = r.matcher(prefix);

            System.out.println(m.matches());
            if (m.matches()) {
                System.out.println("Matches " + prefix);
                return imp.search(m.group(1));
            }

            List<Profile> res = new LinkedList<>();

            int numPostServers = si.getNumProfilesServers();
            Result<List<Profile>> serverUsers;

            for (int i = 0; i < numPostServers; i++) {
                if (super.calculateServerLocation() == i) {
                    System.out.println("Invoking me");
                    serverUsers = imp.search(prefix);
                }
                else {
                    System.out.println("Invoking other server server id " + super.calculateServerLocation() + " " + addServerPattern(prefix));
                    serverUsers = si.profiles(i).search(addServerPattern(prefix));
                }

                if (serverUsers.isOK()) {
                    res.addAll(serverUsers.value());
                }
            }

            return ok(res);
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
             Matcher m1 = r.matcher(userId1);
             Matcher m2 = r.matcher(userId2);

             if (m1.matches() || m2.matches()) {
                return imp.follow(userId1,userId2,isFollowing);
             }


            int user1Location = super.calculateResourceLocation(userId1);
            int user2Location = super.calculateResourceLocation(userId2);

            System.out.println("Start following " + user1Location + " " + user2Location);
            Result<Void> r1;
            Result<Void> r2;

            if (user1Location == user2Location) {
                if (user1Location == super.calculateServerLocation())
                    return imp.follow(userId1, userId2, isFollowing);
                else {
                    return super.si.profiles(user1Location).follow(userId1, userId2, isFollowing);
                }
            }
            r1 = super.si.profiles(user1Location).follow(userId1, addServerPattern(userId2), isFollowing);
            r2 = super.si.profiles(user2Location).follow(addServerPattern(userId1), userId2, isFollowing);

            if (!r1.isOK() || !r2.isOK()) {
                super.si.profiles(user1Location).follow(userId1, addServerPattern(userId2), !isFollowing);
                super.si.profiles(user2Location).follow(addServerPattern(userId1), userId2, !isFollowing);
            }

            if(r1.isOK())
                return r2;

            return r1;
    }

    @Override
    public Result<Boolean> isFollowing(String userId1, String userId2) {
        int user1Location = super.calculateResourceLocation(userId1);

        if(user1Location == super.calculateServerLocation())
            return imp.isFollowing(userId1,userId2);

        return super.si.profiles(user1Location).isFollowing(userId1,userId2);
    }

    @Override
    public Result<Set<String>> getFollowing(String userId) {
        int userLocation = super.calculateResourceLocation(userId);

        if(userLocation == super.calculateServerLocation())
            return imp.getFollowing(userId);

        return super.si.profiles(userLocation).getFollowing(userId);

    }
}
