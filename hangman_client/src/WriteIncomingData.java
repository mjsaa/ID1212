import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriteIncomingData implements Runnable {
	private final BlockingQueue<String> valuesToHandle = new LinkedBlockingQueue<>();
	private BufferedReader br;
	private PrintWriter pw;
	public volatile boolean stop=false;
	private Play play;
	private char [] cbuf=new char [200];
	private int nrOfChars;
	WriteIncomingData(BufferedReader br){
		this.br=br;
	}

	/*The following part of code is copied from the StackOverflow web forum*/
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	/*end of the copied part from StackOverflow forum*/
	@Override
	public void run(){
        while (!stop) {
        	try {
        		this.nrOfChars=br.read(cbuf);
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	/**/
			//input=br.readLine();
			for (int i=0; i<this.nrOfChars; i++){
				System.out.print(cbuf[i]);
			}
			
			if(cbuf[0]=='1'||(cbuf[nrOfChars-9]=='Y' && cbuf[nrOfChars-8]=='O' && cbuf[nrOfChars-7]=='U')){				
				stop=true;
				synchronized(this){
					this.notifyAll();	
				}
			}
			else{
				System.out.println("\nEnter Letter");
				synchronized(this){
					this.notifyAll();	
				}
			}
        }
	}

}
