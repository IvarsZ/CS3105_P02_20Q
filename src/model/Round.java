package model;

import java.util.ArrayList;

public class Round {
	
	private static final int QUESTION_LIMIT = 20;

	private TwentyQuestionsModel model;
	private ArrayList<Answer> answers;

	private ArrayList<Integer> unansweredQuestions;

	private Concept guessedConcept;
	private boolean hasNewGuess;
	private ArrayList<Concept> madeGuesses;

	private boolean isGuessCorrect;

	/**
	 * Constructor from a 20q model.
	 */
	public Round(TwentyQuestionsModel model) {

		this.model = model;
		this.answers = new ArrayList<Answer>();

		isGuessCorrect = false;
		hasNewGuess = false;

		// For each question,
		unansweredQuestions = new ArrayList<Integer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {

			// add its index to the list of unanswered questions.
			unansweredQuestions.add(model.getQuestions().get(i).getId());
		}
		
		madeGuesses = new ArrayList<Concept>();
	}

	/**
	 * @return the next question to ask.
	 */
	public Question nextQuestion() {
		
		// If 20 questions have been answered,
		if (answers.size() >= QUESTION_LIMIT) {
			
			// stop asking.
			return null;
		}
		
		// If a correct guess has already been made,
		if (isGuessCorrect) {
			
			// pick the first unanswered question with an unknown answer.
			for (int i = 0; i < unansweredQuestions.size(); i++) {
				
				Question question = model.getQuestions().get(unansweredQuestions.get(i));
				if (guessedConcept.getAnswer(question).getValue() == TwentyQuestionsModel.UNKNOWN) {
					
					return question;
				}
			}
			
			// If all questions are answered stop asking.
			return null;
		}

		// For each unanswered question.
		int minScore = Integer.MAX_VALUE;
		Question bestQuestion = null;
		for (int i = 0; i < unansweredQuestions.size(); i++) {
			Question question = model.getQuestions().get(unansweredQuestions.get(i));

			// Get the possible concepts if the answer is yes.
			answers.add(new Answer(question, TwentyQuestionsModel.YES));
			ArrayList<Concept> possibleYesConcepts = model.possibleConcepts(answers);
			answers.remove(answers.size() - 1);

			// Get the possible concepts if the answer is no.
			answers.add(new Answer(question, TwentyQuestionsModel.NO));
			ArrayList<Concept> possibleNoConcepts = model.possibleConcepts(answers);
			answers.remove(answers.size() - 1);
			
			// Calculate the score, and if it is lower than the current lowest,
			int score = Math.max(possibleYesConcepts.size(), possibleNoConcepts.size());
			if (score < minScore) {
				
				// update it and the question.
				bestQuestion = question;
				minScore = score;
			}
		}

		return bestQuestion;
	}

	/**
	 * Add an answer.
	 */
	public void addAnswer(Answer answer) {

		hasNewGuess = false;

		// Add the answer and remove the question it answered from unanswered questions.
		answers.add(answer);
		unansweredQuestions.remove(unansweredQuestions.indexOf(answer.getQuestion().getId()));

		ArrayList<Concept> possibleConcepts = model.possibleConcepts(answers);
		if (possibleConcepts.size() == 1 || answers.size() == QUESTION_LIMIT) {

			if (!possibleConcepts.get(0).equals(guessedConcept) && !madeGuesses.contains(possibleConcepts.get(0))) {

				guessedConcept = possibleConcepts.get(0);
				hasNewGuess = true;
				madeGuesses.add(guessedConcept);
			}
		}
	}

	/**
	 * Guessed concept getter.
	 */
	public Concept getGuessedConcept() {
		return guessedConcept;
	}

	/**
	 * Answers getter.
	 */
	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	/**
	 * @return true if the system wants to make a new guess.
	 */
	public boolean hasNewGuess() {
		return hasNewGuess;
	}

	/**
	 * @return true if the system guessed correctly.
	 */
	public boolean isGuessCorrect() {
		return isGuessCorrect;
	}

	/**
	 * Setter for isGuessCorrect.
	 */
	public void setGuessCorrect(boolean isGuessCorrect) {
		this.isGuessCorrect = isGuessCorrect;
	}
}
