package microgram.impl.rest.posts.replicated;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.rest.replication.MicrogramOperation;
import microgram.impl.rest.replication.MicrogramOperationExecutor;
import microgram.impl.rest.replication.OrderedExecutor;

import java.util.List;

public class PostsReplicator implements MicrogramOperationExecutor, Posts {

	private static final int PostID = 0, UserID = 1;
	
	final Posts localReplicaDB;
	final OrderedExecutor executor;
	
	PostsReplicator(Posts localDB, OrderedExecutor executor) {
		this.localReplicaDB = localDB;
		this.executor = executor.init(this);
	}

	@Override
	public Result<Post> getPost(String postId) {
		return null;
	}

	@Override
	public Result<String> createPost(Post post) {
		return null;
	}

	@Override
	public Result<Void> deletePost(String postId) {
		return null;
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		return null;
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		return null;
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		return null;
	}

	@Override
	public Result<?> execute(MicrogramOperation op) {
		return null;
	}
}
