/**
 * 
 */
package microgram.impl.srv.java;

//import java.util.function.Supplier;

import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;

/**
 * Finds the active servers.
 */
public class ServerInstantiator {
	
	/**
	 * Array containing the active Profiles servers, ordered by their URI hash. 
	 */
	private Profiles[] profiles;
	
	/**
	 * Array containing the active Media servers, ordered by their URI hash. 
	 */
	private Media[] media;
	
	/**
	 * Array containing the active Posts servers, ordered by their URI hash.
	 */
	private Posts[] postClients;
	
	/*private void findServers (E[] servers, Supplier<T> func) {
		synchronized (this) {
			if (servers == null) {
				try {
					servers = func();
				} catch (NoServersAvailableException e){
					servers = null;
				}
			}
		}
	}*/
	
	private void findServersMedia () {
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
	
	private void findServersPosts () {
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
	
	private void findServersProfiles () {
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


	public Media media(int index) {
		if(this.media == null) 
			this.findServersMedia();
		return this.media[index];
	}
	
	public Profiles profiles(int index) {
		if(this.profiles == null) 
			this.findServersProfiles();
		return this.profiles[index];
	}
	
	public Posts posts(int index) {
		if(this.postClients == null) 
			this.findServersPosts();
		return this.postClients[index];
	}
	
	public int getNumProfilesServers() {
		if (this.profiles == null)
			this.findServersProfiles();
		return this.profiles.length;
	}
	
	public int getNumPostsServers() {
		if (this.postClients == null)
			this.findServersProfiles();
		return this.postClients.length;
	}
	
	public int getNumMediaServers() {
		if (this.media == null)
			this.findServersProfiles();
		return this.media.length;
	}

}
