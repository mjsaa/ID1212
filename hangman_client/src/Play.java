import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
public class Play implements Runnable {
    private static final int PORT_NO = 5000;
    private static final int SERVER_PORT_NO = 8080;
    private static final int TIMEOUT_HALF_MIN = 30000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
	private static final int PACKET_SIZE = Integer.BYTES;
	private QueueHandler qhandler = new QueueHandler();
	PrintWriter toServer;
	public boolean connected = true;
	private void start() {
		new Thread(this).start();
		//new Thread(qhandler).start();
	}
	/*
	 * This thread processes*/    
	
	@Override
	public void run(){
		Socket socket;
		BufferedReader fromServer;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress("localhost", SERVER_PORT_NO), TIMEOUT_HALF_MIN);
	        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
	        boolean autoFlush = true;
	        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        toServer = new PrintWriter(socket.getOutputStream(), autoFlush);
	        Scanner inScan=new Scanner(System.in);
	        WriteIncomingData wid=new WriteIncomingData(fromServer);
	        Thread t1=new Thread(wid);
	        t1.start();	
	        String in=" ";

	        while(connected){
	        	synchronized(wid){
	        		try {
	        			wid.wait();	
	        		} catch (InterruptedException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}	        		
	        	}
	        	if(wid.stop){
	        		connected=false;
	        		System.out.println("closing the socket and the scan");
	        		socket.close();
	        		inScan.close();
	        	}
	        	else
	        	{
	        		in=inScan.nextLine();
	        		SendDataFromPlayer data=new SendDataFromPlayer(in, toServer);
	        		new Thread(data).start();
	        		
	        		//sendToServer(in);
	        	}
	        	
	        }

	    } catch (IOException e1) {
			e1.printStackTrace();
		}
	}
/*	private void sayHelloToServer(){
		StringJoiner joiner=new StringJoiner("\t");
		joiner.add("hello");
		joiner.add("to");
		joiner.add("server");
		toServer.println(joiner.toString());
	}*/
	private void sendToServer(String in){
		StringJoiner joiner=new StringJoiner("");
		joiner.add(in);
		//joiner.toString();
		toServer.println(joiner.toString());
	}
	public static void main(String[] args) throws Exception {
		Play now=new Play();
		now.start();
	}

}
