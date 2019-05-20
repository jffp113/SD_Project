package microgram.impl.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserPostsPOJO {
    public static final String USER_ID_FIELD = "userId";

    public final String userId;
    public final String postId;

    @BsonCreator
    public UserPostsPOJO(@BsonProperty("userId") String userId, @BsonProperty("postId") String postId){
        this.userId = userId;
        this.postId = postId;
    }

}
