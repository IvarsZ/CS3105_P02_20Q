package model;
/**
 * 
 *  Represents a question of a Twenty Questions system.
 * 
 * @author iz2
 *
 */
public class Question {
	
	private String text;
	private int id;
	
	public Question(String text, int id) {
		this.text = text;
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	public int getId() {
		return id;
	}
}
