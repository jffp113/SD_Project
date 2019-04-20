package microgram.impl.srv.java.Particionated;

import microgram.impl.srv.java.ServerInstantiator;

public abstract class JavaParticionated {


    protected int serverId;
    protected ServerInstantiator si = new ServerInstantiator();

    protected JavaParticionated(int serverId){
        this.serverId = serverId;
    }

    protected int calculateResourceLocation(String postId){
        int numPostServers = this.si.getNumPostsServers();
        return Math.abs(postId.hashCode()) % numPostServers;
    }

    protected int calculateServerLocation(){
        int numPostServers = this.si.getNumPostsServers();
        return ((this.serverId % numPostServers) + (numPostServers - 1))% numPostServers;
    }

}
