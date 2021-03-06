
import java.io.Serializable;

public class Node implements Serializable {
    private String ip;
    private int port;
    private String username;
    private String sep="#";

    public Node() {
    }

    public Node(String encodedString) {
        String [] str= encodedString.split(sep);
        ip = str[0];
        port = Integer.parseInt(str[1]);
        username = str[2];
    }
    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Node(String ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String url() {
        return String.format("http://%s:%d/", ip, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return port == node.port&&ip.equals(node.ip);

    }

    String getEncodedNode(){
        return ip+sep+port+sep+username;
    }

    @Override
    public int hashCode() {
        int result = ip.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return username + " @ " + ip + ":" + port;
    }
}
