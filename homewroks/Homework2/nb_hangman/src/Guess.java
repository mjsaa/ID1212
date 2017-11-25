
public class Guess {
	public char letter;
	public boolean exist;
	public Guess(char letter, boolean exist){
		this.letter=letter;
		this.exist=exist;
	}
	public char getLetter(){
		return this.letter;
	}
	public boolean getExistence(){
		return this.exist;
	}
	
}
