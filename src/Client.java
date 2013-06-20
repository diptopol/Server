import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: diptopol
 * Date: 6/17/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client extends Thread{


    private Socket client;
    private String threadName;
    private String method="GET";
    public Client(int id ) {
         threadName = "Client "+id;
    }

    @Override
    public void run() {
        try {
            System.out.println(threadName+": about to create socket");
            client = new Socket(InetAddress.getByName("0.0.0.0"),4321);     // port=4321
            System.out.println(threadName+": socket created");
            if(method == "GET") {
                writeOutputToSocket();
                System.out.println(threadName+": isclosed "+client.isClosed());
                readInputFromSocket();
            }
            else {
                String fileName="dipto";
                writeOutputToSocket(fileName);

            }



            System.out.println(threadName+": done");

        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        finally {
            try{
                client.close();

            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }



    private void writeOutputToSocket() throws IOException{

        System.out.println(threadName+": creating streams");

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(sendRequestHeaders("GET",new String("index.html").length())));
        System.out.println(threadName+": stream done");

        try{
            bufferedWriter.write("index.html");
            bufferedWriter.flush();
        }
        catch(IOException e){
            System.out.println(threadName+": requesting failed");
        }

        System.out.println(threadName+": requesting finished");
        System.out.println(threadName+": reading the respond");

         client.shutdownOutput();


    }

    private void writeOutputToSocket(String fileName) throws IOException {

        System.out.println(threadName+": creating streams");
        File postFile = new File(fileName);

        OutputStream outputStream = sendRequestHeaders("POST",postFile.length());
        System.out.println(threadName+": stream done");

        try{

            InputStream fileStream = new FileInputStream(postFile);
            byte [] dataInBytes = new byte[1024];
            int numberOfBytePost;
            while((numberOfBytePost = fileStream.read(dataInBytes))>0) {
                outputStream.write(dataInBytes,0,numberOfBytePost);
            }


            fileStream.close();
            outputStream.flush();
        }
        catch(IOException e){
            System.out.println(threadName+": requesting failed");
        }

        System.out.println(threadName+": requesting finished");
        System.out.println(threadName+": reading the respond");

        client.shutdownOutput();


    }


    private void readInputFromSocket() throws IOException{


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        // Read and display response.
        try{
            String line = null;
            while((line=bufferedReader.readLine())!=null) {
                System.out.println(threadName+": "+line);
            }


        }
        catch(IOException e){
            System.out.println(threadName+": reading failed");
        }
        System.out.println(threadName+": reading fished");

        System.out.println(threadName+": is finishing");

    }

    private OutputStream sendRequestHeaders(String method, long length) throws IOException
    {
        StringBuffer response = new StringBuffer();
        response.append(method+" /index.html HTTP/1.1\r\n");

        response.append("Content-Length:").append(length).append("\r\n\r\n");
        OutputStream out = this.client.getOutputStream();
        out.write(response.toString().getBytes());
        out.flush();
        return out;
    }
}
