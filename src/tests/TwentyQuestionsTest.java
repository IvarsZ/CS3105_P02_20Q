package tests;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.TwentyQuestionsModel;

import org.junit.Test;

import View.TwentyQuestionsView;

public class TwentyQuestionsTest {

	@Test
	public void q20Test1() throws FileNotFoundException {
		
		// Read the twenty questions file, train the network and get output.
		File inFile = new File("Part1-Input1.in");
		Scanner in = new Scanner(inFile);
		TwentyQuestionsModel q20Model = TwentyQuestionsView.read(in);
		in.close();
		q20Model.train();

		// Check that all concepts are correctly guessed.
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1}));
		assertEquals("Pig", q20Model.guessConcept(new double[]{1, 1, 1}));
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0}));
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0}));
		assertEquals("Candy", q20Model.guessConcept(new double[]{0, 0, 1}));
		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0}));
		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1}));
		assertEquals("Human", q20Model.guessConcept(new double[]{1, 1, 0}));
	}
	
	@Test
	public void q20Test2() throws FileNotFoundException {
		
		// Read the twenty questions file, train the network and get output.
		File inFile = new File("Part1-Input2.in");
		Scanner in = new Scanner(inFile);
		TwentyQuestionsModel q20Model = TwentyQuestionsView.read(in);
		in.close();
		q20Model.train();

		// Check that all concepts are correctly guessed.
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1}));
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0}));
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0}));
		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0}));
		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1}));
	}
}
