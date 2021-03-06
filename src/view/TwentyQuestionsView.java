package view;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import model.Answer;
import model.Concept;
import model.Question;
import model.Round;
import model.TwentyQuestionsModel;

public class TwentyQuestionsView {

	private static final int EXPERIMENT_ITERATION_COUNT = 25;

	private static final int PLAY_AGAIN_QUESTION_ID = -1;
	private static final int IS_GUESS_CORRECT_QUESTION_ID = -2;

	private static final DecimalFormat DF = new DecimalFormat("#.##");

	/**
	 * Reads a twenty questions model from a file.
	 * 
	 * @param in - the scanner that reads the file.
	 * 
	 * @return - the twenty questions model.
	 */
	public static TwentyQuestionsModel read(Scanner in) {

		// Read the questions.
		int questionCount = in.nextInt();
		in.nextLine();
		ArrayList<Question> questions = new ArrayList<Question>();
		for (int i = 0; i <  questionCount; i++) {
			questions.add(new Question(in.nextLine(), i));
		}

		// Read the concepts,
		int conceptCount = in.nextInt();
		ArrayList<Concept> concepts = new ArrayList<Concept>();
		for (int conceptIndex = 0; conceptIndex < conceptCount; conceptIndex++) {

			in.nextLine();
			String name = in.nextLine();

			// and answers for each concept.
			ArrayList<Answer> answers = new ArrayList<Answer>();
			for (int answerIndex = 0; answerIndex < questionCount; answerIndex++) {
				answers.add(new Answer(questions.get(answerIndex), in.nextDouble()));
			}

			concepts.add(new Concept(name, answers));
		}

		// Read training parameters.
		in.nextLine();
		int hiddenUnitsCount = in.nextInt();
		int maxIterations = in.nextInt();
		double learningRate = in.nextDouble();
		double momentum = in.nextDouble();

		return new TwentyQuestionsModel(concepts, questions, hiddenUnitsCount, maxIterations, learningRate, momentum);
	}

	private TwentyQuestionsModel q20Model;

	/**
	 * Constructor for the view of Twenty Questions.
	 * 
	 * @param filename - the name of the file used to construct the model of the system.
	 * @throws IOException
	 */
	public TwentyQuestionsView(String filename) throws IOException {

		// Create the q20 instance from a file and train it.
		File inFile = new File(filename);
		Scanner in = new Scanner(inFile);
		q20Model = read(in);
		in.close();
	}

