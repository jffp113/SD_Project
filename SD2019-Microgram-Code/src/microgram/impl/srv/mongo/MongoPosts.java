package microgram.impl.srv.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.impl.srv.mongo.postsPOJOS.LikePOJO;
import microgram.impl.srv.mongo.postsPOJOS.UserPostsPOJO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.LinkedList;
import java.util.List;
import  microgram.api.java.Result;
import org.bson.conversions.Bson;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Hash;

import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoPosts implements Posts {

    private static final String POST_COLLECTION = "posts";
    private static final String LIKES_COLLECTION = "posts";
    private static final String USER_POSTS_COLLECTIONS = "userPosts";


    private final MongoCollection<Post> posts;
    private final MongoCollection<LikePOJO> likes;
    private final MongoCollection<UserPostsPOJO> userPosts;

    public MongoPosts() {
        MongoClient mongo = new MongoClient(MongoProps.DEFAULT_MONGO_HOSTNAME);
        CodecRegistry pojoCodecRegistry =
                fromRegistries(MongoClient.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase dbName = mongo.getDatabase(MongoProps.DB_NAME).withCodecRegistry(pojoCodecRegistry);


        posts = dbName.getCollection(POST_COLLECTION, Post.class);
        likes = dbName.getCollection(LIKES_COLLECTION, LikePOJO.class);
        userPosts = dbName.getCollection(USER_POSTS_COLLECTIONS, UserPostsPOJO.class);
        setIndex();
    }

    private void setIndex() {
        final IndexOptions option = new IndexOptions().unique(true);
        final Bson postsIndex = Indexes.hashed("postId");
        final  BasicDBObject index = new BasicDBObject();
                            index.put("userId",1);
                            index.put("postId",1);
        posts.createIndex(postsIndex, option);
        likes.createIndex(index,option);
        userPosts.createIndex(index,option);
    }

    @Override
    public Result<Post> getPost(String postId) {
        final Post post = posts.find(Filters.eq("postId",postId)).first();

        if(post != null) {
            int numberOfLikes = (int)likes.countDocuments(Filters.eq("postId",postId));
            post.setLikes(numberOfLikes);
        }else
            return error(NOT_FOUND);

        return  ok(post);

    }


    @Override
    public Result<String> createPost(Post post) {
        final String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
        final String userId = post.getOwnerId();
        post.setPostId(postId);
        try {
            posts.insertOne(post);
            userPosts.insertOne(new UserPostsPOJO(userId,postId));
        }catch(MongoWriteException e ){
            return error(CONFLICT);
        }

        return ok(postId);
    }

    @Override
    public Result<Void> deletePost(String postId) {
        final Bson filter = Filters.eq("postId",postId);
        final DeleteResult result = posts.deleteOne(Filters.eq("postId",postId));

        if(result.getDeletedCount() == 0)
            return error(NOT_FOUND);

        likes.deleteMany(filter);
        userPosts.deleteMany(filter);

        //remove image

        return ok();
    }

    @Override
    public Result<Void> like(String postId, String userId, boolean isLiked) {
        final Post post = posts.find(Filters.eq("postId",postId)).first();

        if(post == null)
            return error(NOT_FOUND);

        if(isLiked){
            return tryLikePost(postId,userId);
        }
        else
            return tryDislikePost(postId,userId);

    }

    private Result<Void> tryDislikePost(String postId, String userId) {
        final DeleteResult result = likes.deleteOne(Filters.and(Filters.eq("postId",postId),
                Filters.eq("userId",userId)));

        if(result.getDeletedCount() == 0)
            return error(NOT_FOUND);

        return ok();
    }

    private Result<Void> tryLikePost(String postId, String userId){
        try {
            likes.insertOne(new LikePOJO(postId,userId));
        }catch(MongoWriteException e ){
            return error(CONFLICT);
        }
        return ok();
    }

    @Override
    public Result<Boolean> isLiked(String postId, String userId) {
        final Post post = posts.find(Filters.eq("postId",postId)).first();

        if(post == null)
            return error(NOT_FOUND);

        final LikePOJO result = likes.find(Filters.and(Filters.eq("postId",postId),
                Filters.eq("userId",userId))).first();

        return ok(result != null) ;
    }

    @Override
    public Result<List<String>> getPosts(String userId) {
        final List<String> result = new LinkedList<>();

        for(UserPostsPOJO next: userPosts.find(Filters.eq("userId",userId))){
            result.add(next.userId);
        }

        return ok(result);
    }

    @Override
    public Result<List<String>> getFeed(String userId) {
        //TODO waiting for Camponês
        return null;
    }

    @Override
    public Result<Void> removeAllPostsFromUser(String userId) {
        throw new NotImplementedException();
        //Not needed with mongoDb
    }

    public static void main(String[] args){
        MongoPosts post = new MongoPosts();
        Result<String> r = post.createPost(new Post("","jff.pereira","http","portugal",123123,1));
        System.out.println(r.value());
    }

}
