package microgram.impl.srv.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Set;

public class UserPostsPOJO {

    public final String userId;
    public final String post;

    public UserPostsPOJO(@BsonProperty String userId, @BsonProperty String post){
        this.userId = userId;
        this.post = post;
    }
}
