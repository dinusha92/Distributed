import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
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

        String msg = Command.REG+" " + ip + " " + port + " " + username;
        String length_final = formatter.format(msg.length() + 5);
        String reply = length_final + " " + msg;
        return reply;

    }

    public static void joinNode(Neighbour nodeA, Neighbour nodeB){

    }

    public static void send(Communicator request) {
        System.out.println(request);
        try {
            DatagramPacket packet = new DatagramPacket(request.getMessage().getBytes(), request.getMessage().getBytes().length,
                    InetAddress.getByName(request.getHost()), request.getPort());
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
                send(new Communicator("127.0.0.1", 55554,reply));
                done = false;
            }
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try{
                socket.receive(packet);

                byte[] data = packet.getData();
                String message = new String(data, 0, packet.getLength());

                Communicator response = new Communicator(packet.getAddress().getHostAddress(), packet.getPort(), message);
                onResponseReceived(response);


                System.out.println("dfdfdfsdfsd");
            }catch (IOException e){
                System.out.println("ddaee");
            }

        }
    }

    private static void onResponseReceived(Communicator response) {

        StringTokenizer tokenizer = new StringTokenizer(response.getMessage(), " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();
        if (Command.REGOK.equals(command)) {
            int no_nodes = Integer.parseInt(tokenizer.nextToken());

            switch (no_nodes) {
                case 0:

                    break;

                case 1:

                    break;

                case 2:

                    break;

                case 9996:
                    System.out.println("Failed to register. BootstrapServer is full.");
                    break;

                case 9997:
                    System.out.println("Failed to register. This ip and port is already used by another Node.");
                    break;

                case 9998:
                    System.out.println("You are already registered. Please unregister first.");
                    break;

                case 9999:
                    System.out.println("Error in the command. Please fix the error");
                    break;
            }

        } else if (Command.UNROK.equals(command)) {
            System.out.println("Successfully unregistered this node");
        } else if (Command.JOIN.equals(command)) {
        } else if (Command.JOINOK.equals(command)) {
        } else if (Command.LEAVE.equals(command)) {
        } else if (Command.LEAVEOK.equals(command)) {
        } else if (Command.DISCON.equals(command)) {

        } else if (Command.SER.equals(command)) {
        } else if (Command.SEROK.equals(command)) {

        } else if (Command.ERROR.equals(command)) {
        } else {
        }
    }

}
