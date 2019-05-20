package microgram.impl.srv.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class LikePOJO {

    public final String postId;
    public final String userId;

    public LikePOJO(@BsonProperty String postId,@BsonProperty String userId){
        this.postId = postId;
        this.userId = userId;
    }
}
