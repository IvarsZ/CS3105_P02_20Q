package tests;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import model.Answer;
import model.Concept;
import model.Question;
import model.Round;
import model.TwentyQuestionsModel;

import org.junit.Test;

import view.TwentyQuestionsView;


public class TwentyQuestionsTest {

	@Test
	public void testGuessAll() throws IOException {

		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input1.in");
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

		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input1.in");
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
	
	@Test
	public void earlyGuessTest() throws IOException {
		
		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input1.in");
		TwentyQuestionsModel model = view.getModel();
		
		// Human should be guessed with only two answers.
		Round game = new Round(model);
		game.addAnswer(new Answer(model.getQuestions().get(0), TwentyQuestionsModel.YES));
		game.addAnswer(new Answer(model.getQuestions().get(1), TwentyQuestionsModel.YES));
		assertEquals("Human", game.getGuessedConcept().getName());
	}
	
	@Test
	public void questionPriorityTest() throws IOException {
		
		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input2.in");
		TwentyQuestionsModel model = view.getModel();
		
		// Should ask is larger than a hand as it provides the most separation in eitheir case.
		Round game = new Round(model);
		assertEquals(model.getQuestions().get(1), game.nextQuestion());
		
		// Answers it, now should ask is it food.
		game.addAnswer(new Answer(model.getQuestions().get(1), TwentyQuestionsModel.YES));
		assertEquals(model.getQuestions().get(2), game.nextQuestion());
	}
	
	@Test
	public void addClashingConceptTest() throws IOException {

		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input1.in");
		TwentyQuestionsModel model = view.getModel();

		// Cabbage has all the same answers as Pumpkin.
		Concept pumpkin = model.getConcepts().get(3);
		ArrayList<Answer> answers = new ArrayList<Answer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {
			answers.add(pumpkin.getAnswer(model.getQuestions().get(i)));
		}
		Concept cabbage = model.addConcept("Cabbage", answers);
		
		// So there is a clash.
		assertEquals(true, cabbage.clashes(pumpkin));
		
		// Add a question separating them.
		model.addQuestion("Is it green?", pumpkin, cabbage);
		
		// Both can be guessed now.
		Round game = new Round(model);
		pumpkin = model.getConcepts().get(3);
		answers = new ArrayList<Answer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {
			game.addAnswer(pumpkin.getAnswer(model.getQuestions().get(i)));
		}
		assertEquals("Pumpkin", game.getGuessedConcept().getName());
		
		game = new Round(model);
		answers = new ArrayList<Answer>();
		for (int i = 0; i < model.getQuestions().size(); i++) {
			game.addAnswer(cabbage.getAnswer(model.getQuestions().get(i)));
		}
		assertEquals("Cabbage", game.getGuessedConcept().getName());
	}
	
	@Test
	public void gatherInfoTest() throws IOException {
		
		// After a correct guess asks the unknow answer question to gather info.
		TwentyQuestionsView view = new TwentyQuestionsView("Part2-Input2.in");
		TwentyQuestionsModel model = view.getModel();
		Round game = new Round(model);
		
		// answers that it is not larger than a hand and not food, so guesses it is a wristwatch.
		Concept wristwatch = model.getConcepts().get(0);
		game.addAnswer(wristwatch.getAnswer(model.getQuestions().get(1)));
		game.addAnswer(wristwatch.getAnswer(model.getQuestions().get(2)));
		assertEquals("Wristwatch", game.getGuessedConcept().getName());
		game.setGuessCorrect(true);
		
		// Now the game asks if it is physical. Answer it.
		assertEquals(model.getQuestions().get(3), game.nextQuestion());
		game.addAnswer(new Answer(model.getQuestions().get(3), TwentyQuestionsModel.YES));
		
		// Should have no more questions.
		assertEquals(null, game.nextQuestion());
	}

}
