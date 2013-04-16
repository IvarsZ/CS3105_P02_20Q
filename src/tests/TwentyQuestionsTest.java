package tests;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import model.Answer;
import model.Round;
import model.TwentyQuestionsModel;

import org.junit.Test;

import view.TwentyQuestionsView;


public class TwentyQuestionsTest {

	@Test
	public void testGuessAll() throws IOException {

		TwentyQuestionsView view = new TwentyQuestionsView("Part1-Input1.in");
		TwentyQuestionsModel model = view.getModel();

		// Check that all concepts are correctly guessed.
		String[] concepts = new String[]{"Wristwatch", "Candy", "Chair", "Pumpkin", "Ant", "Oyster", "Human"};
		for (int i = 0; i < concepts.length; i++) {

			Round game = new Round(model);
			game.addAnswer(new Answer(model.getQuestions().get(2), i % 2));
			game.addAnswer(new Answer(model.getQuestions().get(1), (i % 4) / 2));
			game.addAnswer(new Answer(model.getQuestions().get(0), i/4));
			assertEquals(concepts[i], game.getGuessedConcept().getName());
		}
	}

	@Test
	public void addConceptTest() throws IOException {

		TwentyQuestionsView view = new TwentyQuestionsView("Part1-Input1.in");
		TwentyQuestionsModel model = view.getModel();

		// Add pig which answers yes to all questions.
		ArrayList<Answer> answers = new ArrayList<Answer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {
			answers.add(new Answer(model.getQuestions().get(i), 1));
		}
		model.addConcept("Pig", answers);

		// Check that all concepts are correctly guessed.
		String[] concepts = new String[]{"Wristwatch", "Candy", "Chair", "Pumpkin", "Ant", "Oyster", "Human", "Pig"};
		for (int i = 0; i < concepts.length; i++) {

			Round game = new Round(model);
			game.addAnswer(new Answer(model.getQuestions().get(2), i % 2));
			game.addAnswer(new Answer(model.getQuestions().get(1), (i % 4) / 2));
			game.addAnswer(new Answer(model.getQuestions().get(0), i/4));
			assertEquals(concepts[i], game.getGuessedConcept().getName());
		}
	}

	/**
	 * Less questions test.
	 */
	/*
	@Test
	public void q20Test3() throws FileNotFoundException {

		// Read the twenty questions file, train the network and get output.
		File inFile = new File("Part1-Input3.in");
		Scanner in = new Scanner(inFile);
		TwentyQuestionsModel q20Model = TwentyQuestionsView.read(in);
		in.close();
		q20Model.train();

		// Check that all concepts are correctly guessed.
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0}).getName());
		assertEquals("Candy", q20Model.guessConcept(new double[]{0, 0, 1}).getName());
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0}).getName());
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1}).getName());

		// Even when one of the answers is wrong.
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{1, 0, 0}).getName());
		assertEquals("Candy", q20Model.guessConcept(new double[]{1, 0, 1}).getName());
		assertEquals("Chair", q20Model.guessConcept(new double[]{1, 1, 0}).getName());
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{1, 1, 1}).getName());
	}

	@Test
	public void addQuestionTest() throws IOException {

		// Read the twenty questions file, train the network and get output.
		TwentyQuestionsView view = new TwentyQuestionsView("Part1-Input1.in");
		TwentyQuestionsModel q20Model = view.getModel();
		q20Model.train();

		ArrayList<Question> questions = q20Model.getQuestions();
		ArrayList<Answer> answers = new ArrayList<Answer>();
		for (int i = 0; i < questions.size(); i++) {

			answers.add(new Answer(questions.get(i), 1));
		}
		q20Model.addConcept("Pig", answers);

		answers = new ArrayList<Answer>();
		answers.add(new Answer(questions.get(0), 0));
		answers.add(new Answer(questions.get(1), 1));
		answers.add(new Answer(questions.get(2), 1));
		Concept addedConcept = q20Model.addConcept("Cabbage", answers);
		q20Model.addQuestion("Is it green?", q20Model.getConcepts().get(3), addedConcept);

		// Check that all concepts are correctly guessed for all possible inputs.
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1, 0}).getName());
		assertEquals("Cabbage", q20Model.guessConcept(new double[]{0, 1, 1, 1}).getName());

		assertEquals("Pig", q20Model.guessConcept(new double[]{1, 1, 1, 0}).getName());
		assertEquals("Pig", q20Model.guessConcept(new double[]{1, 1, 1, 1}).getName());

		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0, 0}).getName());
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0, 1}).getName());

		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0, 0}).getName());
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0, 1}).getName());

		assertEquals("Candy", q20Model.guessConcept(new double[]{0, 0, 1, 0}).getName());
		assertEquals("Candy", q20Model.guessConcept(new double[]{0, 0, 1, 1}).getName());

		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0, 0}).getName());
		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0, 1}).getName());

		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1, 0}).getName());
		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1, 1}).getName());

		assertEquals("Human", q20Model.guessConcept(new double[]{1, 1, 0, 0}).getName());
		assertEquals("Human", q20Model.guessConcept(new double[]{1, 1, 0, 1}).getName());
	}
	 */

}
