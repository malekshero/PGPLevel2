package Client_Server;

import java.net.ServerSocket;
import java.net.Socket;

public class MultithreadedSocketServer {

    public static void main(String[] args) throws Exception {
        try {
            ServerSocket server = new ServerSocket(8888);
            int counter = 0;
            System.out.println("Server Started ....");
            while (true) {
                counter++;
                Socket serverClient = server.accept();  //server accept the client connection request
                System.out.println(" >> " + "Client No:" + counter + " started!");
                ServerThread sct = new ServerThread(serverClient, counter); //send  the request to a separate thread
                sct.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
