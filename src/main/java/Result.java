import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class Result implements Serializable {
    private Node owner;
    private List<String> movies;
    private int hops;
    private long timestamp;
    private String sep = "@";

    public Result(String encodedResult){
        String[] str = encodedResult.split(sep);
        owner = new Node(str[0]);
        movies = Arrays.asList(str[1].split(","));
        if(movies.get(0).equals(""))
            movies = new ArrayList<>();
        hops = Integer.parseInt(str[2]);
        timestamp = Long.parseLong(str[3]);
    }

    public  Result(){

    }
    public String getEncodedResult(){
        String moviesStr="";
        for (String str: movies
             ) {
            moviesStr+=str+",";
        }
        if(moviesStr.length()>1)
        moviesStr=moviesStr.substring(0,moviesStr.length()-1);
        return owner.getEncodedNode()+sep+moviesStr+sep+hops+sep+timestamp;
    }
    public Node getOwner() {
        return owner;
    }

    public void setOwner(Node owner) {
        this.owner = owner;
    }

    public List<String> getMovies() {
        return movies;
    }

    public void setMovies(List<String> movies) {
        this.movies = movies;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
