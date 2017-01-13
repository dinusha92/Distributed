import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by kasun on 1/12/17.
 */
public class NodeHandler extends Thread{
    public static void main(String[] args) {
        final Random  rand = new Random();
        int port1 = rand.nextInt(1000) + 2000;

        final Node node = new Node("user123", "127.0.0.1", port1, "movies.txt");
        Thread thread1 = new Thread(){
            public void run(){
                try {
                    node.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


         Thread thread2 = new Thread(){
             public void run(){
                 int hopcount = 10;
                 Scanner scan = new Scanner(System.in);
                 int opt;
                 String movie;
                 DatagramSocket socket = null;
                 int port2 = 0;
                 boolean  succuess = true;
                 while(succuess) {
                     try {
                         port2 = rand.nextInt(1000) + 3000;
                         socket = new DatagramSocket(port2);
                         succuess = false;
                     } catch (SocketException e) {

                     }
                 }
                 while (true) {
                     System.out.println("---------------------");
                     System.out.println("1. Search a file");
                     System.out.println("2. Leave the network");
                     opt = Integer.parseInt(scan.nextLine().trim());
                     int currenthops = 0;
                     switch (opt) {
                         case 1:
                             try {
                                 System.out.println("Enter movie name");
                                 movie = scan.nextLine().trim();
                                 String msg="0024 SER "+ node.getMyIp()+" "+node.getMyPort()+" "+movie+" "+hopcount+" "+currenthops;
                                 msg =String.format("%4d",msg.length() + 4)  +msg;
                                 DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                                         InetAddress.getByName(node.getMyIp()),node.getMyPort());
                                 socket.send(packet);
                             } catch (IOException e) {
                                 System.out.println(e);
                             }
                             break;
                         case 2:
                             try {
                                 String msg=" LEAVE "+ node.getMyIp()+" "+node.getMyPort();
                                 msg =String.format("%4d",msg.length() + 4)  +msg;
                                 DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                                         InetAddress.getByName(node.getMyIp()),node.getMyPort());
                                 socket.send(packet);
                             } catch (IOException e) {
                                 System.out.println(e);
                             }
                             break;
                         default:
                             System.out.println("Input error");
                             break;
                     }
                 }
             }
         };
         thread1.start();
         thread2.start();

    }

//    public void search() {
//
//        try {
//            String msg="0024 SER 127.0.0.1 2227 8 5 0";
//            DatagramSocket socket = new DatagramSocket(2219);
//            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
//                    InetAddress.getByName("127.0.0.1"),2227);
//            socket.send(packet);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }

}



