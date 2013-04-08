package model;

/**
 * 
 * A concept recognized by a Twenty Questions system.
 * 
 * @author iz2
 *
 */
public class Concept {
	
		private String name;
		private Answer[] answers;
		
		/**
		 * Simple constructor.
		 * 
		 * @param name - The name of the concept.
		 * @param answers - The answers of this concept to questions from a Twenty Questions system.
		 */
		public Concept(String name, Answer[] answers) {
			this.name = name;
			this.answers = answers;
		}
		
		/**
		 * Getter for the name of the concept.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Getter for the answers of this concept.
		 */
		public Answer[] getAnswers() {
			return answers;
		}
		
		/**
		 * Getter for an answer to a specific question.
		 */
		public Answer getAnswer(Question question) {
			return answers[question.getId()];
		}
}
