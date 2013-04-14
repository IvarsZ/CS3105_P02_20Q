package model;

import java.util.ArrayList;

/**
 * 
 * A concept recognized by a Twenty Questions system.
 * 
 * @author iz2
 *
 */
public class Concept {
	
		private String name;
		private ArrayList<Answer> answers;
		
		/**
		 * Simple constructor.
		 * 
		 * @param name - The name of the concept.
		 * @param answers - The answers of this concept to questions from a Twenty Questions system.
		 */
		public Concept(String name, ArrayList<Answer> answers) {
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
		public ArrayList<Answer> getAnswers() {
			return answers;
		}
		
		/**
		 * Getter for an answer to a specific question.
		 */
		public Answer getAnswer(Question question) {
			return answers.get(question.getId());
		}
}
