package model;

import java.util.ArrayList;

import sun.security.krb5.Asn1Exception;

public class Round {
	
	private TwentyQuestionsModel model;
	private Concept guessedConcept;
	
	ArrayList<Answer> answers;
	
	private int nextQuestionId;
	
	public Round(TwentyQuestionsModel model) {
		
		this.model = model;
		this.answers = new ArrayList<Answer>();
	}
	
	public Question nextQuestion() {
		
		// TODO improve next question.
		if (nextQuestionId < model.getQuestions().length) {
			return model.getQuestions()[nextQuestionId++];
		}
		
		return null;
	}
	
	public void addAnswer(Answer answer) {
		
		answers.add(answer);
		
		// TODO add early guesses.
		if (answers.size() == model.getQuestions().length) {
			guessedConcept = model.guessConcept(answers);
		}
	}
	
	public Concept getGuessedConcept() {
		return guessedConcept;
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
}
