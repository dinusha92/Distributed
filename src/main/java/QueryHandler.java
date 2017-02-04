import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryHandler {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        List<String> queryList = new ArrayList<>();
        List<String> ipList = new ArrayList<>();
        List<Integer> portList = new ArrayList<Integer>();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));
        nodeList.add(new Node("127.0.0.1",34343));

        String fileName = "Queries.txt";
        boolean stay = true;
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                queryList.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(queryList);
        try{
            int portNumber = 11111;
            socket = new DatagramSocket(portNumber);
            System.out.println("Query Handler Activated on port " + portNumber);
            int option = 0;
            String reply = null;
            int query_position =0;
            int node_position = 0;
            while(stay){
                System.out.println("-----------------Instruction--------------");
                System.out.println("1.Clear stat");
                System.out.println("9.Query next movie");
                System.out.println("3.Select node to send query: Default value 0");
                System.out.println("4.Get stat after 50 queries");
                System.out.println("5.Exit");
                System.out.println("CURRENT QUERY POSITION: "+ query_position);
                Scanner scan = new Scanner(System.in);
                option = scan.nextInt();
                int len = nodeList.size();

                switch (option){
                    case 1:
                        reply = "0010 CLEAR";
                        query_position = 0;
                        for(int i=0;i<len;i++) {
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, InetAddress.getByName(nodeList.get(i).getIp()), nodeList.get(i).getPort());
                            socket.send(dpReply);
                        }
                        break;
                    case 9:
                        String query_search = queryList.get(query_position);
                        query_position++;
                        reply = "0000 QUERY "+ query_search;
                        DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, InetAddress.getByName(nodeList.get(node_position).getIp()), nodeList.get(node_position).getPort());
                        socket.send(dpReply);
                        break;
                    case 3:
                        System.out.println("Current Node " + node_position);
                        System.out.println("Enter new node position");
                        Scanner scanner = new Scanner(System.in);
                        node_position = scanner.nextInt();
                        query_position=0;
                        System.out.println("Current node updated: "+ node_position);
                        break;
                    case 4:
                        reply = "0000 STAT";
                        String s = null;
                        for(int i=0;i<len;i++) {
                            DatagramPacket statReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, InetAddress.getByName(nodeList.get(i).getIp()), nodeList.get(i).getPort());
                            socket.send(statReply);

                            byte[] buffer = new byte[65536];
                            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                            socket.receive(incoming);

                            byte[] data = incoming.getData();
                            s = new String(data, 0, incoming.getLength());

                            //// TODO: 2/5/17 Implement of stat calculating after they recieved
                        }
                        break;
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
            socket.close();
        }catch (Exception e){

        }

    }
}
