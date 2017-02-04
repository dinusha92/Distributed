import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by kasun on 1/12/17.
 */
public class AppLauncher extends Thread {
    public static void main(String[] args) {
        final Random rand = new Random();
        int port1 = rand.nextInt(1000) + 2000;
        Node bootsTrap = new Node("127.0.0.1", 55555);
        Node currentNode = new Node("127.0.0.1", port1, "user123");
        final App app = new App(bootsTrap, currentNode);
        Thread thread1 = new Thread() {
            public void run() {
                try {
                    app.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


        Thread thread2 = new Thread() {
            public void run() {
                Scanner scan = new Scanner(System.in);
                int opt;
                String movie;

                while (true) {
                    System.out.println("\n---------------------");
                    System.out.println("\t" + currentNode.getIp() + " : " + currentNode.getPort());
                    System.out.println("---------------------");
                    System.out.println("1. Search a file");
                    System.out.println("2. Leave the network");
                    System.out.println("3. List movies");
                    System.out.println("4. List peers");
                    System.out.println("5. Get status");
                    System.out.println("6. Clear status");
                    System.out.println("---------------------");
                    try {
                        opt = Integer.parseInt(scan.nextLine().trim());
                    }catch (NumberFormatException e){
                        opt=-1;
                    }
                    switch (opt) {
                        case 1:
                            System.out.println("Enter movie name");
                            movie = scan.nextLine().trim().replace(" ", "_");
                            app.initiateSearch(movie);
                            break;
                        case 2:
                            app.disconnect();
                            break;
                        case 3:
                            app.echoMovies();
                            break;
                        case 4:
                            app.echoPeers();
                            break;
                        case 5:
                            app.getStats();
                            break;
                        case 6:
                            app.clearStats();
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

}