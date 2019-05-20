package microgram.impl.mongo.postsPOJOS;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class LikePOJO {

    public final String postId;
    public final String userId;

    @BsonCreator
    public LikePOJO(@BsonProperty String postId,@BsonProperty String userId){
        this.postId = postId;
        this.userId = userId;
    }
}
