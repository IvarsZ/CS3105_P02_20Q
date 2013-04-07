import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

// TODO add momentum, add support for wrong answers.
public class TwentyQuestionsModel {
	
	private BackPropagation backProgationRule;
	private Concept[] concepts;
	
	private NeuralNetwork neuralNetwork;
	
	private String[] questions;
	private DataSet trainingDataSet;
	
	public TwentyQuestionsModel(Concept[] concepts, String[] questions, int hiddenUnitsCount, int maxIterations, double learningRate) {

		this.concepts = concepts;
		this.questions = questions;

		if (hiddenUnitsCount < 1) {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, questions.length, getOutputUnitsCount());
		}
		else {
			neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, getInputUnitsCount(), hiddenUnitsCount, getOutputUnitsCount());
		}
		
		// Learning rule used by the neural network.
		backProgationRule = new BackPropagation();
		backProgationRule.setMaxIterations(maxIterations);
		backProgationRule.setLearningRate(learningRate);

		trainingDataSet = new DataSet(getInputUnitsCount(), getOutputUnitsCount());
		for (int conceptIndex = 0; conceptIndex < concepts.length; conceptIndex++) {

			double[] inputValues = new double[getInputUnitsCount()];
			double[] outputValues = conceptIndexToBinaryPattern(conceptIndex);
			for (int i = 0; i < getInputUnitsCount(); i++) {
				inputValues[i] = concepts[conceptIndex].getAnswer(i);
			}

			trainingDataSet.addRow(inputValues, outputValues);
		}
	}
	

	public String[] getQuestions() {
		return questions;
	}

	public String guessConcept(double[] answers) {
		
		// Get the answer.
		neuralNetwork.setInput(answers);
		neuralNetwork.calculate();
		double[] networkOutput = neuralNetwork.getOutput();
		int conceptIndex = binaryPatternToConceptIndex(networkOutput);
		return concepts[conceptIndex].getName();
	}
	
	public void train() {
		
		neuralNetwork.learn(trainingDataSet, backProgationRule);
	}
	
	public String conceptsToString() {

		String conceptsString = "";
		for (Concept concept : concepts) {
			conceptsString += concept.getName() + ", ";
		}

		return conceptsString;
	}
	
	// TODO add input method too and print both.
	public double[][] getOutput() {
		
		double[][] output = new double[concepts.length][];
		for (int i = 0; i < concepts.length; i++) {
			neuralNetwork.setInput(concepts[i].getAnswers());
			neuralNetwork.calculate();
			output[i] = neuralNetwork.getOutput();
		}
		
		return null;
	}
	
	public Concept[] getConcepts() {
		return concepts;
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
