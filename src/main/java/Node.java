import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;

public class Node {


    private Neighbour node_a;
    private Neighbour node_b;

    public static DatagramSocket socket;

    static DecimalFormat formatter = new DecimalFormat("0000");

    //length JOIN IP_address port_no
    public static String Register(Neighbour node){
        String ip = node.getIp();
        int port  = node.getPort();
        String username = node.getUsername();

        String msg = "REG " + ip + " " + port + " " + username;
        String length_final = formatter.format(msg.length() + 5);
        String reply = length_final + " " + msg;
        return reply;

    }

    public static void joinNode(Neighbour nodeA, Neighbour nodeB){

    }

    public static void send(String messsage, String ip, int port) {
        System.out.println("Sending " + messsage + " to " + ip + ":" + port);
        try {
            DatagramPacket packet = new DatagramPacket(messsage.getBytes(), messsage.getBytes().length,
                    InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.println( e);
        }
    }

    public static void main(String[] args) throws IOException {
        boolean done = true;
        while(true) {
            if(done) {
                socket = new DatagramSocket(2224);
                Neighbour neigh = new Neighbour("127.0.0.1", 2224, "123dinssq");
                String reply = Register(neigh);
                send(reply, "127.0.0.1", 55554);
                done = false;
            }
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try{
                socket.receive(incoming);
                System.out.println("dfdfdfsdfsd");
            }catch (IOException e){
                System.out.println("ddaee");
            }

            byte[] data = incoming.getData();
            String s = new String(data, 0, incoming.getLength());
            System.out.println(s);
        }
    }

    public static void Request() {

    }
}
