/**
 * Created with IntelliJ IDEA.
 * User: diptopol
 * Date: 6/17/13
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String args[]) {

        HTTPServer httpServer = new HTTPServer(4321);
        Thread serverThread = new Thread(httpServer);
        /*Thread clientFirstThread = new Thread(new Client(1));
        Thread clientSecondThread = new Thread(new Client(2));
        Thread clientThirdThread = new Thread(new Client(3));
        Thread clientFourthThread = new Thread(new Client(4));*/

        serverThread.start();
        /*clientFirstThread.start();
        clientSecondThread.start();
        clientThirdThread.start();
        clientFourthThread.start();*/
        try {
            serverThread.join();
            /*clientFirstThread.join();
            clientSecondThread.join();
            clientThirdThread.join();
            clientFourthThread.join();*/
        } catch (InterruptedException ex) {
            System.out.println("joining failed");
        }


        System.out.println("Main done");
    }
}
