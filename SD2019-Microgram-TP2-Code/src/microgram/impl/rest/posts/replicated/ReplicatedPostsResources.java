package microgram.impl.rest.posts.replicated;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.rest.RestPosts;
import microgram.impl.mongo.MongoPosts;
import microgram.impl.rest.RestResource;

public class ReplicatedPostsResources extends RestResource implements RestPosts {
	final Posts localDB;
	final PostsReplicator replicator;
	
	public ReplicatedPostsResources() {
		this.localDB = new MongoPosts();
		//this.localDB = new JavaPosts();
		this.replicator = null; //new PostsReplicator(localDB, new TotalOrderExecutor(MicrogramTopic.MicrogramEvents));
	}

	@Override
	public Post getPost(String postId) {
		return super.resultOrThrow( replicator.getPost( postId ));
	}

	@Override
	public void deletePost(String postId) {
		super.resultOrThrow( replicator.deletePost( postId ));
	}

	@Override
	public String createPost(Post post) {
		return super.resultOrThrow( replicator.createPost( post ));
	}

	@Override
	public boolean isLiked(String postId, String userId) {
		return super.resultOrThrow( replicator.isLiked(postId, userId));
	}

	@Override
	public void like(String postId, String userId, boolean isLiked) {
		super.resultOrThrow( replicator.like(postId, userId, isLiked ));
	}

	@Override
	public List<String> getPosts(String userId) {
		return super.resultOrThrow( replicator.getPosts(userId));
	}

	@Override
	public List<String> getFeed(String userId) {
		return super.resultOrThrow( replicator.getFeed(userId));
	}	
}
