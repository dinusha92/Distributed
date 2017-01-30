import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.*;

public class App {

    private  DatagramSocket socket;
    private  MovieHandler movieHandler;
//    private  DecimalFormat formatter = new DecimalFormat("0000");
    private Node thisNode,bootstrapServer;
    private final List<Node> neighbours = new ArrayList<>();
    private final List<Query> queryList = new ArrayList<>();

    public App( String bootstrapServerIp, int bootstrapServerPort, String ip, int port,String userName, String fileName){
        thisNode = new Node(ip,port,userName);
        movieHandler = new MovieHandler(fileName);
        bootstrapServer = new Node(bootstrapServerIp,bootstrapServerPort);
    }

    public  void run() throws IOException {

        System.out.println("my ip and port = "+ thisNode.getIp()+" : "+ thisNode.getPort());
        boolean done = true;
        while(true) {
            if(done) {
                socket = new DatagramSocket(thisNode.getPort());
                String reply = Register();
                send(new Communicator(bootstrapServer,reply));
                done = false;
            }
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try{
                socket.receive(packet);

                byte[] data = packet.getData();
                String message = new String(data, 0, packet.getLength());

//                Communicator response = new Communicator(new Node(packet.getAddress().getHostAddress(), packet.getPort()), message);
                System.out.println("receiving ; "+ message);
                onResponseReceived(message);

            }catch (IOException e){
                e.printStackTrace();
            }
            
        }
    }

    //will be invoked when a response is received
    private  void onResponseReceived(String messege) {

        StringTokenizer tokenizer = new StringTokenizer(messege, " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        if (Command.REGOK.equals(command)) {
            int no_nodes = Integer.parseInt(tokenizer.nextToken());

            switch (no_nodes) {
                case 0:

                    echo("First node registered");
                    break;

                case 1:
                    echo("Second node registered");
                    String ipAddress = tokenizer.nextToken();
                    int portNumber = Integer.parseInt(tokenizer.nextToken());


                    Node node = new Node(ipAddress, portNumber);
                    join(node);
                    sendJoin(node, new Node(thisNode.getIp(), thisNode.getPort()));
                    break;

                case 2:
                    List<Node> returnedNodes = new ArrayList<>();
                    for (int i = 0; i < no_nodes; i++) {
                        String host = tokenizer.nextToken();
                        String hostPost = tokenizer.nextToken();

                        Node temp = new Node(host, Integer.parseInt(hostPost));
                        returnedNodes.add(temp);
                    }

                    Collections.shuffle(returnedNodes);

                    Node nodeA = returnedNodes.get(0);
                    Node nodeB = returnedNodes.get(1);

                    join(nodeA);
                    sendJoin(nodeA, thisNode);

                    join(nodeB);
                    sendJoin(nodeB ,thisNode);
                    break;
                case 9996:
                    System.out.println("Failed to register. BootstrapServer is full.");
                    break;

                case 9997:
                    System.out.println("Failed to register. This ip and port is already used by another App.");
                    closeSocket();
                    break;

                case 9998:
                    System.out.println("You are already registered. Please unregister first.");
                    break;

                case 9999:
                    System.out.println("Error in the command. Please fix the error");
                    closeSocket();
                    break;
            }

        } else if (Command.UNROK.equals(command)) {
            System.out.println("Successfully unregistered this node");
            closeSocket();
            
        } else if (Command.JOIN.equals(command)) {
//            ip = tokenizer.nextToken();
//            port = Integer.parseInt(tokenizer.nextToken());
//            predecessorConnect(new Node(ip,port,""));
            join(new Node(tokenizer.nextToken()));

        } else if (Command.JOINOK.equals(command)) {

        } else if (Command.LEAVE.equals(command)) {
            //successorConnect(new Node(predecessor.getIp(),predecessor.getPort(),""),new Node(successor.getIp(),successor.getPort(),""));
            leave(new Node(tokenizer.nextToken()));
        }  else if (Command.DISCON.equals(command)) {
            unRegister();

        } else if (Command.SER.equals(command)) {
//            String sourceIP = tokenizer.nextToken();
//            int sourcePort = Integer.parseInt(tokenizer.nextToken());
//            int hopLimit = 0,hops=0;
//            StringBuilder queryBuilder = new StringBuilder();
//            int tokenCount=tokenizer.countTokens();
//            for (int i = 2; i < tokenCount; i++) {
//                queryBuilder.append(tokenizer.nextToken());
//                queryBuilder.append(' ');
//            }
//            String hopLimitToken = tokenizer.nextToken();
//            String hopsToken = tokenizer.nextToken();
//            try {
//                //Check if hops are added in request
//                hopLimit = Integer.parseInt(hopLimitToken);
//                hops = Integer.parseInt(hopsToken);
//            } catch (NumberFormatException e) {
//                queryBuilder.append(hopLimitToken);
//            }
//            String fileName = queryBuilder.toString().trim();
//            List<String> moviesResult = movieHandler.searchMoviesList(fileName);
//            hops++;
//            String resultString = "0114 "+Command.SEROK+" " + moviesResult.size() + " 127.0.0.1 " + myPort + " " + hops;
//            for (int i = 0; i < moviesResult.size(); i++) {
//                resultString += " " + moviesResult.get(i);
//            }
//
//            if(moviesResult.size() >0||hops>=hopLimit) {
//                send(new Communicator(sourceIP, sourcePort, resultString));
//            }
//            else
//            {
//                String msg= " "+Command.SER+" "+sourceIP+" "+sourcePort+" "+fileName+" "+hopLimit+" "+hops;
//                String length_final = formatter.format(msg.length() + 4);
//                String final_reply = length_final  + msg;
////                send(new Communicator(successor.getIp(), successor.getPort(),final_reply));
//            }

        } else if (Command.SEROK.equals(command)) {
//            int fileCount = Integer.parseInt(tokenizer.nextToken());
//
//            // Remove port and ip od origin
//            tokenizer.nextToken();
//            tokenizer.nextToken();
//
//            int hops = Integer.parseInt(tokenizer.nextToken());
//
//
//            if (fileCount == 0) {
//                System.out.println("No files found at " + response.getIp() + ":" + response.getPort());
//            }
//            if (fileCount == 1) {
//                System.out.println("1 file found at " + response.getIp() + ":" + response.getPort());
//                System.out.println("\t" + tokenizer.nextToken());
//            }
//            if (fileCount > 1) {
//                System.out.println(fileCount + " files found at " + response.getIp() + ":" + response.getPort());
//                for (int i = 0; i < fileCount; i++) {
//                    System.out.println("\t" + tokenizer.nextToken());
//                }
//            }
//            System.out.println("No. of hops = "+hops);
        } else if (Command.ERROR.equals(command)) {
            System.out.println("Error");
            closeSocket();

        } else {
        }
    }

