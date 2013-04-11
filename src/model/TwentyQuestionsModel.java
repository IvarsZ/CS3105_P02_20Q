package model;
import java.util.Arrays;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

public class TwentyQuestionsModel {
	
	private Question[] questions;
	private Concept[] concepts;
	
	private NeuralNetwork neuralNetwork;
	private BackPropagation backProgationRule;
	private DataSet trainingDataSet;
	
	/**
	 * Constructor for the model of the Twenty Questions system.
	 * 
	 * @param concepts - array of concepts recognized by the system.
	 * @param questions - array of questions associated with concepts in the system.
	 * @param hiddenUnitsCount - number of hidden units in the middle layer of the neural network.
	 * @param maxIterations - maximum number of iterations to do when training the network.
	 * @param learningRate - the learning rate to use when training the network.
	 */
	public TwentyQuestionsModel(Concept[] concepts, Question[] questions, int hiddenUnitsCount, int maxIterations, double learningRate) {

		this.concepts = concepts;
		this.questions = questions;

		// Set the hidden unit count if it's positive.
		if (hiddenUnitsCount < 1) {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, questions.length, getOutputUnitsCount());
		}
		else {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
		}
		
		// Set the max iterations, learning rate and TODO momentum for the learning rule used by the neural network.
		backProgationRule = new BackPropagation();
		backProgationRule.setMaxIterations(maxIterations);
		backProgationRule.setLearningRate(learningRate);

		// For each concept,
		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		for (int conceptIndex = 0; conceptIndex < concepts.length; conceptIndex++) {

			// calculate its output values from the index and input values from the answers.
			double[] outputValues = conceptIndexToBinaryPattern(conceptIndex);
			double[] inputValues = new double[getInputUnitsCount()];
			for (int i = 0; i < getInputUnitsCount(); i++) {
				inputValues[i] = concepts[conceptIndex].getAnswer(questions[i]).getValue();
			}

			// Add the values as a training set.
			trainingDataSet.addRow(inputValues, outputValues);
		}
	}
	
	/**
	 * Getter for the questions of the system.
	 */
	public Question[] getQuestions() {
		return questions;
	}
	
	/**
	 * Getter for the concepts of the system.
	 */
	public Concept[] getConcepts() {
		return concepts;
	}

	/**
	 * @return the system's guess of the concept based on the given answers.
	 */
	public String guessConcept(double[] answers) {
		
		// Get the network output and convert it to a concept index.
		neuralNetwork.setInput(answers);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		int conceptIndex = binaryPatternToConceptIndex(networkOutput);
		return concepts[conceptIndex].getName();
	}
	
	/**
	 * Trains the neural network of the system.
	 */
	public void train() {
		
		neuralNetwork.learn(trainingDataSet, backProgationRule);
	}
	
	/**
	 * @return a string listing all concepts in the system.
	 */
	public String conceptsToString() {

		String conceptsString = "";
		for (Concept concept : concepts) {
			conceptsString += concept.getName() + ", ";
		}

		return conceptsString;
	}
	
	public double[][] getInput() {
		
		double[][] input = new double[concepts.length][];
		for (int i = 0; i < concepts.length; i++) {
			
			Answer[] answers = concepts[i].getAnswers();
			input[i] = new double[answers.length];
			for (int j = 0; j < answers.length; j++) {
				input[i][j] = answers[j].getValue();
			}
		}
		
		return input;
	}
	
	public double[][] getExpectedOutput() {
		
		double[][] output = new double[concepts.length][];
		for (int i = 0; i < concepts.length; i++) {
			output[i] = conceptIndexToBinaryPattern(i);
		}
		
		return output;
	}
	
	public double[][] getActualOutput() {
		
		double[][] output = new double[concepts.length][];
		double[][] input = getInput();
		for (int i = 0; i < concepts.length; i++) {
			
			neuralNetwork.setInput(input[i]);
			neuralNetwork.calculate();
			output[i] = Arrays.copyOf(neuralNetwork.getOutput(), neuralNetwork.getOutput().length);
		}
		
		return output;
	}

	private int binaryPatternToConceptIndex(double bits[]) {
		
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

	private double[] conceptIndexToBinaryPattern(int conceptIndex) {

		int bitCount = getOutputUnitsCount();
		double[] bits = new double[bitCount];
		
		// Bits are read from right to match the binaryPatternToConceptIndex method.
		for (int i = bitCount - 1; i >= 0; i--) {

			if ((conceptIndex & (1 << i)) != 0) {
				bits[i] = 1;
			}
		}

		return bits;
	}

	private int getInputUnitsCount() {
		return questions.length;
	}

	private int getOutputUnitsCount() {
		return (int) Math.ceil((Math.log(concepts.length) / Math.log(2)));
	}
}
