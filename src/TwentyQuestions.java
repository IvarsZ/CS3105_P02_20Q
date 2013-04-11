import java.io.IOException;

import View.TwentyQuestionsView;


public class TwentyQuestions {

	/**
	 * Entry point for part1.
	 */
	public static void main(String[] args) throws IOException {
		
		TwentyQuestionsView q20 = new TwentyQuestionsView("Part1-Input1.in");
		q20.print();
		q20.play();
	}
}
