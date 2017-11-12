import java.io.PrintWriter;
import java.util.StringJoiner;

public class SendDataFromPlayer implements Runnable{
	private String data;
	private PrintWriter toServer;
	public SendDataFromPlayer(String data, PrintWriter toServer){
		this.data=data;
		this.toServer=toServer;
	}
	
	@Override
	public void run(){
		sendToServer(data);
		System.out.println("sent data to server");
	}
	private void sendToServer(String in){
		StringJoiner joiner=new StringJoiner("");
		joiner.add(in);
		//joiner.toString();
		toServer.println(joiner.toString());
	}
}
