import org.neuroph.core.NeuralNetwork;


public class Concept {
	
		private String name;
		private double[] answers;
		
		public Concept(String name, double[] answers) {
			this.name = name;
			this.answers = answers;

		}
		
		public String getName() {
			return name;
		}
		
		public double[] getAnswers() {
			return answers;
		}
		
		public double getAnswer(int questionIndex) {
			return answers[questionIndex];
		}
}
