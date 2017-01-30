import java.io.Serializable;


public class Query implements Serializable {
    
    private int hops;
    private Node sender;
    private Node origin;
    private String queryText;
    private long timestamp;
    private String sep = "@";

    public  Query(String encodedQuery){
        String [] str = encodedQuery.split(sep);
        hops = Integer.parseInt(str[0]);
        sender = new Node(str[1]);
        origin = new Node(str[2]);
        queryText = str[3];
        timestamp = Long.parseLong(str[4]);
    }

    public Query(){

    }

    public  String getEncodedQuery(){
        return  hops+ sep +sender.getEncodedNode()+ sep +origin.getEncodedNode()
                + sep +queryText+ sep +timestamp;
    }
    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }
    
    public Node getOrigin() {
        return origin;
    }

    public void setOrigin(Node origin) {
        this.origin = origin;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query qry = (Query) o;

        if (timestamp != qry.timestamp) return false;
        if (origin != null ? !origin.equals(qry.origin) : qry.origin != null) return false;
        return queryText != null ? queryText.equals(qry.queryText) : qry.queryText == null;

    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (queryText != null ? queryText.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
