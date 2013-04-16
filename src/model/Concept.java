package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * A concept recognized by a Twenty Questions system.
 * 
 * @author iz2
 *
 */
public class Concept {
	
		private String name;
		private HashMap<Integer, Answer> answers;
		
		/**
		 * Simple constructor.
		 * 
		 * @param name - The name of the concept.
		 * @param answers - The answers of this concept to questions from a Twenty Questions system.
		 */
		public Concept(String name, ArrayList<Answer> answers) {
			this.name = name;
			this.answers = new HashMap<Integer, Answer>();
			
			// Add all answers.
			for (Answer answer : answers) {
				addAnswer(answer);
			}
		}
		
		/**
		 * Add an answer for the concept.
		 */
		public void addAnswer(Answer answer) {
			answers.put(answer.getQuestion().getId(), answer);
		}
		
		public void updateAnswers(ArrayList<Answer> answers) {
			
			for (Answer answer : answers) {
				
				// If the current answer is unknown,
				Answer currentAnswer = getAnswer(answer.getQuestion());
				if (currentAnswer.getValue() == TwentyQuestionsModel.UNKNOWN) {
					
					// update it.
					currentAnswer.setValue(answer.getValue());
				}
			}
		}
		
		/**
		 * Getter for the name of the concept.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Getter for an answer to a specific question.
		 */
		public Answer getAnswer(Question question) {
			return answers.get(question.getId());
		}

		public boolean clashes(Concept concept) {
			
			// For each answer,
			for (Answer answer : answers.values()) {
				
				// if the known answers are different,
				double answerValue1 = answer.getValue();
				double answerValue2 = concept.getAnswer(answer.getQuestion()).getValue();
				if (answerValue1 != TwentyQuestionsModel.UNKNOWN && answerValue2 != TwentyQuestionsModel.UNKNOWN &&
					answerValue1 != answerValue2) {
					
					// there is no clash.
					return false;
				}
			}
			
			return true;
		}
}
