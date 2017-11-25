import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private static final int LINGER_TIME = 5000;
    private int portNo = 8080;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    public boolean timeToSend=false;
    
    
    public static void main(String[] args) {
        Server server= new Server();
        server.parseArguments(args);
        server.serve();
    }
	private void serve() {
        try {
            initSelector();
            initListeningSocketChannel();
            while(true){
            	
            	
            	selector.select(); //returns when an event occurs.
            	
            	//an iterator is bound with a specific object SelectionKey in this case.
            	//the function selectedKeys() returns an "set" data structure. This data structure is iterable.
            	Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            	
            	/* In this while-loop we take one item by a time in the iterator,
            	 * store it in a SelectionKey object
            	 * and remove it from the iterator.
            	 * Then we check what type of key we have stored (acceptable? i.e. new connection) (readable? client sent a msg) (writable? server is sending a msg)*/
            	while (iterator.hasNext()) {
            		SelectionKey key = iterator.next();
            		iterator.remove();
            		if (!key.isValid()) {
            			continue;
            		}
            		if (key.isAcceptable()) {
            			startHandler(key);
            		} else if (key.isReadable()) {
            			recvFromClient(key);
            		} else if (key.isWritable()) {
            			sendToClient(key);
            		}
            	}
            }   
            }
         catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
	
	

	
    private void initSelector() throws IOException {
        selector = Selector.open();
    }

    /* ***************************************
     * this method takes an ACCEPTABLE key and 
     * the same process as in prev homework. ServerSocketChannel receives the incoming connection and pass it to a client socket.
     * after this conncection the TCP connection is established
     * what is missed is that we need to start the game and send it to the client
     * ***************************************/
   private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept(); //returns directly (the benefit of non-blocked sockets)
        clientChannel.configureBlocking(false); 
        clientChannel.register(selector, SelectionKey.OP_WRITE, new ClientHandler(this, clientChannel)); //registers OP_WRITE which makes the selector wake again and isWritable will be true!
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME); //not sure what this does
    }
    
    private void initListeningSocketChannel() throws IOException {
        this.listeningSocketChannel = ServerSocketChannel.open(); //opens an unbound socket channel
        this.listeningSocketChannel.configureBlocking(false); //non-blocking mode
        this.listeningSocketChannel.bind(new InetSocketAddress(portNo)); //bind the channel socket
        this.listeningSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }
    
    public void sendToClient(SelectionKey key) throws IOException {
    	ClientHandler client = (ClientHandler) key.attachment();
    	client.alreadyJoined="";

    	if(client.game.score>=client.game.MAX_SCORE){
			System.out.println("client won");
			client.joinGameHeader();
			client.joinUpdate(client.game);
			client.joinYouWon();
    	}
    	else if(client.game.remained_attempts==0){
			System.out.println("client lost");
			client.joinGameHeader();
			client.joinUpdate(client.game);
			client.joinYouLost();
			
		}else
		{
			System.out.println("Don't quit");
			client.joinGameHeader();
			client.joinUpdate(client.game);    			
		}
    	ByteBuffer outDataBuf=ByteBuffer.allocateDirect(400);
    	System.out.println("this is the messages that will be inserted to the buffer:\n\n"+client.alreadyJoined+"\n\n end of the message");
    	byte [] byte_array_dataFromServer= client.alreadyJoined.getBytes();
    	outDataBuf.clear();
    	outDataBuf.put(byte_array_dataFromServer);
    	outDataBuf.flip();	
    	synchronized(client){
    		client.clientChannel.write(outDataBuf);
    		key.interestOps(SelectionKey.OP_READ);
    		System.out.println("interest is chaged to read.");

    	}
    	
    	client.alreadyJoined="";
    	 if (outDataBuf.hasRemaining()) {
    		 System.err.println("cannot send the data to the client");
         }
    }
    
    private void recvFromClient(SelectionKey key) throws IOException{
    	ClientHandler client = (ClientHandler) key.attachment();
    	ByteBuffer inDataBuf=ByteBuffer.allocateDirect(200);

    	int numOfReadBytes = client.clientChannel.read(inDataBuf);//read from channel and write to buffer    		
    	
        //the data in the buffer to String
        inDataBuf.flip();
        byte[] bytes = new byte[inDataBuf.remaining()];//why remaining not limit?
        inDataBuf.get(bytes);
        inDataBuf.clear();
        String inDataString =new String(bytes);
        client.incomingData=inDataString;
        ForkJoinPool.commonPool().execute(client);
        key.interestOps(SelectionKey.OP_WRITE);
        
        
    }

/**
 * This function converts the string argument parameter that represents the port number into an integer
 * */
    
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
