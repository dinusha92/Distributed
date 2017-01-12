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
    private  DatagramSocket socket;
    private  MovieHandler movieHandler;
    private  String myIp;
    private  int myPort;
    private  String myUserName;
    private  DecimalFormat formatter = new DecimalFormat("0000");

    public Node (String userName, String ip, int port,String fileName){
        myIp=ip;
        myPort= port;
        myUserName= userName;
        movieHandler = new MovieHandler(fileName);
    }

    public  void run() throws IOException {

        System.out.println("my ip and port = "+myIp+" : "+myPort);
        boolean done = true;
        while(true) {
            if(done) {
                socket = new DatagramSocket(myPort);
                String reply = Register();
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
                e.printStackTrace();
            }

            if(predecessor!=null){
                System.out.println("predecessor "+ predecessor.getIp() + " " + predecessor.getPort());
            }
            if(successor!=null){
                System.out.println("successor "+ successor.getIp()+ " "+ successor.getPort());
            }

        }
    }

    //will be invoked when a response is received
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

        } else if (Command.LEAVE.equals(command)) {
            successorConnect(new Neighbour(predecessor.getIp(),predecessor.getPort(),""),new Neighbour(successor.getIp(),successor.getPort(),""));

        } else if (Command.LEAVEOK.equals(command)) {

        } else if (Command.DISCON.equals(command)) {
            unRegister();

        } else if (Command.SER.equals(command)) {
            String sourceIP = tokenizer.nextToken();
            int sourcePort = Integer.parseInt(tokenizer.nextToken());
            int hopLimit = 0,hops=0;
            StringBuilder queryBuilder = new StringBuilder();
            int tokenCount=tokenizer.countTokens();
            for (int i = 2; i < tokenCount; i++) {
                queryBuilder.append(tokenizer.nextToken());
                queryBuilder.append(' ');
            }
            String hopLimitToken = tokenizer.nextToken();
            String hopsToken = tokenizer.nextToken();
            try {
                //Check if hops are added in request
                hopLimit = Integer.parseInt(hopLimitToken);
                hops = Integer.parseInt(hopsToken);
            } catch (NumberFormatException e) {
                queryBuilder.append(hopLimitToken);
            }
            String fileName = queryBuilder.toString().trim();
            List<String> moviesResult = movieHandler.searchMoviesList(fileName);
            hops++;
            String resultString = "0114 "+Command.SEROK+" " + moviesResult.size() + " 127.0.0.1 " + myPort + " " + hops;
            for (int i = 0; i < moviesResult.size(); i++) {
                resultString += " " + moviesResult.get(i);
            }

            if(moviesResult.size() >0||hops>=hopLimit) {
                send(new Communicator(sourceIP, sourcePort, resultString));
            }
            else
            {
                String msg= " "+Command.SER+" "+sourceIP+" "+sourcePort+" "+fileName+" "+hopLimit+" "+hops;
                String length_final = formatter.format(msg.length() + 4);
                String final_reply = length_final  + msg;
                send(new Communicator(successor.getIp(), successor.getPort(),final_reply));
            }

        } else if (Command.SEROK.equals(command)) {
            int fileCount = Integer.parseInt(tokenizer.nextToken());

            // Remove port and ip od origin
            tokenizer.nextToken();
            tokenizer.nextToken();

            int hops = Integer.parseInt(tokenizer.nextToken());


            if (fileCount == 0) {
                System.out.println("No files found at " + response.getIp() + ":" + response.getPort());
            }
            if (fileCount == 1) {
                System.out.println("1 file found at " + response.getIp() + ":" + response.getPort());
                System.out.println("\t" + tokenizer.nextToken());
            }
            if (fileCount > 1) {
                System.out.println(fileCount + " files found at " + response.getIp() + ":" + response.getPort());
                for (int i = 0; i < fileCount; i++) {
                    System.out.println("\t" + tokenizer.nextToken());
                }
            }
            System.out.println("No. of hops = "+hops);
        } else if (Command.ERROR.equals(command)) {
        } else {
        }
    }

    //used to connect a node as current node predecessor
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
        String final_reply = length_final  + reply;
        send(new Communicator(receiver.getIp(),receiver.getPort(),final_reply));
    }

    private  String Register(){
        String msg = Command.REG+" " + myIp + " " + myPort + " " + myUserName;
        String length_final = formatter.format(msg.length() + 5);
        return length_final + " " + msg;
    }

    private  String unRegister(){
        String msg = Command.UNREG+" " + myIp + " " + myPort + " " + myUserName;
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
}