	/**
	 * Prints the model of the system.
	 */
	public void print() {

		// Print all questions.
		int questionCount = q20Model.getQuestions().size();
		for (int i = 0; i < questionCount; i++) {

			Question question = q20Model.getQuestions().get(i);
			System.out.println(question.getId() + ": " + question.getText());
		}

		// Print all concepts,
		for (int i = 0; i < q20Model.getConcepts().size(); i++) {

			Concept concept = q20Model.getConcepts().get(i);
			System.out.println(concept.getName());

			// and their answers to all questions in order.
			for (int j = 0; j < questionCount; j++) {

				System.out.print(concept.getAnswer(q20Model.getQuestions().get(j)).getValue() + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Prints the model of the system.
	 * @throws IOException 
	 */
	public void writeToFile(BufferedWriter out) throws IOException {

		// Print all questions.
		int questionCount = q20Model.getQuestions().size();
		out.write(questionCount + "");
		out.newLine();
		for (int i = 0; i < questionCount; i++) {

			Question question = q20Model.getQuestions().get(i);
			out.write(question.getText());
			out.newLine();
		}

		// Print all concepts,
		int conceptsCount = q20Model.getConcepts().size();
		out.write(conceptsCount + "");
		out.newLine();
		for (int i = 0; i < conceptsCount; i++) {

			Concept concept = q20Model.getConcepts().get(i);
			out.write(concept.getName());
			out.newLine();

			// and their answers to all questions in order.
			for (int j = 0; j < questionCount; j++) {

				out.write(concept.getAnswer(q20Model.getQuestions().get(j)).getValue() + " ");
			}
			out.newLine();
		}
		
		// Print parameters.
		out.write(q20Model.getHiddenUnitCount() + " ");
		out.write(q20Model.getMaxIterations() + " ");
		out.write(q20Model.getLearningRate() + " ");
		out.write(q20Model.getMomentum() + " ");
		out.newLine();
	}

	public void experimentWithTrainingParameters() {

		// For different combinations of learning rate and momemtum values,
		for (double learningRate = 0.1; learningRate <= 1; learningRate += 0.1) {
			for (double momentum = 0; momentum <= 1; momentum += 0.1) {

				// get the average iteration count for training from scratch.
				int iterationCount = 0;
				for (int i = 0; i < 25; i++) {

					q20Model.setTrainingParameters(learningRate, momentum);
					q20Model.resetNetwork();
					iterationCount += q20Model.getLastIterationCount();
				}

				System.out.print(iterationCount/EXPERIMENT_ITERATION_COUNT + " ");
			}
			System.out.println();
		}
	}


	/**
	 * Plays Twenty Questions with the user.
	 */
	public void play() throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		Answer playAgainAnswer = null;
		do {

			// Start a new round of the game.
			System.out.println("Choose a concept.");
			Round game = new Round(q20Model);

			// While the system has more questions,
			Question question;
			while ((question = game.nextQuestion()) != null) {

				// ask the next question and record the answer.
				Answer answer = readAnswer(question, in);
				game.addAnswer(answer);

				// If the game has a new guess,
				if (game.hasNewGuess()) {

					// output it.
					System.out.println("My guess is " + game.getGuessedConcept().getName() + ".");

					// If the guess is correct,
					Question isGuessCorrectQuestion = new Question("Is my guess correct?", IS_GUESS_CORRECT_QUESTION_ID);
					if (readAnswer(isGuessCorrectQuestion, in).getValue() == TwentyQuestionsModel.YES) {

						// mark it and keep asking extra questions to gather information.
						game.setGuessCorrect(true);
						System.out.println("Great, please answer extra questions about " + game.getGuessedConcept().getName() + ".");
					}
				}
			}

			// If the system did not guess the concept,
			if (!game.isGuessCorrect()) {

				// ask the user to enter it and add it.
				String conceptName = readText("What was the concept?", in);
				Concept addedConcept = q20Model.addConcept(conceptName, game.getAnswers());

				// If the added concept clashes with the last guessed concept,
				if (addedConcept.clashes(game.getGuessedConcept())) {

					// ask the user to enter a question and add it.
					String questionText = readText("Please enter a question such that " +
							game.getGuessedConcept().getName() + " has answer no, while " +
							addedConcept.getName() + " has answer yes.", in);
					q20Model.addQuestion(questionText, game.getGuessedConcept(), addedConcept);
				}
			}
			else {

				// Otherwise update answers.
				game.getGuessedConcept().updateAnswers(game.getAnswers());
			}

			// Ask the user to play again.
			Question playAgainQuestion = new Question("Play again?", PLAY_AGAIN_QUESTION_ID);
			playAgainAnswer = readAnswer(playAgainQuestion, in);
		} while (playAgainAnswer.getValue() == TwentyQuestionsModel.YES);

		in.close();
	}

	/**
	 * Writes the neural network of the model to the file specified by the filename.
	 */
	public void writeNetworkToFile(String fileName) {
		q20Model.writeNetworkToFile(fileName);
	}

	/**
	 * Getter for the model of 20q.
	 */
	public TwentyQuestionsModel getModel() {
		return q20Model;
	}

	/**
	 * Asks the question and reads an answer from the user.
	 */
	private Answer readAnswer(Question question, BufferedReader in) throws IOException {

		// Keep asking until user gives a valid answer.
		Double answerValue = null;
		do {

			System.out.println(question.getText() + " (yes/y or no/n)");
			String answerText = in.readLine();
			
			// Record the answer if it is yes or no.
			if (answerText.equals("yes") || answerText.equals("y")) {
				answerValue = TwentyQuestionsModel.YES;
			}
			else if (answerText.equals("no") || answerText.equals("n")) {
				answerValue = TwentyQuestionsModel.NO;
			}

		} while (answerValue == null);

		return new Answer(question, answerValue);
	}

	/**
	 * Get a string from the user.
	 */
	private String readText(String question, BufferedReader in) throws IOException {

		// Keep asking the question until user enters some text.
		System.out.println(question);
		String text = "";
		do {

			text = in.readLine();
		} while (text.length() <= 0);

		return text;
	}
}
