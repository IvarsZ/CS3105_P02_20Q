import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import view.TwentyQuestionsView;



public class TwentyQuestions {

	/**
	 * Entry point for part1.
	 */
	public static void main(String[] args) throws IOException {
		
		
		// TODO args for file to use and experimenting.
		// TODO cleanup, decide on output.
		TwentyQuestionsView q20 = new TwentyQuestionsView("Part2-1.txt");
		//System.out.println(q20.getModel().timedOut());
		//q20.print();
		//q20.experimentWithTrainingParameters();
		//q20.writeNetworkToFile("Part1-Output1.net");
		q20.play();
		//q20.print();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File("Part2-1.txt")));
		q20.writeToFile(out);
		out.close();
	}
}
