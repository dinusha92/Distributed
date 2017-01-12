import java.io.IOException;

/**
 * Created by kasun on 1/12/17.
 */
public class NodeHandler {
    public static void main(String[] args) {
        Node node = new Node("user12", "127.0.0.1",2225);
        try {
            node.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
