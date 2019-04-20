package microgram.impl.srv.java.Particionated;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.java.JavaPosts;
import utils.Hash;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;

public class JavaPostsParticionated extends JavaParticionated implements Posts{

    private static final String USER_REPEATED_REGEX = "^\\?(.*)\\?$";
    private static final Pattern r = Pattern.compile(USER_REPEATED_REGEX);
    private static final String USER_ID_MARSHALLER = "?%s?";

    private Posts imp;

    public JavaPostsParticionated(URI uri){
        super(Math.abs(uri.hashCode()));
        this.imp = new JavaPosts();
        System.out.println(uri);
    }

    @Override
    public Result<Post> getPost(String postId) {
        int postLocation = calculateResourceLocation(postId);
        if (postLocation == calculateServerLocation())
            return imp.getPost(postId);

        return si.posts(postLocation).getPost(postId);
    }

    @Override
    public Result<String> createPost(Post post) {
        String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
        System.out.println("Start creating post with id=" + postId);
        int postLocation = calculateResourceLocation(postId);

        System.out.println("Post Location " + postLocation + " Server Location " + calculateServerLocation());

        if (postLocation == calculateServerLocation()) {
            System.out.println("Request For current Server");
            return imp.createPost(post);
        }
        System.out.println("Request for other Server");
        //throw new RuntimeException();
        return si.posts(postLocation).createPost(post);
    }

    @Override
    public Result<Void> deletePost(String postId) {
        int postLocation = calculateResourceLocation(postId);
        if (postLocation == calculateServerLocation())
            return imp.deletePost(postId);

        return si.posts(postLocation).deletePost(postId);
    }

    @Override
    public Result<Void> like(String postId, String userId, boolean isLiked) {
        int postLocation = calculateResourceLocation(postId);
        if (postLocation == calculateServerLocation())
            return imp.like(postId, userId, isLiked);

        return si.posts(postLocation).like(postId, userId, isLiked);
    }

    @Override
    public Result<Boolean> isLiked(String postId, String userId) {
        int postLocation = calculateResourceLocation(postId);

        if (postLocation == calculateServerLocation())
            return imp.isLiked(postId, userId);

        return si.posts(postLocation).isLiked(postId, userId);
    }

    private String addServerPattern(String userId) {
        return String.format(USER_ID_MARSHALLER,userId);
    }

    @Override
    public Result<List<String>> getPosts(String userId) {
        Matcher m = r.matcher(userId);

        if (!m.matches()){
            userId = m.group(1);
            return imp.getPosts(userId);
        }

        Set<String> res = new TreeSet<>();
        int numPostServers = si.getNumPostsServers();
        boolean foundUser = false;
        Result<List<String>> serverPosts;

        for (int i = 0; i < numPostServers; i++) {

            if(serverId == i)
                serverPosts = imp.getPosts(userId);
            else
                serverPosts = si.posts(i).getPosts(addServerPattern(userId));

            if (serverPosts.isOK()) {
                foundUser = true;
                res.addAll(serverPosts.value());
            }
        }

        if (!foundUser)
            return error(NOT_FOUND);
        return ok (new ArrayList<>(res));
    }

    @Override
    public Result<List<String>> getFeed(String userId) {
            Matcher m = r.matcher(userId);

            if (m.matches()) {
                userId = m.group(1);
                return imp.getFeed(userId);
            }

            Set<String> res = new TreeSet<>();
            int numPostServers = si.getNumPostsServers();
            boolean foundUser = false;
            Result<List<String>> serverPosts;
            for (int i = 0; i < numPostServers; i++) {
                if (serverId == i)
                    serverPosts = imp.getFeed(userId);
                else
                    serverPosts = si.posts(i).getFeed(addServerPattern(userId));

                if (serverPosts.isOK()) {
                    foundUser = true;
                    res.addAll(serverPosts.value());
                }
            }

            if (!foundUser)
                return error(NOT_FOUND);
            return ok(new ArrayList<>(res));
    }

    @Override
    public Result<Void> removeAllPostsFromUser(String userId) {
        Matcher m = r.matcher(userId);

        if(!m.matches()) {
            userId = m.group(1);
            return imp.removeAllPostsFromUser(userId);
        }

        int numPostServers = si.getNumPostsServers();
        boolean foundDeletes = false;
        Result<Void> reply;

        for (int i = 0; i < numPostServers; i++) {
            if(serverId == i)
                reply = imp.removeAllPostsFromUser(userId);
            else
                reply = si.posts(i).removeAllPostsFromUser(addServerPattern(userId));

            if (reply.isOK())
                foundDeletes = true;
        }

        if (!foundDeletes)
            return error(NOT_FOUND);

        return ok ();
    }

}
