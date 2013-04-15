package model;

import java.util.ArrayList;

public class Round {
	
	private TwentyQuestionsModel model;
	private ArrayList<Answer> answers;	
	
	private Concept guessedConcept;
	private int nextQuestionId;
	
	private boolean hasNewGuess;
	
	private boolean isGuessCorrect;
	
	public Round(TwentyQuestionsModel model) {
		
		this.model = model;
		this.answers = new ArrayList<Answer>();
		
		isGuessCorrect = false;
		hasNewGuess = false;
	}
	
	public Question nextQuestion() {
		
		// TODO improve next question.
		if (nextQuestionId < model.getQuestions().size()) {
			return model.getQuestions().get(nextQuestionId++);
		}
		
		return null;
	}
	
	public void addAnswer(Answer answer) {
		
		hasNewGuess = false;
		
		answers.add(answer);
		
		ArrayList<Concept> possibleConcepts = model.possibleConcepts(answers);
		if (possibleConcepts.size() == 1) {
			
			if (!possibleConcepts.get(0).equals(guessedConcept)) {
				
				guessedConcept = possibleConcepts.get(0);
				hasNewGuess = true;
			}
		}
	}
	
	public Concept getGuessedConcept() {
		return guessedConcept;
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
	public boolean hasMoreQuestions() {
		
		// TODO proper implementation.
		return nextQuestionId >= model.getQuestions().size();
	}

	public boolean hasNewGuess() {
		return hasNewGuess;
	}

	public boolean isGuessCorrect() {
		return isGuessCorrect;
	}

	public void setGuessCorrect(boolean isGuessCorrect) {
		this.isGuessCorrect = isGuessCorrect;
	}
}
