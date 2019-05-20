package microgram.impl.srv.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserPostsPOJO {

    public final String userId;
    public final String post;

    @BsonCreator
    public UserPostsPOJO(@BsonProperty String userId, @BsonProperty String post){
        this.userId = userId;
        this.post = post;
    }
}
