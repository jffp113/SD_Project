package discovery;

public class DiscoveryConfiguration {
    public static final int DEFAULT = 1;

    public static int numberOfPostsServers = DEFAULT;
    public static int numberOfMediaServes = DEFAULT;
    public static int numberOfProfilesServers = DEFAULT;


    public static void setArgs(String[] args){
        for(int i = 0; i < args.length; i +=2){
            System.out.println(args[i] + " " + args[i+1]);
            if(args[i].equals("-profiles")){
                System.out.println("profiles=" + args[i+1]);
                numberOfProfilesServers = Integer.parseInt(args[i+1]);
            }
            else if(args[i].equals("-posts")){
                System.out.println("posts=" + args[i+1]);
                numberOfPostsServers = Integer.parseInt(args[i+1]);
            }
        }
    }

}
