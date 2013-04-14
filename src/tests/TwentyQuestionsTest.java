package tests;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.TwentyQuestionsModel;

import org.junit.Test;

import view.TwentyQuestionsView;


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
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1}).getName());
		//assertEquals("Pig", q20Model.guessConcept(new double[]{1, 1, 1}).getName());
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0}).getName());
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0}).getName());
		assertEquals("Candy", q20Model.guessConcept(new double[]{0, 0, 1}).getName());
		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0}).getName());
		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1}).getName());
		assertEquals("Human", q20Model.guessConcept(new double[]{1, 1, 0}).getName());
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
		assertEquals("Pumpkin", q20Model.guessConcept(new double[]{0, 1, 1}).getName());
		assertEquals("Chair", q20Model.guessConcept(new double[]{0, 1, 0}).getName());
		assertEquals("Wristwatch", q20Model.guessConcept(new double[]{0, 0, 0}).getName());
		assertEquals("Ant", q20Model.guessConcept(new double[]{1, 0, 0}).getName());
		assertEquals("Oyster", q20Model.guessConcept(new double[]{1, 0, 1}).getName());
	}
	
	/**
	 * Less questions test.
	 */
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
}
