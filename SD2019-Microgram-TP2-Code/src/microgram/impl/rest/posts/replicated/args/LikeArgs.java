package microgram.impl.rest.posts.replicated.args;

public class LikeArgs {
    public String postId, userId;

    public LikeArgs(String postId, String userId){
        this.postId = postId;
        this.userId = userId;
    }

}
