package microgram.impl.srv.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserPostsPOJO {
	
	public static final String USER_ID_FIELD = "userId";

    public final String userId;
    public final String post;

    @BsonCreator
    public UserPostsPOJO(@BsonProperty(USER_ID_FIELD) String userId, @BsonProperty("post") String post){
        this.userId = userId;
        this.post = post;
    }
}
