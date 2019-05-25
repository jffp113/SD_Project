package microgram.impl.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.impl.mongo.postsPOJOS.*;
import microgram.impl.mongo.profilesPOJOS.FollowingPOJO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.LinkedList;
import java.util.List;

import  microgram.api.java.Result;
import org.bson.conversions.Bson;


import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.impl.mongo.MongoProfiles.FOLLOWING_COLLECTION;
import static microgram.impl.mongo.MongoProfiles.PROFILES_COLLECTION;
import static microgram.impl.mongo.profilesPOJOS.FollowingPOJO.FOLLOWING_FIELD;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoPosts implements Posts {

    private static final String POST_COLLECTION = "posts";
    private static final String LIKES_COLLECTION = "likes";
    private static final String USER_POSTS_COLLECTIONS = "userPosts";


    private final MongoCollection<Post> posts;
    private final MongoCollection<LikePOJO> likes;
    private final MongoCollection<UserPostsPOJO> userPosts;
    private final MongoDatabase dbName;

    @SuppressWarnings("resource")
    public MongoPosts() {

		MongoClient mongo = new MongoClient(MongoProps.DEFAULT_MONGO_HOSTNAME);
        CodecRegistry pojoCodecRegistry =
                fromRegistries(MongoClient.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
         dbName = mongo.getDatabase(MongoProps.DB_NAME).withCodecRegistry(pojoCodecRegistry);

        posts = dbName.getCollection(POST_COLLECTION, Post.class);
        likes = dbName.getCollection(LIKES_COLLECTION, LikePOJO.class);
        userPosts = dbName.getCollection(USER_POSTS_COLLECTIONS, UserPostsPOJO.class);
        setIndex();
    }

    private void setIndex() {
        final IndexOptions option = new IndexOptions().unique(true);
        final Bson postsIndex = Indexes.ascending("postId");
        final Bson index = Indexes.compoundIndex(Indexes.ascending("userId","postId"));

        posts.createIndex(postsIndex, option);
        likes.createIndex(index,option);
        userPosts.createIndex(index);
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
        final String postId = post.getPostId();
        final String userId = post.getOwnerId();

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
        final DeleteResult result = posts.deleteOne(filter);

        if(result.getDeletedCount() == 0)
            return error(NOT_FOUND);

        likes.deleteMany(filter);
        userPosts.deleteMany(filter);

        //TODO remove image

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
            result.add(next.postId);
        }

        return ok(result);
    }

    @Override
    public Result<List<String>> getFeed(String userId) {
        MongoCollection profilesCollection  =
                dbName.getCollection(PROFILES_COLLECTION, Profile.class);
        MongoCollection<FollowingPOJO> followingCollection =
                dbName.getCollection(FOLLOWING_COLLECTION, FollowingPOJO.class);

        List<String> result = new LinkedList<>();

        long count = profilesCollection.countDocuments(Filters.eq("userId",userId));

        if(count == 0)
            return error(NOT_FOUND);

        for(FollowingPOJO current : followingCollection.find(Filters.eq(FOLLOWING_FIELD))){
            result.addAll(getUserPosts(current.following));
        }

        return ok(result);
    }

    private List<String> getUserPosts(String userId) {
        List<String> result = new LinkedList<>();

        for(UserPostsPOJO current : userPosts.find(Filters.eq("userId",userId))){
            result.add(current.postId);
        }

        return result;
    }
}
