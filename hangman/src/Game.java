import java.io.*;
import java.util.*;
public class Game {
	public String word;
	public String wordToPrint="";
	public int remained_attempts;
	List<Character> hidden_chars=new ArrayList<Character>();
	List<Character> revealed_chars=new ArrayList<Character>();
//	public char [] hidden_chars;//change to list
//	public char [] revealed_chars;//change to list
	public int score;
	public String action;
	private static Scanner scanFile;
	static String [] words;
	public final int MAX_SCORE;
	List<Guess> guesses = new ArrayList<Guess>();
	//public connection
	public Game(){
		openFile(); //opens a file
		this.word=chooseWordFromFile(); //set word for the game to the one chosen from file
		this.score=0;
		wordChars();//Adds all chars of the word to the list hidden_chars
		MAX_SCORE=countUniqueChars(this.word);
		this.remained_attempts=word.length();
		this.action="game started";
		//this.connection
		//print header
		start();
	}
	
	private void start(){
		System.out.println("Action \t Word \t Remaind_tries \t Score \t Word chosen");
		//System.out.println("We start by printing the word alone"+ print_word());
		
	//	while(this.remained_attempts>0 && !this.hidden_chars.isEmpty()){
		

	}
	public String print(){
		String nRow=this.action+"\t"+print_word()+"\t"+this.score+"\t"+this.remained_attempts+"\t"+"\t\t"+this.word+"\n"+"missed: "+getMissed(this.guesses)+"\n";
		//String nRow=this.action+"\t"+print_word()+"\t"+this.score+"\t"+this.remained_attempts+"\t"+"\t\t"+"\n"+"missed: "+getMissed(this.guesses)+"\n";
		return nRow;
	}
	/*Today: Read from System.in
	 * To be modified: Read from queue*/
	private static char new_attempt(){
		Scanner input=new Scanner(System.in);
		char attempt_char=input.next().charAt(0);
		return attempt_char;
	}
	
	/*The data (letter) that has been read will be processed.
	 * The processing is in the same way as if the client was local host.
	 * 1. Check if an identical guess already exist.
	 * 		a. if yes: return
	 * 		b. if no:
	 * 			i-Add a (List) of objects in class Guess.
	 * 			ii-modify score, wrong tries etc.
	 * */
	public void process(char input){
		/*why creating list of chars? to see if the input char is wrong or right*/

		boolean exists=hidden_chars.contains(input);//see if input letter is one in the word.
		Guess g=new Guess(input, exists);
		if(contains(input))
			System.out.println("Have already tried this one.");
		else{
			this.guesses.add(g);//add to list of guesses.
			//examine if it is a fail or pass try.
			if(exists){
				process_right_answer(input);				
			}
			else{
				process_wrong_answer(input);
			}
		}
	}
	private void process_wrong_answer(char input){
		this.remained_attempts--;
		this.action="guess "+input;
	}
	private void process_right_answer(char input){
		this.score++;
		this.action="guess "+input;
		this.revealed_chars.add(input);
		for(int i=0; i<this.hidden_chars.size();i++)
			this.hidden_chars.remove(Character.valueOf(input));

	}
	private String print_word(){

		for (int i=0; i<this.word.length(); i++){
			if(revealed_chars.contains(this.word.charAt(i)))
				this.wordToPrint+=this.word.charAt(i);
				//System.out.print(this.word.charAt(i));
			else
				this.wordToPrint+="-";
			//if(hidden_chars.contains(this.word.charAt(i)))
				//System.out.print("-");
		}
		String retVal=this.wordToPrint;
		this.wordToPrint="";
		return retVal;
	}
	
	/*open file function. Opens our file*/
	private void openFile(){
		try {
			this.scanFile=new Scanner(new File("/Users/mustafaal-abaychi/Documents/workspace/hangman/src/word_list.txt"));
		} catch (FileNotFoundException e) {
				e.printStackTrace();
		}
	}
	
	/*chooses word from File and returns it*/
    private String chooseWordFromFile() 
    { 
    	int i = 0; 
        while(this.scanFile.hasNext()) 
        { 
        	this.scanFile.next();
            i++; 
        }
        this.scanFile.close();
        words=new String[i];
        i = 0; 
        openFile();
        while(this.scanFile.hasNext()) 
        {
            words[i] = scanFile.next();      
            ++i; 
        }
        Random r=new Random();
        String chosen_word=words[r.nextInt(i)];
        return chosen_word; 
    }
    
    private void wordChars(){
    	for(int i=0; i<this.word.length(); i++)
			this.hidden_chars.add(this.word.charAt(i));
    	
    }
    
    //******
    private boolean contains (char input){
    	for(int i=0; i<this.guesses.size(); i++){
    		if(this.guesses.get(i).letter==input){
    			return true;
    		}
    	}
    	return false;//if list is empty
    }
    //*****
    private boolean hasLetter(List<Character> listOfLetters, char letter){
    	for(int i=0; i<listOfLetters.size(); i++){
    		if(listOfLetters.get(i)==letter){
    			return true;
    		}
    	}
    	return false;//if list is empty
    }
    private int countUniqueChars(String aWord){
    	int nrOfUniqueLetters=0;
    	List<Character> listOfLetters=new ArrayList<Character>();
    	char currentChar;
    	for(int i=0; i<aWord.length(); i++){
    		if(hasLetter(listOfLetters, currentChar=aWord.charAt(i))){
    			System.out.println("since "+currentChar+" is in the list, we do nothing");
    		}
    		else{
    			System.out.println("since "+currentChar+" is new to us, we add it to the list and increase nrUnique");
    			listOfLetters.add(aWord.charAt(i));
    			nrOfUniqueLetters++;
    		}
    	}
    	return nrOfUniqueLetters;
    }
    private List<Guess> missed(List<Guess> guesses){
    	List<Guess>wrong=new ArrayList<Guess>();
    	for(int i=0; i<guesses.size(); i++){
    		if(!guesses.get(i).exist){
    			wrong.add(guesses.get(i));
    		}
    	}
    	return wrong;
    }
    private String getMissed(List<Guess> guesses){
    	List<Guess> wrong=missed(guesses);
    	String str="";
    	for(int i=0; i<wrong.size(); i++){
    		str+=wrong.get(i).letter+", ";
    	}
    	return str;
    }
    
}
