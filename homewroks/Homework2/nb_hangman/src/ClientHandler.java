import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.StringJoiner;

/**
 * Handles all communication with one particular chat client.
 */
class ClientHandler implements Runnable {
    private final Server server;
    public final SocketChannel clientChannel;
    private final int MAX_length=8192;//the number 8192 is inspired from Leif's constant. However it may not be that necessary to have such a large number in the context of Hangman game. 
    private ByteBuffer fromClient=ByteBuffer.allocateDirect(MAX_length);
    private boolean connected; //not sure whether we need this one. probably not.
    public String alreadyJoined="";
    public String incomingData;
    Game game=new Game();

    /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientChannel The socket to which this handler's client is connected.
     */
    ClientHandler(Server server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
        this.connected=true;
    }
    @Override
    public void run(){
    	synchronized(this){
    		game.process(this.incomingData.charAt(0));
    		alreadyJoined="";
    		System.out.println(this.incomingData);
    		System.out.println(game.MAX_SCORE);
    		
    		if(this.incomingData.charAt(0)=='#'){
    			System.out.println("request to quit");
    			disconnectClient();//has to be updated
    			//this.server.prepareSending();
    			
    		}
    		else if(game.score==game.MAX_SCORE){
    			System.out.println("client won");
    			joinGameHeader();
    			joinUpdate(game);
    			joinYouWon();
    			//this.server.prepareSending();
    		}
    		else if(game.remained_attempts==0){
    			System.out.println("client lost");
    			joinGameHeader();
    			joinUpdate(game);
    			joinYouLost();
    			//this.server.prepareSending();
    			
    		}else
    		{
    			System.out.println("Don't quit");
    			//joinGameHeader();
    			joinUpdate(game);    			
    			//this.server.prepareSending();
    		}
    	}
		    
    }
    public String joinInstructionToBreakConnection(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);
    	joiner.add("To break the connection, send #");
    	return this.alreadyJoined=joiner.toString();
    }
    public String joinYouWon(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);
    	joiner.add("YOU  WON");
    	return this.alreadyJoined=joiner.toString();
    }
    public String joinYouLost(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(alreadyJoined);
    	joiner.add("YOU LOST");
    	return this.alreadyJoined=joiner.toString();
    }
    public String joinGameHeader(){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(alreadyJoined);
    	joiner.add("Action\t"+"Word\t"+"Score\t"+"Remained_tries\t"+"Chosen_word\t");
    	return this.alreadyJoined=joiner.toString();
    }
    public String joinUpdate(Game game){
    	StringJoiner joiner=new StringJoiner("\n");
    	joiner.add(this.alreadyJoined);//first we add we already have 
    	joiner.add(game.print());//second we add more
    	//System.out.println(game.print());//just to print it out in the server also
    	return this.alreadyJoined=joiner.toString();//alreadyJoined gets updated.
    }

    public void notifyClose(){
    	//toClient.println(101);
    }
    public void disconnectClient() {
        try {
        	notifyClose();
            clientChannel.close();
            System.out.println("client socket closed");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
    }

 }

