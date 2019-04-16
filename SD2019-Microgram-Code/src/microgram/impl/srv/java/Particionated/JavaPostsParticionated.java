package microgram.impl.srv.java.Particionated;

import kakfa.KafkaSubscriber;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.java.JavaPosts;
import microgram.impl.srv.java.ServerInstantiator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;
import java.util.*;

public class JavaPostsParticionated implements Posts{

    private Posts imp;
    private String selfKey;

    private final ServerInstantiator si = new ServerInstantiator();
    private Posts[] posts;

    public JavaPostsParticionated(Posts imp,int numberOfServers){
        this.imp = imp;
        posts = null;
    }

    private void serverFinder() {
        if(posts != null)
            return;

        this.posts = si.posts();
    }

    @Override
    public Result<Post> getPost(String postId) {
        serverFinder();
        return null;
    }

    @Override
    public Result<String> createPost(Post post) {
        serverFinder();
        return null;
    }

    @Override
    public Result<Void> deletePost(String postId) {
        serverFinder();
        String key = postsLocation.get(postId);
        if(key == null || key == selfKey)
            return imp.deletePost(postId);

        return mapper.get(key).client.deletePost(postId);
    }

    @Override
    public Result<Void> like(String postId, String userId, boolean isLiked) {
        serverFinder();

        String key = postsLocation.get(postId);
        if(key == null || key == selfKey)
            return imp.like(postId,userId,isLiked);

        return mapper.get(key).client.like(postId,userId,isLiked);
    }

    @Override
    public Result<Boolean> isLiked(String postId, String userId) {
        serverFinder();

        return null;
    }

    @Override
    public Result<List<String>> getPosts(String userId) {
        serverFinder();

        return null;
    }

    @Override
    public Result<List<String>> getFeed(String userId) {
        serverFinder();

        return null;
    }

    @Override
    public Result<Void> removeAllPostsFromUser(String userId) {
        serverFinder();

        return null;
    }
    
    public URI getServiceURI() {
        return imp.getServiceURI();
    }
}
