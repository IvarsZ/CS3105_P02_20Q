package model;
import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class TwentyQuestionsModel {

	private Question[] questions;
	private ArrayList<Concept> concepts;

	private NeuralNetwork neuralNetwork;
	private MomentumBackpropagation backProgationRule;
	private DataSet trainingDataSet;

	private int hiddenUnitsCount;
	private int maxIterations;
	private double learningRate;
	private double momentum;

	private int lastIterationCount;

	/**
	 * Constructor for the model of the Twenty Questions system.
	 * 
	 * @param concepts - array of concepts recognized by the system.
	 * @param questions - array of questions associated with concepts in the system.
	 * @param hiddenUnitsCount - number of hidden units in the middle layer of the neural network.
	 * @param maxIterations - maximum number of iterations to do when training the network.
	 * @param learningRate - the learning rate to use when training the network.
	 */
	public TwentyQuestionsModel(ArrayList<Concept> concepts, Question[] questions, int hiddenUnitsCount, int maxIterations, double learningRate, double momentum) {

		this.concepts = concepts;
		this.questions = questions;

		this.hiddenUnitsCount = hiddenUnitsCount;
		this.maxIterations = maxIterations;
		this.learningRate = learningRate;
		this.momentum = momentum;

		// Create the network.
		initializeNetwork();
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
	public ArrayList<Concept> getConcepts() {
		return concepts;
	}

	/**
	 * @return the system's guess of the concept based on the given answers.
	 */
	public Concept guessConcept(Answer[] answers) {

		// Get the network output and convert it to a concept index.
		double[] answerValues = new double[answers.length];
		for (int i = 0; i < answers.length; i++) {
			answerValues[i] = answers[i].getValue();
		}

		return guessConcept(answerValues);
	}

	/**
	 * @return the system's guess of the concept based on the given answers.
	 */
	public Concept guessConcept(double[] answerValues) {

		neuralNetwork.setInput(answerValues);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		int conceptIndex = binaryPatternToConceptIndex(networkOutput);

		// If the concept is not in the system,
		while (conceptIndex >= concepts.size()) {

			// reduce the index by half, thereby picking a similar existing concept.
			conceptIndex /= 2;
		}

		return concepts.get(conceptIndex);
	}

	/**
	 * Trains the neural network of the system.
	 */
	public void train() {

		// Set the max iterations, learning rate and momentum for the learning rule used by the neural network.
		if (backProgationRule == null) {
			backProgationRule = new MomentumBackpropagation();
		}
		backProgationRule.setMaxIterations(maxIterations);
		backProgationRule.setLearningRate(learningRate);
		backProgationRule.setMomentum(momentum);

		backProgationRule.setMaxError(0.0001);

		neuralNetwork.learn(trainingDataSet, backProgationRule);

		// Save the iteration count.
		lastIterationCount = backProgationRule.getCurrentIteration();
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

	/**
	 * @return an array of required input for each concept.
	 */
	public double[][] getInput() {

		// For each concept,
		double[][] input = new double[concepts.size()][];
		for (int i = 0; i < concepts.size(); i++) {

			// construct required input array from its answers to questions.
			Answer[] answers = concepts.get(i).getAnswers();
			input[i] = new double[answers.length];
			for (int j = 0; j < answers.length; j++) {
				input[i][j] = answers[j].getValue();
			}
		}

		return input;
	}

	/**
	 * @return an array of values to which the network is trained to for each concept.
	 */
	public double[][] getExpectedOutput() {

		// Initialize the array of output values for each concept by converting its index to binary.
		double[][] output = new double[concepts.size()][];
		for (int i = 0; i < concepts.size(); i++) {
			output[i] = conceptIndexToBinaryPattern(i);
		}

		return output;
	}

	/**
	 * @return an array of actual output values for each concept.
	 */
	public double[][] getActualOutput() {

		double[][] output = new double[concepts.size()][];
		double[][] input = getInput();

		// For each concept,
		for (int i = 0; i < concepts.size(); i++) {

			// get its input and calculate actual output.
			neuralNetwork.setInput(input[i]);
			neuralNetwork.calculate();
			output[i] = Arrays.copyOf(neuralNetwork.getOutput(), neuralNetwork.getOutput().length);
		}

		return output;
	}

	/**
	 * Setter for the training parameters.
	 */
	public void setTrainingParameters(double learningRate, double momentum) {
		this.learningRate = learningRate;
		this.momentum = momentum;
	}

	/**
	 * @return - the number of iterations during last training.
	 */
	public int getLastIterationCount() {
		return lastIterationCount;
	}

	/**
	 * Resets the weights in the network to random values.
	 */
	public void resetNetwork() {
		neuralNetwork.randomizeWeights();
	}

	/**
	 * Writes the neural network to the file specified by the filename.
	 */
	public void writeNetworkToFile(String fileName) {
		neuralNetwork.save(fileName);
	}

	public void addConcept(String conceptName, Answer[] answers) {

		// Add the concept.
		Concept concept = new Concept(conceptName, answers);
		concepts.add(concept);

		// Calculate its output values from the index and input values from the answers.
		double[] outputValues = conceptIndexToBinaryPattern(concepts.size() - 1);
		double[] inputValues = new double[getInputUnitsCount()];
		for (int i = 0; i < getInputUnitsCount(); i++) {
			inputValues[i] = concepts.get(concepts.size() - 1).getAnswer(questions[i]).getValue();
		}

		// If there's too many concepts
		while (concepts.size() > Math.pow(2, neuralNetwork.getOutputsCount())) {

			// recreate the network.
			initializeNetwork();
		}

		// Add the values as a training set.
		trainingDataSet.addRow(inputValues, outputValues);

		train();
	}

	/**
	 * Creates the used neural network.
	 */
	private void initializeNetwork() {

		// Set the hidden unit count if it's positive.
		if (hiddenUnitsCount < 1) {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, questions.length, getOutputUnitsCount());
		}
		else {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
		}

		// Randomize weights and create a training set for the network.
		neuralNetwork.randomizeWeights();
		initializeTrainingSet();
	}

	private void initializeTrainingSet() {

		// For each concept,
		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		for (int conceptIndex = 0; conceptIndex < concepts.size(); conceptIndex++) {

			// calculate its output values from the index and input values from the answers.
			double[] outputValues = conceptIndexToBinaryPattern(conceptIndex);
			double[] inputValues = new double[getInputUnitsCount()];
			for (int i = 0; i < getInputUnitsCount(); i++) {
				inputValues[i] = concepts.get(conceptIndex).getAnswer(questions[i]).getValue();
			}

			// Add the values as a training set.
			trainingDataSet.addRow(inputValues, outputValues);
		}
	}

	/**
	 * Converts the specified array of binary values to its integer.
	 */
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

	/**
	 * Converts the specified integer to an array of it binary values.
	 */
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

	/**
	 * @return the number of input units.
	 */
	private int getInputUnitsCount() {
		return questions.length;
	}

	/**
	 * @return the number of output units.
	 */
	private int getOutputUnitsCount() {
		return (int) Math.ceil((Math.log(concepts.size()) / Math.log(2)));
	}
}
