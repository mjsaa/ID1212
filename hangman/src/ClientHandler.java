import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.StringJoiner;

/**
 * Handles all communication with one particular chat client.
 */
class ClientHandler implements Runnable {
    private final Server server;
    private final Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private boolean connected;
    private String alreadyJoined="";

    /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientSocket The socket to which this handler's client is connected.
     */
    ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected=true;
    }
    @Override
    public void run() {
    	Game game=new Game();//we know we return since the line senGameHeader runs.
        try {
            boolean autoFlush = true;
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
            System.out.println("this pring should be empty"+this.alreadyJoined);
            joinInstructionToBreakConnection();
            System.out.println("after joining instr"+this.alreadyJoined);
            joinGameHeader();
            System.out.println("after joinGameHeader"+this.alreadyJoined);

        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        joinUpdate(game);
        System.out.println("before send"+this.alreadyJoined);
        send();
        while(connected){
        	try {
        		String inMsg=fromClient.readLine();
        		game.process(inMsg.charAt(0));
        		System.out.println(inMsg);
        		System.out.println(game.MAX_SCORE);
        		if(inMsg.charAt(0)=='#'){
        			System.out.println("request to quit");
        			disconnectClient();
        		}
        		else{
        			if(game.score==game.MAX_SCORE){
        				System.out.println("client won");
        				alreadyJoined=joinGameHeader();
        				joinUpdate(game);
        				joinYouWon();
        				send();
        			}
        			else if(game.remained_attempts==0){
        				System.out.println("client lost");
        				alreadyJoined=joinGameHeader();
        				joinUpdate(game);
        				joinYouLost();
        				send();
        			}else
        			{
        				System.out.println("Don't quit");
        				this.alreadyJoined=joinGameHeader();
        				joinUpdate(game);
        				send();
					}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	  
      }
    }
    private String joinInstructionToBreakConnection(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);
    	joiner.add("To break the connection, send #");
    	return this.alreadyJoined=joiner.toString();
    }
    private String joinYouWon(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);
    	joiner.add("YOU  WON");
    	return this.alreadyJoined=joiner.toString();
    }
    private String joinYouLost(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(alreadyJoined);
    	joiner.add("YOU LOST");
    	return this.alreadyJoined=joiner.toString();
    }
    private String joinGameHeader(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(alreadyJoined);
    	joiner.add("Action\t"+"Word\t"+"Score\t"+"Remained_tries\t"+"Chosen_word\t");
    	return this.alreadyJoined=joiner.toString();
    }
    private String joinUpdate(Game game){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);//first we add we already have 
    	joiner.add(game.print());//second we add more
    	System.out.println(game.print());//just to print it out in the server also
    	return this.alreadyJoined=joiner.toString();//alreadyJoined gets updated.
    }
    private void send(){
    	toClient.println(this.alreadyJoined);
    	toClient.flush();
    	alreadyJoined="";
    	
    }
    private void notifyClose(){
    	toClient.println(101);
    }
    private void disconnectClient() {
        try {
        	notifyClose();
            clientSocket.close();
            System.out.println("client socket closed");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
    }

    private static class Message{
    	boolean quit;
    	char receivedChar;
    	private Message(String receivedMsg){
    		System.out.println(receivedMsg);
    		 receivedChar=receivedMsg.charAt(0);
    	}
    	private boolean quit(char receivedChar){
    		boolean quit=false;
    		if(receivedChar=='#')quit=true;
    		return quit;
    	}
    }
 }

