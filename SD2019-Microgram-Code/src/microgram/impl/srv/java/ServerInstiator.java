/**
 * 
 */
package microgram.impl.srv.java;

import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;

/**
 *
 */
class ServerInstantiator {
	
	private Profiles[] profiles;
	private Media[] media;
	private Posts[] postClients;
	
	Media media() {
		if(media == null) {
			synchronized (this) {
				if(media == null) {
					try {
						this.media = ClientFactory.buildMedia();
					} catch (NoServersAvailableException e){
						this.media = null;
					}
				}
			}
		}
		return media[0];
	}
	
	Profiles profiles() {
		if(profiles == null) {
			synchronized (this) {
				if(profiles == null) {
					try{
					this.profiles = ClientFactory.buildProfile();
					} catch (NoServersAvailableException e){
						this.profiles = null;
					}
				}
			}
		}
		return profiles[0];
	}
	
	Posts posts() {
		if(postClients == null) {
			synchronized (this) {
				if(postClients == null) {
					try {
						this.postClients = ClientFactory.buildPosts();
					}catch (NoServersAvailableException e){
						this.postClients = null;
					}
				}
			}
		}
		return postClients[0];
	}

}
