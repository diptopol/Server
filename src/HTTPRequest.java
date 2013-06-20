import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: diptopol
 * Date: 6/17/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequest implements Runnable{

    private Socket socket;
    private File homeDirectory;
    private static final Pattern GET_REQUEST_PATTERN = Pattern.compile("^GET (/.*) HTTP/1.[01]$");
    private static final Pattern POST_REQUEST_PATTERN = Pattern.compile("^POST (/.*) HTTP/1.[01]$");
    private static final Pattern POST_FIELDNAME_PATTERN = Pattern.compile("(?<=name=\")(.*?)(?=\")");

    @Override
    public void run() {
        try{

            String line = getInputFromSocket();

            writeOutputToSocket(line);

            System.out.println("Server:Socket is done");
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        finally {
             try {
                socket.close();
             }
             catch (IOException ioException) {
                 ioException.printStackTrace();
             }
        }



    }

    public HTTPRequest(Socket socket, File homeDirectory) {

        this.socket = socket;
        this.homeDirectory = homeDirectory;

    }

    private String getInputFromSocket() throws IOException{

        System.out.println("Server: creating streams");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Server: stream done");
        System.out.println("Server: reading the request");
        String line;


        line = bufferedReader.readLine();


        if(line.contains("GET")) {

            Matcher getMatcher = GET_REQUEST_PATTERN.matcher(line);
            String getLine = getMatcher.matches() ? getMatcher.group(1) : null;

            System.out.println("Server: client said-> "+ line);
            return getLine;
        }
        else if(line.contains("POST")) {

            Matcher postMatcher = POST_REQUEST_PATTERN.matcher(line);
            String postLine = postMatcher.matches() ? postMatcher.group(1) : null;

            System.out.println("Server: checking Content Length");
            int contentLength=0;

            while(!(line=bufferedReader.readLine()).equals("")) {
                if(line.contains("Content-Length:"))
                    contentLength = Integer.decode(line.substring(15).trim());

            }

            System.out.println("Server: "+contentLength);


            int characterInt;
            int count=0;

            StringBuilder postDataBuilder = new StringBuilder();
            while ((characterInt = bufferedReader.read()) != -1) {
                count++;
                char character = (char) characterInt;
                postDataBuilder.append(character);
                if(count == contentLength) {
                    break;
                }
            }

            // parse fname and age from stringBuilder
            Matcher fieldMatcher = POST_FIELDNAME_PATTERN.matcher(postDataBuilder.toString());
            while(fieldMatcher.find()) {
                String fieldName = fieldMatcher.group();
                String postData = postDataBuilder.toString();
                int startingIndex = postData.indexOf(fieldName)+fieldName.length()+5;
                int endingIndex = postData.indexOf("-",postData.indexOf(fieldName));
                String value = postData.substring(startingIndex,endingIndex).trim();


                System.out.println("Server: FieldName: "+fieldName+" FieldValue: "+value);
            }

            System.out.println("Server: Primary input is over");
            return postLine;
        }
        return null;

    }


    private void writeOutputToSocket(String line) throws IOException {


        if(line == null) {
            System.out.println("Server: line is null");
        }

        else {
            System.out.println(line);
            String path = line;

            if(path.equals("/")) {
                path = path+"index.html";

            }

            if(path == null) {
                sendErrorMessage(400,"Bad Request");
            }
            else {
                File file = new File(homeDirectory,path);
                if(!file.exists() && (!file.isFile() || !file.canRead())) {
                    sendErrorMessage(403,"Forbidden");
                }
                else if (!file.exists()) {
                    sendErrorMessage(404,"File not found");
                }
                else {
                    sendOutput(file);
                }

            }


        }
    }



    private void sendErrorMessage(int status, String message) throws IOException{
        System.out.println("Server: writing message");

        BufferedWriter bufferedWriter  = new BufferedWriter(new OutputStreamWriter(sendResponseHeaders(status,message,message.length()))) ;
        bufferedWriter.write((message+"\n error number :"+status));
        bufferedWriter.flush();

        System.out.println("Server: writting is done");
    }

    private void sendOutput(File file) throws IOException {

        OutputStream outputStream = sendResponseHeaders(200,"OK",file.length());
        /*OutputStream outputStream = socket.getOutputStream();*/

        InputStream readStream = new FileInputStream(file);

        byte [] dataInBytes = new byte[1024];
        int numberOfByteRead;
        while((numberOfByteRead = readStream.read(dataInBytes))>0) {
            outputStream.write(dataInBytes,0,numberOfByteRead);
        }
        outputStream.flush();

    }

    private OutputStream sendResponseHeaders(int status, String message,long len) throws IOException
    {
        StringBuffer response = new StringBuffer();
        response.append("HTTP/1.1 ");
        response.append(status).append(' ').append(message).append("\r\n");
        response.append("Content-Length: ").append(len).append("\r\n\r\n");
        OutputStream out = this.socket.getOutputStream();
        out.write(response.toString().getBytes());
        out.flush();
        return out;
    }


}
