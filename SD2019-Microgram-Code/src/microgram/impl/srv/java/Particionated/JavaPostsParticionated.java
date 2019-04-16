package microgram.impl.srv.java.Particionated;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.java.ServerInstantiator;
import utils.Hash;
import java.util.*;

public class JavaPostsParticionated implements Posts{

    private Posts imp;

    private final ServerInstantiator si = new ServerInstantiator();
    private int serverId;

    public JavaPostsParticionated(Posts imp){
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

        return null;
    }

    @Override
    public Result<List<String>> getFeed(String userId) {

        return null;
    }

    @Override
    public Result<Void> removeAllPostsFromUser(String userId) {

        return null;
    }

    //NEW
    @Override
    public Result<List<String>> getPostsServer(String userId) {
        return null;
    }

    @Override
    public Result<List<String>> getFeedServer(String userId) {
        return null;
    }

    @Override
    public Result<Void> removeAllPostsFromUserServer(String userId) {
        return null;
    }
}
