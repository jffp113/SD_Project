package microgram.impl.srv.java.Particionated;

public class Node<E> implements Comparable<Node>{

    String key;
    Integer value;
    E client;

    public Node(String key, Integer value, E client) {
        this.key = key;
        this.value = value;
        this.client = client;
    }


    @Override
    public int compareTo(Node o) {
        int x = value.compareTo(o.value);
        if(x == 0)
            return key.compareTo(o.key);

        return x;
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(((Node)obj).value) && key.equals(((Node)obj).value);
    }
}