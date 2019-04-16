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

    private SortedSet<Node<Posts>> servers;
    private Map<String,Node<Posts>> mapper;
    private Map<String,String> postsLocation;

    private KafkaSubscriber subscriber;

    public JavaPostsParticionated(Posts imp){
        this.imp = imp;
        servers = null;
        initKafkaSubscriber();
    }

    private String parseKey(String uri){
        throw new NotImplementedException();
    }

    //TODO test
    private void initKafkaSubscriber() {
        subscriber = new KafkaSubscriber(Arrays.asList(JavaPosts.JAVA_POST_EVENTS));
        new Thread( () -> {
            subscriber.consume(((topic, key, value) ->  {
                String[] result = value.split(" ");

            }));
        }).start();
    }

    private void serverFinder() {
        if(servers != null)
            return;

        servers = new TreeSet<>();
        mapper = new HashMap<>();
        postsLocation = new HashMap<>();
        selfKey = parseKey(imp.getServiceURI().toString());

        for(Posts p  : si.posts()){
            Node<Posts> node = new Node<>(p.getServiceURI().toString(),0,p);
            servers.add(node);
            mapper.put(parseKey(p.getServiceURI().toString()),node);
        }
    }

    @Override
    public Result<Post> getPost(String postId) {
        serverFinder();
        String key = postsLocation.get(postId);
        if(key == null || key == selfKey)
            return imp.getPost(postId);

        return mapper.get(key).client.getPost(postId);
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
