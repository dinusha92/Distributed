import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Node {


    private Neighbour predecessor, successor;
    private ArrayList<Neighbour> finger = new ArrayList<Neighbour>();
    public static DatagramSocket socket;
    private static MovieHandler movieHandler;

    static DecimalFormat formatter = new DecimalFormat("0000");

    public Node (String fileName){
        movieHandler = new MovieHandler(fileName);
    }

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



    public static void send(Communicator request) {
        System.out.println(request);
        try {
            DatagramPacket packet = new DatagramPacket(request.getMessage().getBytes(), request.getMessage().getBytes().length,
                    InetAddress.getByName(request.getIp()), request.getPort());
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
                send(new Communicator("127.0.0.1", 55555,reply));
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

        System.out.println(response);
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
            String sourceIP = tokenizer.nextToken();
            int sourcePort = Integer.parseInt(tokenizer.nextToken());
            int hops = 0;
            StringBuilder queryBuilder = new StringBuilder();
            for (int i = 1; i < tokenizer.countTokens(); i++) {
                queryBuilder.append(tokenizer.nextToken());
                queryBuilder.append(' ');
            }
            String hopsToken = tokenizer.nextToken();
            try {
                //Check if hops are added in request
                hops = Integer.parseInt(hopsToken);
            } catch (NumberFormatException e) {
                queryBuilder.append(hopsToken);
            }
            String fileName = queryBuilder.toString().trim();
            List<String> moviesResult = movieHandler.searchMovies(fileName);
            hops++;
            //ToDo: Need to change all static methods to non static and complete join and pred, successor assignment
            /*String resultString = "0114 SEROK " + results.size() + " 127.0.0.1 " + port + " " + hops;
            for (int i = 0; i < moviesResult.size(); i++) {
                resultString += " " + moviesResult.get(i);
            }
            send(resultString, sourceIP, sourcePort);

            // Pass the message to neighbours
            Neighbour sender = new Neighbour(senderIP, senderPort);
            if (sender.equals() && right != null) {
                // Pass the message to RIGHT
                send(message, right.getIp(), right.getPort());
            } else if (sender.equals(right) && left != null) {
                // Pass the message to LEFT
                send(message, left.getIp(), left.getPort());
            }*/

        } else if (Command.SEROK.equals(command)) {

        } else if (Command.ERROR.equals(command)) {
        } else {
        }
    }

    private void createChordRing(){

    }

    private static void joinNode(Neighbour node){

    }

    private  static void stabilize(){

    }

    private static void notifyNodes(){

    }

    private static  void fix_fingers(){

    }

    private static void check_predecessor(){

    }

    private static void find_successor (){

    }

    private static void closest_preceding_node(){

    }

}
