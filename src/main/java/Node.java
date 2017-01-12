import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Node {


    private  Neighbour predecessor, successor;
    private ArrayList<Neighbour> finger = new ArrayList<Neighbour>();
    private  DatagramSocket socket;
    private  MovieHandler movieHandler;
    private  String myIp="127.0.0.1";
    private    int myPort  = 2223;
    private   String myUserName = "dingi123";

     DecimalFormat formatter = new DecimalFormat("0000");

    public Node (String fileName){
        movieHandler = new MovieHandler(fileName);
    }

    public Node (String userName, String ip, int port){
        myIp=ip;
        myPort= port;
        myUserName= userName;
    }
    //length PredecessorJOIN IP_address port_no
    private  String Register(Neighbour node){
        String ip = node.getIp();
        int port  = node.getPort();
        String username = node.getUsername();

        String msg = Command.REG+" " + ip + " " + port + " " + username;
        String length_final = formatter.format(msg.length() + 5);
        return length_final + " " + msg;

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

    public  void run() throws IOException {

        System.out.println("my ip and port = "+myIp+" : "+myPort);
        boolean done = true;
        while(true) {
            if(done) {
                socket = new DatagramSocket(myPort);
                Neighbour neigh = new Neighbour(myIp ,myPort, myUserName);
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
                System.out.println("receiving ; "+ response);
                onResponseReceived(response);



            }catch (IOException e){
                System.out.println("ddaee");
            }

            if(predecessor!=null){
                System.out.println("predecessor "+ predecessor.getIp() + " " + predecessor.getPort());
            }
            if(successor!=null){
                System.out.println("successor "+ successor.getIp()+ " "+ successor.getPort());
            }

        }
    }

    private  void onResponseReceived(Communicator response) {

        StringTokenizer tokenizer = new StringTokenizer(response.getMessage(), " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();
        String ip;
        int port;
        if (Command.REGOK.equals(command)) {
            int no_nodes = Integer.parseInt(tokenizer.nextToken());

            switch (no_nodes) {
                case 0:

                    break;

                case 1:
                    ip = tokenizer.nextToken();
                    port = Integer.parseInt(tokenizer.nextToken());
                    predecessorConnect(new Neighbour(ip,port,""));
                    successorConnect(new Neighbour(myIp,myPort,""),new Neighbour(ip,port,""));
                    break;

                case 2:
                    Random rnd= new Random();

                    //select random node from the given two nodes
                    if(rnd.nextInt()%2==0){     //randomly consume one ip and port
                        tokenizer.nextToken();
                        Integer.parseInt(tokenizer.nextToken());
                    }
                    ip = tokenizer.nextToken();
                    port = Integer.parseInt(tokenizer.nextToken());

                    predecessorConnect(new Neighbour(ip,port,""));
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
        } else if (Command.PredecessorJOIN.equals(command)) {
            ip = tokenizer.nextToken();
            port = Integer.parseInt(tokenizer.nextToken());
            System.out.println("details" + ip + " " + port);

            if(successor!=null){
                successorConnect(new Neighbour(ip,port,""),successor);
            }
            successor = new Neighbour(ip,port,"");
            String reply = "0014 "+Command.PredecessorJOINOK+" 0";
            send(new Communicator(ip,port,reply));
        } else if (Command.PredecessorJOINOK.equals(command)) {

            int value = Integer.parseInt(tokenizer.nextToken());
            if(value == 0){
                System.out.println("PredecessorJOIN Successful");
            }else {
                
                System.out.println("error");
            }
        } else if (Command.SuccessorJOIN.equals(command)) {
            ip = tokenizer.nextToken();
            port = Integer.parseInt(tokenizer.nextToken());

            predecessorConnect(new Neighbour(ip,port,""));
        } else if (Command.SuccessorJOINOK.equals(command)) {

            int value = Integer.parseInt(tokenizer.nextToken());
            if(value == 0){
                System.out.println("PredecessorJOIN Successful");
            }else {

                System.out.println("error");
            }
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

    private  void predecessorConnect(Neighbour receiver){
        String ip = receiver.getIp();
        int port = receiver.getPort();
        String reply = " "+Command.PredecessorJOIN +" " + myIp + " " + myPort;
        predecessor = new Neighbour(ip,port,"");
        String length_final = formatter.format(reply.length() + 4);
        String final_reply = length_final  + reply;;
        send(new Communicator(receiver.getIp(),receiver.getPort(),final_reply));
    }

    private  void successorConnect(Neighbour neighbour, Neighbour receiver){
        String reply = " "+Command.SuccessorJOIN+" " + neighbour.getIp() + " " + neighbour.getPort();

        String length_final = formatter.format(reply.length() + 4);
        String final_reply = length_final  + reply;;
        send(new Communicator(receiver.getIp(),receiver.getPort(),final_reply));
    }


}
