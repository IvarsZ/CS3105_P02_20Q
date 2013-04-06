import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

public class NeurophTest {
	
	private class Concept {
		
		public String name;
		public double[] answers;
		
		public Concept(String name, double[] answers) {
			this.name = name;
			this.answers = answers;

		}
	}
	
	public NeurophTest()
	{
		
		String[] questions = {"Has it ever been alive?", "Is it larger than a hand", "Is it food"};
		
		Concept[] concepts = new Concept[8];
		concepts[0] = new Concept("Wristwatch", new double[]{0, 0, 0});
		concepts[1] = new Concept("Candy", 		new double[]{0, 0, 1});
		concepts[2] = new Concept("Chair", 		new double[]{0, 1, 0});
		concepts[3] = new Concept("Pumpkin", 	new double[]{0, 1, 1});
		concepts[4] = new Concept("Ant", 		new double[]{1, 0, 0});
		concepts[5] = new Concept("Oyster", 	new double[]{1, 0, 1});
		concepts[6] = new Concept("Human", 		new double[]{1, 1, 0});
		concepts[7] = new Concept("Pig", 		new double[]{1, 1, 1});
		
		// TODO explain why sigmoid.
		NeuralNetwork neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 3);
		
		DataSet dataSet = new DataSet(3, 3);
		for (int i = 0; i < concepts.length; i++) {
			dataSet.addRow(new double[]{concepts[i].answers[0], concepts[i].answers[1], concepts[i].answers[2]},
						   intToBitArray(i, 3));
		}
		
		BackPropagation bp = new BackPropagation();
		bp.setMaxIterations(10000);
		neuralNetwork.learn(dataSet, bp);
		
		
		for (int i = 0; i < concepts.length; i++) {
			neuralNetwork.setInput(concepts[i].answers);
			neuralNetwork.calculate();
			double[] networkOutput = neuralNetwork.getOutput();
			for (int j = 0; j < networkOutput.length; j++) {
				System.out.print(networkOutput[j] + " ");
			}
			System.out.println();
		}
	}
	
	private static double[] intToBitArray(int number, int length) {

		// TODO reverse order.
		double[] bits = new double[length];
	    for (int i = length - 1; i >= 0; i--) {
	    	
	    	if ((number & (1 << i)) != 0) {
	    		bits[i] = 1;
	    	}
	    }
	    
	    return bits;
	}

	public static void main(String[] args)
	{
		new NeurophTest();
	}
}
