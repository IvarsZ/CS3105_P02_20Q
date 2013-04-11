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
	
	/**
	 * Simple constructor
	 * 
	 * @param text - the text of the question.
	 * @param id - id used to identify the question in the system.
	 */
	public Question(String text, int id) {
		this.text = text;
		this.id = id;
	}
	
	/**
	 * Getter for the text of this question.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Getter for the id of this question.
	 */
	public int getId() {
		return id;
	}
}
