import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.SocketException;

public class Server {
    private static final int LINGER_TIME = 5000;
    private int portNo = 8080;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
 //   private final List<ClientHandler> clients = new ArrayList<>();
    
    public static void main(String[] args) {
        Server server= new Server();
        server.parseArguments(args);
        server.serve();
    }
	private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
            	System.out.println("will listen and accept a new client");
                Socket clientSocket = listeningSocket.accept();//note that listeningSocket is a ServerSocket.
                process_client(clientSocket);
                }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
	
	
	/*
	 * When a new client establishes a connection, this method runs.
	 * The method creates a new thread which takes an object, which implements Runnable, as parameter argument.
	 * 
	 * */
	private void process_client(Socket clientSocket){
		ClientHandler handler=new ClientHandler(this, clientSocket);//in this class we have a runnable method
		Thread clientThread=new Thread(handler);

		clientThread.start();

	}


/*
 * This function converts the string argument parameter that represents the port number into an integer */
	 private void parseArguments(String[] arguments) {
	        if (arguments.length > 0) {
	            try {
	                portNo = Integer.parseInt(arguments[1]);
	            } catch (NumberFormatException e) {
	                System.err.println("Invalid port number, using default.");
	            }
	        }
	    }
}
