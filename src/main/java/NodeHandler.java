import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by kasun on 1/12/17.
 */
public class NodeHandler {
    public static void main(String[] args) {
//        Node node = new Node("user123", "127.0.0.1",2228,"movies.txt");
//        try {
//            node.run();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            String msg="0024 SER 127.0.0.1 2227 Microsoft Office 2010 10";
            DatagramSocket socket = new DatagramSocket(2219);
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName("127.0.0.1"),2227);
            socket.send(packet);
        } catch (IOException e) {
            System.out.println( e);
        }
    }
}
