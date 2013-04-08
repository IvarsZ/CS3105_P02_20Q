package model;

/**
 * 
 * Represents an answer to a question of a Twenty Questions system.
 * 
 * @author iz2
 *
 */
public class Answer {
	
	private Question question;
	private double value;
	
	/**
	 * @param question - the question which is answered.
	 * @param value - the value representing the answer. 0 is no, 1 is yes.
	 */
	public Answer(Question question, double value) {
		this.question = question;
		this.value = value;
	}
	
	/**
	 * Getter for the question.
	 */
	public Question getQuestion() {
		return question;
	}
	
	/**
	 * The getter for the value.
	 */
	public double getValue() {
		return value;
	}
}
