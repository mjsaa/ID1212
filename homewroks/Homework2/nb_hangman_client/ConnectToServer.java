import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class ConnectToServer implements Runnable{
	  private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(200);
	    //private final List<CommunicationListener> listeners = new ArrayList<>();
	    private InetSocketAddress serverAddress;
	    private SocketChannel socketChannel;
	    private Selector selector;
	    private boolean connected;
	    private volatile boolean timeToSend = false;
	    private String clientData;
	    public static void main(String[] args){
	    	ConnectToServer c=new ConnectToServer();
	    	c.connect("localhost", 8080);
	    }
	    
	    @Override
	    public void run(){
	    	try {
				initConnection();
				initSelector();
				while (connected) {
	                if (timeToSend) {
	                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
	                    timeToSend = false;
	                }

	                selector.select();
	                for (SelectionKey key : selector.selectedKeys()) {
	                    selector.selectedKeys().remove(key);
	                    if (!key.isValid()) {
	                        continue;
	                    }
	                    if (key.isConnectable()) {
	                        completeConnection(key);
	                    } else if (key.isReadable()) {
	                        recvFromServer(key);
	                    } else if (key.isWritable()) {
	                        sendToServer(key);
	                    }
	                }
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            
	    }
	    
	    
	   private void initSelector() throws IOException {
	        this.selector = Selector.open();
	        this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
	    }

	    private void initConnection() throws IOException {
	        this.socketChannel = SocketChannel.open();
	        this.socketChannel.configureBlocking(false);
	        this.socketChannel.connect(serverAddress);
	        this.connected = true;
	    }
	    
	    public void connect(String host, int port) {
	        serverAddress = new InetSocketAddress(host, port);
	        new Thread(this).start();
	    }
	    private void completeConnection(SelectionKey key) throws IOException {
	    	socketChannel.finishConnect();
	    	key.interestOps(SelectionKey.OP_READ);
	    	try {
	    		InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
	    		//notifyConnectionDone(remoteAddress);
	    	} catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
	    		//notifyConnectionDone(serverAddress);
	    	}
	    }
	    private void recvFromServer(SelectionKey key) throws IOException {
	        msgFromServer.clear();
	        int numOfReadBytes = socketChannel.read(msgFromServer);
	        System.out.println("nubOfReadBytes:"+ numOfReadBytes);
	        String recvdString = extractMessageFromBuffer();
	        System.out.println("the message is:"+ recvdString);
	        Executor pool = ForkJoinPool.commonPool();
	        Scanner in=new Scanner(System.in);
	        clientData=in.nextLine();
	        timeToSend=true;
	        selector.wakeup();
	    }
	    
	    private String extractMessageFromBuffer() {
	        msgFromServer.flip();
	        byte[] bytes = new byte[msgFromServer.remaining()];
	        msgFromServer.get(bytes);
	        msgFromServer.clear();
	        System.out.println("msgfromserver remaingng: "+msgFromServer.remaining());
	        return new String(bytes);
	    }
	    
	    private void sendToServer(SelectionKey key) throws IOException {
	        ByteBuffer msg =ByteBuffer.allocateDirect(200);
	        byte [] data=clientData.getBytes();
	        msg.clear();
	        msg.put(data);
	        msg.flip();
	        socketChannel.write(msg);
	        key.interestOps(SelectionKey.OP_READ);
	        
	    }
	    
}
