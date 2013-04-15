package model;
import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import sun.security.krb5.Asn1Exception;

public class TwentyQuestionsModel {

	private ArrayList<Question> questions;
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
	public TwentyQuestionsModel(ArrayList<Concept> concepts, ArrayList<Question> questions, int hiddenUnitsCount, int maxIterations, double learningRate, double momentum) {

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
	public ArrayList<Question> getQuestions() {
		return questions;
	}

	/**
	 * Getter for the concepts of the system.
	 */
	public ArrayList<Concept> getConcepts() {
		return concepts;
	}

	public ArrayList<Concept> possibleConcepts(ArrayList<Answer> answers) {
		
		ArrayList<Concept> possibleConcepts = new ArrayList<Concept>();

		// Get the network output and convert it to a concept index.
		double[] answerValues = new double[getInputUnitsCount()];
		for (int i = 0; i < answerValues.length; i++) {
			answerValues[i] = 0.5; // TODO magic constants.
		}
		
		for (int i = 0; i < answers.size(); i++) {
			answerValues[answers.get(i).getQuestion().getId()] = answers.get(i).getValue();
		}
		
		neuralNetwork.setInput(answerValues);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		
		for (int i = 0; i < networkOutput.length; i++) {
			
			if (networkOutput[i] >= 0.5) {
				possibleConcepts.add(concepts.get(i));
			}
		}
		
		// TODO what if no answer.
		return possibleConcepts;
	}

	/**
	 * @return the system's guess of the concept based on the given answers.
	 */
	public Concept guessConcept(ArrayList<Answer> answers) {


		// Get the network output and convert it to a concept index.
		double[] answerValues = new double[answers.size()];
		for (int i = 0; i < answers.size(); i++) {
			answerValues[i] = answers.get(i).getValue();
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
		int conceptIndex = 0; // TODO binaryPatternToConceptIndex(networkOutput);

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
			ArrayList<Answer> answers = concepts.get(i).getAnswers();
			input[i] = new double[answers.size()];
			for (int j = 0; j < answers.size(); j++) {
				input[i][j] = answers.get(j).getValue();
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
			// TODO output[i] = conceptIndexToBinaryPattern(i);
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

	public Concept addConcept(String conceptName, ArrayList<Answer> answers) {

		// Add the concept.
		Concept concept = new Concept(conceptName, answers);
		concepts.add(concept);

		initializeNetwork();
		train();

		/*
		// Calculate its output values from the index and input values from the answers.
		double[] outputValues = conceptIndexToBinaryPattern(concepts.size() - 1);
		double[] inputValues = new double[getInputUnitsCount()];
		for (int i = 0; i < getInputUnitsCount(); i++) {
			inputValues[i] = concepts.get(concepts.size() - 1).getAnswer(questions.get(i)).getValue();
		}

		// If there's too many concepts
		while (concepts.size() > Math.pow(2, neuralNetwork.getOutputsCount())) {

			// recreate the network.
			initializeNetwork();
		}

		// Add the values as a training set.
		trainingDataSet.addRow(inputValues, outputValues);

		train();
		 */

		return concept;
	}

	public boolean timedOut() {
		return lastIterationCount == maxIterations;
	}

	public void addQuestion(String questionText, Concept guessedConcept, Concept addedConcept) {

		// Add question.
		int questionId = questions.size();
		questions.add(new Question(questionText, questionId));
		Question question = questions.get(questionId);

		// For each concept,
		for (Concept concept : concepts) {

			// add an answer to the added question.
			if (concept == guessedConcept) {
				concept.addAnswer(new Answer(question, 0));
			}
			else if (concept == addedConcept) {
				concept.addAnswer(new Answer(question, 1));
			}
			else {
				concept.addAnswer(new Answer(question, 0.5));
			}
		}

		// Recreate the network and retrain.
		initializeNetwork();
		train();
	}

	/**
	 * Creates the used neural network.
	 */
	private void initializeNetwork() {

		// Set the hidden unit count if it's positive.
		if (hiddenUnitsCount < 1) {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, questions.size(), getOutputUnitsCount());
		}
		else {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
		}

		// Randomize weights and create a training set for the network.
		neuralNetwork.randomizeWeights();
		initializeTrainingSets();
	}

	private void initializeTrainingSets() {

		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		initializeTrainingSet(new double[getInputUnitsCount()], 0);

		/*
		// For each concept,
		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		for (int conceptIndex = 0; conceptIndex < concepts.size(); conceptIndex++) {



			// calculate its output values from the index and input values from the answers.
			double[] outputValues = conceptIndexToBinaryPattern(conceptIndex);
			ArrayList<Answer> answers = concepts.get(conceptIndex).getAnswers();
			double[] inputValues = new double[answers.size()];
			for (int j = 0; j < answers.size(); j++) {
				inputValues[answers.get(j).getQuestion().getId()] = answers.get(j).getValue();
			}

			// Add the values as a training set.
			trainingDataSet.addRow(inputValues, outputValues);
		}
		 */

		// TODO remove.
		for (DataSetRow row : trainingDataSet.getRows()) {

			for (double d : row.getInput()) {
				System.out.print(d + " ");
			}
			System.out.print(" || ");
			for (double d : row.getDesiredOutput()) {
				System.out.print(d + " ");
			}
			System.out.println();
		}
	}

	private void initializeTrainingSet(double[] input, int indexToPermute) {

		// If there is nothing left to permute,
		if (indexToPermute >= input.length) {

			// add a training set.
			double[] output = new double[getOutputUnitsCount()];
			for (int i = 0; i < getOutputUnitsCount(); i++) {
				if (canMatch(input, concepts.get(i))) {
					output[i] = 1;
				}
				else {
					output[i] = 0;
				}
			}
			trainingDataSet.addRow(input, output);
			return;
		}

		// Otherwise permute the entry at the given index and make a recursive call.
		for (double d = 0; d <= 1; d += 0.5) {

			double[] permutatedInput = Arrays.copyOf(input, input.length);
			permutatedInput[indexToPermute] = d;
			initializeTrainingSet(permutatedInput, indexToPermute + 1);
		}
	}

	private boolean canMatch(double[] input, Concept concept) {

		for (int i = 0; i < input.length; i++) {

			double answerValue = concept.getAnswer(questions.get(i)).getValue();
			if ((input[i] == 1 && answerValue == 0) || (input[i] == 0 && answerValue == 1)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the number of input units.
	 */
	private int getInputUnitsCount() {
		return questions.size();
	}

	/**
	 * @return the number of output units.
	 */
	private int getOutputUnitsCount() {
		return concepts.size();
	}
}
