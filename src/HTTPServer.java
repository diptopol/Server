import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: diptopol
 * Date: 6/17/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServer implements Runnable{


     private int port;
     private File directory;
     private ExecutorService executorService;

    public HTTPServer(int port ) {

        directory = new File("localhost");
        this.port = port;
        executorService = Executors.newFixedThreadPool(2);

        if(!directory.exists() && !directory.isDirectory()) {

            System.out.println("home directory does not exists");

        }

    }

    public void run() {
        ServerSocket serverSocket;
        try {
            System.out.println("Server: is about to create socket");
            serverSocket = new ServerSocket(port);
            System.out.println("Server: socket created");
            boolean flag=true;
            while(flag==true) {
               /*flag=false;*/
               System.out.println("Server: is listening");
               Socket socket = serverSocket.accept();
               System.out.println("Server: Connection Established");
               HTTPRequest httpRequest = new HTTPRequest(socket,directory);


               // use threadPool

               Runnable serverThread = new Thread(httpRequest);
               executorService.execute(serverThread);

            }
            System.out.println("Server Socket: is done");
            serverSocket.close();

        }
        catch(IOException ioException) {
             ioException.printStackTrace();
        }

    }

}
