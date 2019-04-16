package microgram.impl.srv.java.Particionated;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.java.ServerInstantiator;
import utils.Hash;

import java.net.URI;
import java.util.*;

import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;

public class JavaPostsParticionated implements Posts{

    private Posts imp;

    private final ServerInstantiator si = new ServerInstantiator();
    private int serverId;

    public JavaPostsParticionated(Posts imp, URI uri){
        this.serverId = uri.hashCode();
        this.imp = imp;

    }

    @Override
    public Result<Post> getPost(String postId) {
        int numPostServers = this.si.getNumPostsServers();
        int postLocation = postId.hashCode() % numPostServers;
        if (postLocation == this.serverId % numPostServers)
            return imp.getPost(postId);

        return si.posts(postLocation).getPost(postId);
    }

    @Override
    public Result<String> createPost(Post post) {
        String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
        int numPostServers = this.si.getNumPostsServers();
        int postLocation = postId.hashCode() % numPostServers;
        if (postLocation == this.serverId % numPostServers)
            return imp.createPost(post);

        return si.posts(postLocation).createPost(post);
    }

    @Override
    public Result<Void> deletePost(String postId) {
        int numPostServers = this.si.getNumPostsServers();
        int postLocation = postId.hashCode() % numPostServers;
        if (postLocation == this.serverId % numPostServers)
            return imp.deletePost(postId);

        return si.posts(postLocation).deletePost(postId);
    }

    @Override
    public Result<Void> like(String postId, String userId, boolean isLiked) {
        int numPostServers = this.si.getNumPostsServers();
        int postLocation = postId.hashCode() % numPostServers;
        if (postLocation == this.serverId % numPostServers)
            return imp.like(postId, userId, isLiked);

        return si.posts(postLocation).like(postId, userId, isLiked);
    }

    @Override
    public Result<Boolean> isLiked(String postId, String userId) {
        int numPostServers = this.si.getNumPostsServers();
        int postLocation = postId.hashCode() % numPostServers;
        if (postLocation == this.serverId % numPostServers)
            return imp.isLiked(postId, userId);

        return si.posts(postLocation).isLiked(postId, userId);
    }

    @Override
    public Result<List<String>> getPosts(String userId) {
        Set<String> res = new TreeSet<>();
        int numPostServers = si.getNumPostsServers();
        boolean foundUser = false;
        Result<List<String>> serverPosts;

        for (int i = 0; i < numPostServers; i++) {

            if(serverId == i)
                serverPosts = imp.getPosts(userId);
            else
                serverPosts = si.posts(i).getPosts(userId);

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
        Set<String> res = new TreeSet<>();
        int numPostServers = si.getNumPostsServers();
        boolean foundUser = false;
        Result<List<String>> serverPosts;
        for (int i = 0; i < numPostServers; i++) {
            if(serverId == i)
                serverPosts = imp.getFeed(userId);
            else
                serverPosts = si.posts(i).getFeed(userId);

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
    public Result<Void> removeAllPostsFromUser(String userId) {
        int numPostServers = si.getNumPostsServers();
        boolean foundDeletes = false;
        Result<Void> reply;

        for (int i = 0; i < numPostServers; i++) {
            reply = si.posts(i).removeAllPostsFromUser(userId);
            if (reply.isOK())
                foundDeletes = true;
        }

        if (!foundDeletes)
            return error(NOT_FOUND);

        return ok ();
    }

}
