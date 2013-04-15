package model;

import java.util.ArrayList;

public class Round {

	private TwentyQuestionsModel model;
	private ArrayList<Answer> answers;

	private ArrayList<Integer> unansweredQuestions; 

	private Concept guessedConcept;
	private int nextQuestionId;

	private boolean hasNewGuess;

	private boolean isGuessCorrect;

	public Round(TwentyQuestionsModel model) {

		this.model = model;
		this.answers = new ArrayList<Answer>();

		isGuessCorrect = false;
		hasNewGuess = false;

		// For each question,
		unansweredQuestions = new ArrayList<Integer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {

			// add its index to the list of unasnwered questions.
			unansweredQuestions.add(model.getQuestions().get(i).getId());
		}
	}

	public Question nextQuestion() {

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

	public void addAnswer(Answer answer) {

		hasNewGuess = false;

		// Add the answer and remove the question it answered from unanswered questions.
		answers.add(answer);
		unansweredQuestions.remove(unansweredQuestions.indexOf(answer.getQuestion().getId()));

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
