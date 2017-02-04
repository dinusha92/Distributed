import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class App {

    private DatagramSocket socket;
    private Node thisNode, bootstrapServer;
    private final List<Node> neighbours = new ArrayList<>();
    private final List<Query> queryList = new ArrayList<>();
    private MovieHandler movieHandler = MovieHandler.getInstance("movies.txt");
    private int receivedMessages, sentMessages,unAnswerdMessages;
    private List<Integer> latencyArray = new ArrayList<>();
    private List<Integer> hopArray = new ArrayList<>();

    public App(Node bootstrapServer, Node currentNode) {
        thisNode = currentNode;
        this.bootstrapServer = bootstrapServer;
    }

    public void run() throws IOException {

        boolean done = true;
        while (true) {
            if (done) {
                socket = new DatagramSocket(thisNode.getPort());
                String reply = getRegisterString();
                send(new Communicator(bootstrapServer, reply));
                done = false;
            }
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);

                byte[] data = packet.getData();
                String message = new String(data, 0, packet.getLength());

//                System.out.println("receiving ; " + message);
                onResponseReceived(message);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //will be invoked when a response is received
    private void onResponseReceived(String messege) {

        receivedMessages++;
        StringTokenizer tokenizer = new StringTokenizer(messege, " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        if (Command.REGOK.equals(command)) {
            int no_nodes = Integer.parseInt(tokenizer.nextToken());

            switch (no_nodes) {
                case 0:
                    break;

                case 1:
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
                    sendJoin(nodeB, thisNode);
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
            System.out.println("Successfully unregistered this node from the boostrap server");

        } else if (Command.JOIN.equals(command)) {
            join(new Node(tokenizer.nextToken()));

        } else if (Command.JOINOK.equals(command)) {

        } else if (Command.LEAVE.equals(command)) {
            leave(new Node(tokenizer.nextToken()));
        } else if (Command.SER.equals(command)) {
            search(new Query(tokenizer.nextToken()));
        } else if (Command.SEROK.equals(command)) {

            Result result = new Result(tokenizer.nextToken());
            int moviesCount = result.getMovies().size();

            long latancy = (System.currentTimeMillis() - result.getTimestamp());
            String output = String.format("\nNumber of movies: %d\nMovies: %s\nHops: %d\nSender %s:%d\nLatency: %s ms",
                    moviesCount, result.getMovies().toString(), result.getHops(), result.getOwner().getIp(), result.getOwner().getPort(), latancy);

            latencyArray.add((int) latancy);
            echo(output);

        } else if (Command.ERROR.equals(command)) {
            System.out.println("Error");
            closeSocket();

        } else {
            unAnswerdMessages++;
        }
    }

    synchronized void initiateSearch(String name) {

        Query query = new Query();
        query.setOrigin(thisNode);
        query.setQueryText(name);
        query.setHops(0);
        query.setSender(thisNode);
        query.setTimestamp(System.currentTimeMillis());

        queryList.add(query);

        List<String> results = movieHandler.searchMoviesList(query.getQueryText());

        Result result = new Result();
        result.setOwner(thisNode);
        result.setMovies(results);
        result.setHops(0);
        result.setTimestamp(query.getTimestamp());


        for (Node peer : neighbours) {
            sendSearch(peer, query);
        }


        sendResults(query.getOrigin(), result);

    }

    synchronized private void search(Query query) {

        if (queryList.contains(query)) {
            unAnswerdMessages++;
            return;
        } else {
            queryList.add(query);
        }

        // Increase the number of hops by one
        query.setHops(query.getHops() + 1);
        query.setSender(thisNode);

        Node sender = query.getSender();

        List<String> results = movieHandler.searchMoviesList(query.getQueryText());

        Result result = new Result();
        result.setOwner(thisNode);
        result.setMovies(results);
        result.setHops(query.getHops());
        result.setTimestamp(query.getTimestamp());

        neighbours.stream().filter(peer -> !peer.equals(sender)).forEach(peer -> {
            sendSearch(peer, query);

        });
        sendResults(query.getOrigin(), result);
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

    private void join(Node node) {
        if (!neighbours.contains(node)) {
            neighbours.add(node);
        }
    }

    private void sendJoin(Node receiver, Node node) {
        send(new Communicator(receiver, " " + Command.JOIN + " " + node.getEncodedNode()));
    }

    private void sendLeave(Node receiver, Node node) {
        send(new Communicator(receiver, " " + Command.LEAVE + " " + node.getEncodedNode()));
    }

    private void sendSearch(Node receiver, Query query) {
        send(new Communicator(receiver, " " + Command.SER + " " + query.getEncodedQuery()));
    }

    private void sendResults(Node receiver, Result result) {
        send(new Communicator(receiver, " " + Command.SEROK + " " + result.getEncodedResult()));
    }

    private void leave(Node node) {
        neighbours.remove(node);
    }


    private String getRegisterString() {
        return Command.REG + " " + thisNode.getIp() + " " + thisNode.getPort() + " " + thisNode.getUsername();
    }


    private void send(Communicator request) {
//        System.out.println("***** sending ; " + request);
        try {
            DatagramPacket packet = new DatagramPacket(request.getMessage().getBytes(), request.getMessage().getBytes().length,
                    InetAddress.getByName(request.getIp()), request.getPort());
            socket.send(packet);
            sentMessages++;
        } catch (IOException e) {
            System.out.println(e);
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

    void echoPeers() {
        neighbours.forEach(System.out::println);
    }

    void echoMovies() {
        movieHandler.getSelectedMovies().forEach(System.out::println);
    }

    void getStats(){
        int min=0,max=0;
        double avg,sd=0;

        String out = "";

        out += "messages received \t: "+ receivedMessages +
                "\nmessages sent \t: "+sentMessages+
                "\nmessages answered \t: "+(receivedMessages-unAnswerdMessages)+
                "\nNode degree \t: "+neighbours.size();
        if(latencyArray.size()>0) {
            max = Collections.max(latencyArray);
            min = Collections.min(latencyArray);
            avg = latencyArray.stream().mapToLong(val -> val).average().getAsDouble();
            sd = getSD(latencyArray.toArray(), avg);
            out +=    "\nlatency SD \t: "+sd+
                    "\nlatency  max\t:"+max+
                    "\nlatency  count \t:"+latencyArray.size()+
                    "\nlatency  min\t:"+min;
        }
        echo(out);
    }

    void clearStats(){
        receivedMessages=0;
        sentMessages= 0;
        unAnswerdMessages = 0;
        latencyArray= new ArrayList<>();
    }

    double getSD(Object[] latency, double mean){
    double variance = 0, sd =0;
    double [] temp =  new double[latency.length];
        for (int i = 0; i < latency.length; i++) {
            temp[i] = (double)(Integer)latency[i] - mean;
            temp[i] = Math.pow(temp[i], 2.0); //to get the (x-average)……2
            variance += temp[i];
        }
        variance = variance / (latency.length-1); // sample variance
        sd = Math.sqrt(variance);
        return sd;
    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }
}
