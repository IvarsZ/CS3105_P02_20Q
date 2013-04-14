package model;

import java.util.ArrayList;

import sun.security.krb5.Asn1Exception;

public class Round {
	
	private TwentyQuestionsModel model;
	private ArrayList<Answer> answers;	
	
	private Concept guessedConcept;
	private int nextQuestionId;
	
	public Round(TwentyQuestionsModel model) {
		
		this.model = model;
		this.answers = new ArrayList<Answer>();
	}
	
	public Question nextQuestion() {
		
		// TODO improve next question.
		if (nextQuestionId < model.getQuestions().size()) {
			return model.getQuestions().get(nextQuestionId++);
		}
		
		return null;
	}
	
	public void addAnswer(Answer answer) {
		
		answers.add(answer);
		
		// TODO add early guesses.
		if (answers.size() == model.getQuestions().size()) {
			guessedConcept = model.guessConcept(answers);
		}
	}
	
	public Concept getGuessedConcept() {
		return guessedConcept;
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
	public boolean isGuessUnsure() {
		
		for (int i = 0; i < answers.size(); i++) {
			
			if (guessedConcept.getAnswer(answers.get(i).getQuestion()).getValue() != answers.get(i).getValue()) {
				
				return true;
			}
		}
		
		return false;
	}
}
