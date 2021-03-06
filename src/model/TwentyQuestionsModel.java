package model;
import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class TwentyQuestionsModel {
	
	private static final double MAX_TOTAL_ERROR = 0.0001;
	private static final int MAXIMUM_NUMBER_OF_RETRIES = 3;
	
	public static final double NO = 0;
	public static final double YES = 1;
	
	private static final double UNANSWERED = 0.5;
	public static final double UNKNOWN = 0.5;

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

	/**
	 * @return - a list of concepts that have not been excluded by the given answers.
	 */
	public ArrayList<Concept> possibleConcepts(ArrayList<Answer> answers) {
		
		ArrayList<Concept> possibleConcepts = new ArrayList<Concept>();

		// Set all answers to unanswered.
		double[] answerValues = new double[getInputUnitsCount()];
		for (int i = 0; i < answerValues.length; i++) {
			answerValues[i] = UNANSWERED;
		}
		
		// Set the known answers.
		for (int i = 0; i < answers.size(); i++) {
			answerValues[answers.get(i).getQuestion().getId()] = answers.get(i).getValue();
		}
		
		// Get the network output for the input from answers.
		neuralNetwork.setInput(answerValues);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		
		// Add all possible concepts to the list.
		for (int i = 0; i < networkOutput.length; i++) {
			if (isAnswerYes(networkOutput[i])) {
				possibleConcepts.add(concepts.get(i));
			}
		}
		
		return possibleConcepts;
	}
	
	/**
	 * Adds a concept to the system with the given answers and name.
	 */
	public Concept addConcept(String conceptName, ArrayList<Answer> answers) {

		// Add the concept.
		Concept concept = new Concept(conceptName, answers);
		concepts.add(concept);

		// Recreate the network and retrain.
		initializeNetwork();
		train();

		return concept;
	}
	
	/**
	 * Adds a question to the system, it has answer yes to the addedConcept and answer no to the guessedConcept,
	 * rest are unknown.
	 */
	public void addQuestion(String questionText, Concept guessedConcept, Concept addedConcept) {

		// Add question.
		int questionId = questions.size();
		questions.add(new Question(questionText, questionId));
		Question question = questions.get(questionId);

		// For each concept,
		for (Concept concept : concepts) {

			// add an answer to the added question.
			if (concept == guessedConcept) {
				concept.addAnswer(new Answer(question, NO));
			}
			else if (concept == addedConcept) {
				concept.addAnswer(new Answer(question, YES));
			}
			else {
				concept.addAnswer(new Answer(question, UNKNOWN));
			}
		}

		// Recreate the network and retrain.
		initializeNetwork();
		train();
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
	 * Resets the weights in the network to random values and retrains.
	 */
	public void resetNetwork() {
		neuralNetwork.randomizeWeights();
		train();
	}

	/**
	 * Writes the neural network to the file specified by the filename.
	 */
	public void writeNetworkToFile(String fileName) {
		neuralNetwork.save(fileName);
	}

	/**
	 * @return true if the network did not finish training.
	 */
	public boolean timedOut() {
		return lastIterationCount == maxIterations;
	}
	
	/**
	 * Hidden unit count getter.
	 */
	public int getHiddenUnitCount() {
		return hiddenUnitsCount;
	}

	/**
	 * Max iterations while training getter.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * Learning rate getter.
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * Momentum getter.
	 */
	public double getMomentum() {
		return momentum;
	}
	
	/**
	 * @return true if the answer is rounded to yes.
	 */
	private boolean isAnswerYes(double answerValue) {
		
		return answerValue >= 0.5; 
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

		// Create a training set for the network and reset it.
		initializeTrainingSets();
		resetNetwork();
	}

	/**
	 * Creates training sets to match questions and concepts.
	 */
	private void initializeTrainingSets() {

		// Create the training set and initialize it recursively.
		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		initializeTrainingSet(new double[getInputUnitsCount()], 0);
	}

	/**
	 * Recursive helper function for initializing training sets.
	 */
	private void initializeTrainingSet(double[] input, int indexToPermute) {

		// If there is nothing left to permute,
		if (indexToPermute >= input.length) {

			// For each output unit,
			double[] output = new double[getOutputUnitsCount()];
			for (int i = 0; i < getOutputUnitsCount(); i++) {
				
				// if its concept matches the input, 
				if (canMatch(input, concepts.get(i))) {
					
					// the concept is included.
					output[i] = YES;
				}
				else {
					
					// Otherwise it is excluded.
					output[i] = NO;
				}
			}
			
			// Add the training set and exit.
			trainingDataSet.addRow(input, output);
			return;
		}

		// Permute the entry at the given index and make a recursive call.
		for (double d = 0; d <= 1; d += 0.5) {

			double[] permutatedInput = Arrays.copyOf(input, input.length);
			permutatedInput[indexToPermute] = d;
			initializeTrainingSet(permutatedInput, indexToPermute + 1);
		}
	}

	/**
	 * @return - true if the given concept is not excluded by the given input.
	 */
	private boolean canMatch(double[] inputValue, Concept concept) {

		// For each input value,
		for (int i = 0; i < inputValue.length; i++) {

			// if it is opposite concept's answer value,
			double answerValue = concept.getAnswer(questions.get(i)).getValue();
			if ((inputValue[i] == YES && answerValue == NO) || (inputValue[i] == NO && answerValue == YES)) {
				
				// there is not match.
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Trains the neural network of the system.
	 */
	private void train() {
		
		// If no rule,
		if (backProgationRule == null) {
			
			// create it and set maximum total error.
			backProgationRule = new MomentumBackpropagation();
			backProgationRule.setMaxError(MAX_TOTAL_ERROR);
		}

		// Set the max iterations, learning rate and momentum.
		backProgationRule.setMaxIterations(maxIterations);
		backProgationRule.setLearningRate(learningRate);
		backProgationRule.setMomentum(momentum);
		
		// Train and save the iteration count.
		neuralNetwork.learn(trainingDataSet, backProgationRule);
		lastIterationCount = backProgationRule.getCurrentIteration();
		
		// While training keeps timing out,
		int retries = 0;
		while (timedOut() && retries < MAXIMUM_NUMBER_OF_RETRIES) {
			
			// increase the number of hidden units 4 times and train again.
			if (hiddenUnitsCount > 0) {
				hiddenUnitsCount *= 4;
			}
			else {
				hiddenUnitsCount = 1;
			}
			
			System.out.println("Increased number of hidden units to " + hiddenUnitsCount);
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
			neuralNetwork.randomizeWeights();
			neuralNetwork.learn(trainingDataSet, backProgationRule);
			lastIterationCount = backProgationRule.getCurrentIteration();
			
			retries++;
		}
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
