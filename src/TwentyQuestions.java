import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

// TODO split in player and network.
public class TwentyQuestions {
	
	public static void main(String[] args) throws IOException {
		
		File inFile = new File("Part1-Input2.in");
		Scanner in = new Scanner(inFile);
		TwentyQuestions q20 = read(in);
		in.close();
		q20.train();
		q20.all();
		q20.testAll();
		//q20.play();
	}
	
	// TODO move, rename, refactor.
	private void all() {
		
		for (int i = 0; i < concepts.length; i++) {
			neuralNetwork.setInput(concepts[i].getAnswers());
			neuralNetwork.calculate();
			double[] networkOutput = neuralNetwork.getOutput();
			for (int j = 0; j < networkOutput.length; j++) {
				System.out.print(networkOutput[j] + " ");
			}
			System.out.println();
		}
	}
	
	private void testAll() {
		
		boolean hasError = false;
		for (int i = 0; i < concepts.length; i++) {
			neuralNetwork.setInput(concepts[i].getAnswers());
			neuralNetwork.calculate();
			double[] networkOutput = neuralNetwork.getOutput();
			
			if (binaryPatternToConceptIndex(networkOutput) != i) {
				System.out.println("ERROR on: " + concepts[i].getName() + " expected " + i + " actual " + binaryPatternToConceptIndex(networkOutput));
				hasError = true;
			}
		}
		System.out.println(!hasError);
	}

	private Concept[] concepts;
	private String[] questions;

	private NeuralNetwork neuralNetwork;
	private int maxIterations;
	private double learningRate;

	public TwentyQuestions(Concept[] concepts, String[] questions, int hiddenUnitsCount, int maxIterations, double learningRate) {

		this.concepts = concepts;
		this.questions = questions;

		this.maxIterations = maxIterations;
		this.learningRate = learningRate;

		if (hiddenUnitsCount < 1) {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, questions.length, getOutputUnitsCount());
		}
		else {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
		}
	}

	public static TwentyQuestions read(Scanner in) {

		// Read the questions.
		int questionCount = in.nextInt();
		in.nextLine();
		String[] questions = new String[questionCount];
		for (int i = 0; i <  questionCount; i++) {
			questions[i] = in.nextLine();
		}

		// Read the concepts.
		//in.nextLine();
		int conceptCount = in.nextInt();
		Concept[] concepts = new Concept[conceptCount];
		for (int conceptIndex = 0; conceptIndex < conceptCount; conceptIndex++) {

			in.nextLine();
			String name = in.nextLine();

			// Read the answers.
			double[] answers = new double[questionCount];
			for (int answerIndex = 0; answerIndex < questionCount; answerIndex++) {
				answers[answerIndex] = in.nextDouble();
			}

			concepts[conceptIndex] = new Concept(name, answers);
		}

		// Read training parameters.
		in.nextLine();
		int hiddenUnitsCount = in.nextInt();
		int maxIterations = in.nextInt();
		double learningRate = in.nextInt();

		return new TwentyQuestions(concepts, questions, hiddenUnitsCount, maxIterations, learningRate);
	}

	public void train() {

		// Learning rule used by the neural network.
		BackPropagation backProgationRule = new BackPropagation();
		backProgationRule.setMaxIterations(maxIterations);
		backProgationRule.setLearningRate(learningRate);

		DataSet dataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		for (int conceptIndex = 0; conceptIndex < concepts.length; conceptIndex++) {

			double[] inputValues = new double[getInputUnitsCount()];
			double[] outputValues = conceptIndexToBinaryPattern(conceptIndex, getOutputUnitsCount());
			for (int i = 0; i < getInputUnitsCount(); i++) {
				inputValues[i] = concepts[conceptIndex].getAnswer(i);
			}

			dataSet.addRow(inputValues, outputValues);
		}

		// TODO separate?
		neuralNetwork.learn(dataSet, backProgationRule);
	}

	// TODO refactor-split and cleanup.
	public void play() throws IOException {

		System.out.println("Choose a concept from: " + conceptsToString());
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		double[] answers = new double[questions.length];
		for (int i = 0; i < questions.length; i++) {
			
			
			System.out.println(questions[i] + " (yes/no)");
			
			// Read answer.
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
		
		// Get the answer.
		neuralNetwork.setInput(answers);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		int conceptIndex = binaryPatternToConceptIndex(networkOutput);
		System.out.println(concepts[conceptIndex].getName());
		
	}

	private int getInputUnitsCount() {
		return questions.length;
	}

	private int getOutputUnitsCount() {
		return (int) Math.ceil((Math.log(concepts.length) / Math.log(2)));
	}

	private String conceptsToString() {

		String conceptsString = "";
		for (Concept concept : concepts) {
			conceptsString += concept.getName() + ", ";
		}

		return conceptsString;
	}
	
	// TODO add momentum, add support for wrong answers.

	// TODO  explain reverse.
	private static int binaryPatternToConceptIndex(double bits[]) {
		
		int index = 0;
		int pow2 = 1;
		for (int i = 0; i < bits.length; i++) {
			if (bits[i] >= 0.5) {
				index += pow2;
			}
			
			pow2 *= 2;
		}
		
		return index;
	}

	private static double[] conceptIndexToBinaryPattern(int conceptIndex, int bitCount) {

		double[] bits = new double[bitCount];
		for (int i = bitCount - 1; i >= 0; i--) {

			if ((conceptIndex & (1 << i)) != 0) {
				bits[i] = 1;
			}
		}

		return bits;
	}
}
