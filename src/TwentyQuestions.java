import java.io.IOException;

import view.TwentyQuestionsView;



public class TwentyQuestions {

	/**
	 * Entry point for part1.
	 */
	public static void main(String[] args) throws IOException {
		
		
		// TODO args for file to use and experimenting.
		// TODO cleanup, decide on output.
		TwentyQuestionsView q20 = new TwentyQuestionsView("Part1-Input1.in");
		//System.out.println(q20.getModel().timedOut());
		q20.print();
		//q20.experimentWithTrainingParameters();
		//q20.writeNetworkToFile("Part1-Output1.net");
		q20.play();
		q20.print();
	}
}