    boolean disconnect() {

        final int peerSize = neighbours.size();
        for (int i = 0; i < peerSize; i++) {
            Node on = neighbours.get(i);
            if (on.equals(thisNode)) {
                continue;
            }
            for (int j = 0; j < peerSize; j++) {
                Node node = neighbours.get(j);
                if (i != j) {
                    sendJoin(on, node);
                }
            }
        }

        for (Node peer : neighbours) {
            sendLeave(peer, thisNode);
        }

        String message = String.format(" UNREG %s %d %s", thisNode.getIp(), thisNode.getPort(), thisNode.getUsername());
        message = String.format("%04d", (message.length() + 4)) + message;
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(bootstrapServer.getIp()), bootstrapServer.getPort());
            socket.send(packet);
            byte[] buffer = new byte[65536];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            byte[] data = response.getData();
            String msg = new String(data, 0, response.getLength());

            StringTokenizer tokenizer = new StringTokenizer(msg, " ");
            String length = tokenizer.nextToken();
            String command = tokenizer.nextToken();
            if (Command.UNROK.equals(command)) {
                this.thisNode = null;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    void join(Node node){
        if (!neighbours.contains(node)) {
            neighbours.add(node);
        }
        neighbours.forEach(System.out::println);
    }

    private void sendJoin(Node receiver, Node node){
        send(new Communicator(receiver," "+Command.JOIN+" "+node.getEncodedString()));
    }
    private void sendLeave(Node receiver, Node node){
        send(new Communicator(receiver," "+Command.LEAVE+" "+node.getEncodedString()));
    }

    private void leave(Node node) {
        neighbours.remove(node);
    }

//
//    //used to connect a node as current node predecessor
//    private  void predecessorConnect(Node receiver){
//        String ip = receiver.getIp();
//        int port = receiver.getPort();
//        String reply = " "+Command.PredecessorJOIN +" " + myIp + " " + myPort;
//        predecessor = new Node(ip,port,"");
//        String length_final = formatter.format(reply.length() + 4);
//        String final_reply = length_final  + reply;;
//        send(new Communicator(receiver.getIp(),receiver.getPort(),final_reply));
//    }
//
//    private  void successorConnect(Node Node, Node receiver){
//        String reply = " "+Command.JOIN +" " + Node.getIp() + " " + Node.getPort();
//
//        String length_final = formatter.format(reply.length() + 4);
//        String final_reply = length_final  + reply;
//        send(new Communicator(receiver.getIp(),receiver.getPort(),final_reply));
//    }

    private  String Register(){
        return Command.REG+" " + thisNode.getIp()  + " " + thisNode.getPort()  + " " + thisNode.getUsername();
    }

    private  String unRegister(){
        return Command.UNREG+" " + thisNode.getIp() + " " + thisNode.getPort() + " " + thisNode.getUsername();

    }

    private  void send(Communicator request) {
        System.out.println("***** sending ; "+request);
        try {
            DatagramPacket packet = new DatagramPacket(request.getMessage().getBytes(), request.getMessage().getBytes().length,
                    InetAddress.getByName(request.getIp()), request.getPort());
            socket.send(packet);
        } catch (IOException e) {
            System.out.println( e);
        }
    }

    public void closeSocket() {
        if (socket != null) {
            if (!socket.isClosed()) {
                socket.close();
                socket = null;
            }
        }
    }

    //simple function to echo data to terminal
    public static void echo(String msg)
    {
        System.out.println(msg);
    }
}
