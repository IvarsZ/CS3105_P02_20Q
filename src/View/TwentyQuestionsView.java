package View;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Scanner;

import model.Answer;
import model.Concept;
import model.Question;
import model.TwentyQuestionsModel;

public class TwentyQuestionsView {
	
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
		Question[] questions = new Question[questionCount];
		for (int i = 0; i <  questionCount; i++) {
			questions[i] = new Question(in.nextLine(), i);
		}

		// Read the concepts,
		int conceptCount = in.nextInt();
		Concept[] concepts = new Concept[conceptCount];
		for (int conceptIndex = 0; conceptIndex < conceptCount; conceptIndex++) {

			in.nextLine();
			String name = in.nextLine();

			// and answers for each concept.
			Answer[] answers = new Answer[questionCount];
			for (int answerIndex = 0; answerIndex < questionCount; answerIndex++) {
				answers[answerIndex] = new Answer(questions[answerIndex], in.nextDouble());
			}

			concepts[conceptIndex] = new Concept(name, answers);
		}

		// Read training parameters.
		in.nextLine();
		int hiddenUnitsCount = in.nextInt();
		int maxIterations = in.nextInt();
		double learningRate = in.nextInt();
		double momentum = in.nextInt();

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
		q20Model.train();
	}
	
	/**
	 * Prints the model of the system.
	 */
	public void print() {
		
		// Print the questions.
		System.out.println("Questions: ");
		Question[] questions = q20Model.getQuestions();
		for (Question question : questions) {
			System.out.println(question.getText());
		}
		
		System.out.println("Input, expected and actual output patterns for concepts:");
		Concept[] concepts = q20Model.getConcepts();
		double[][] input = q20Model.getInput();
		double[][] actualOutput = q20Model.getActualOutput();
		double[][] expectedOutput = q20Model.getExpectedOutput();
		
		// For each concept,
		for (int i = 0; i < concepts.length; i++) {
			
			// print its name,
			System.out.print(concepts[i].getName() + ": ");
			
			// input pattern,
			for (int j = 0; j < input[i].length; j++) {
				System.out.print((int) input[i][j] + " ");
			}
			System.out.print(" | ");
			
			// expected output,
			for (int j = 0; j < expectedOutput[i].length; j++) {
				System.out.print((int) expectedOutput[i][j] + " ");
			}
			System.out.print(" | ");
			
			// and actual output.
			for (int j = 0; j < actualOutput[i].length; j++) {
				System.out.print(DF.format(actualOutput[i][j]) + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Plays Twenty Questions with the user.
	 */
	public void play() throws IOException {

		System.out.println("Choose a concept from: " + q20Model.conceptsToString());
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Question[] questions = q20Model.getQuestions();
		double[] answers = new double[questions.length];
		
		// For each question,
		for (int i = 0; i < questions.length; i++) {
			
			System.out.println(questions[i].getText() + " (yes/no)");
			
			// read the answer.
			Double answer = null;
			do {
				String answerInput = in.readLine();
				if (answerInput.equals("yes")) {
					answer = 1.0;
				}
				else if (answerInput.equals("no")) {
					answer = 0.0;
				}
			} while (answer == null);
			
			answers[i] = answer;
		}
		in.close();
		
		// Get the guess.
		System.out.println(q20Model.guessConcept(answers));
	}
}
