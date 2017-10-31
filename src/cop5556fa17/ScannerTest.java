/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIdentifier() throws LexicalException {
		String input = "polar_a";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_polar_a, 0, 7, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIdentifierWithNewLine() throws LexicalException {
		String input = "polar_a x\r\n XYZ";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_polar_a, 0, 7, 1, 1);
		checkNext(scanner, KW_x, 8, 1, 1, 9);
		checkNext(scanner, IDENTIFIER, 12, 3, 2, 2);		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void greaterThan() throws LexicalException {
		String input = "x>X";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_x, 0, 1, 1, 1);
		checkNext(scanner, OP_GT, 1, 1, 1, 2);
		checkNext(scanner, KW_X, 2, 1, 1, 3);		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void lessThan() throws LexicalException {
		String input = "x<=X";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_x, 0, 1, 1, 1);
		checkNext(scanner, OP_LE, 1, 2, 1, 2);
		checkNext(scanner, KW_X, 3, 1, 1, 4);		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void exclamation() throws LexicalException {
		String input = "0!=\nABS";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, OP_NEQ, 1, 2, 1, 2);
		checkNext(scanner, IDENTIFIER, 4, 3, 2, 1);		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void question() throws LexicalException {
		String input = "0?=\r\nABS";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, OP_Q, 1, 1, 1, 2);
		checkNext(scanner, OP_ASSIGN, 2, 1, 1, 3);
		checkNext(scanner, IDENTIFIER, 5, 3, 2, 1);		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void comment() throws LexicalException {
		String input = "0abs//a\n%";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, KW_abs, 1, 3, 1, 2);
		checkNext(scanner, OP_MOD, 8, 1, 2, 1);		
		checkNextIsEOF(scanner);
	}
	
//	@Test
//	public void checkStringLiteral() throws LexicalException {
//		String input = "\" hello world \"";
//		Scanner scanner = new Scanner(input).scan();
//		show(input);
//		show(scanner);
//		checkNext(scanner, IDENTIFIER, 0, 2, 1, 1);		
//		checkNext(scanner, INTEGER_LITERAL, 2, 1, 1, 3);		
//		checkNext(scanner, INTEGER_LITERAL, 3, 2, 1, 4);		
//		checkNext(scanner, STRING_LITERAL, 6, 9, 1, 14);		
//		checkNextIsEOF(scanner);
//	}
	
	@Test
	public void checkIntegerRange() throws LexicalException {
		String input = "999999999999999999999999999";
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(27,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void stringValidation_2() throws LexicalException {
		String input = "x=\"\b\";";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_x, 0, 1, 1, 1);
		checkNext(scanner, OP_ASSIGN, 1, 1, 1, 2);
		checkNext(scanner, STRING_LITERAL, 2, 3, 1, 3);
		checkNext(scanner, SEMI, 5, 1, 1, 6);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void stringValidation_3() throws LexicalException {
		String input = "\"abc\\\"a\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 8, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void stringValidation_4() throws LexicalException {
		String input = "\"abc\t\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void stringValidation_5() throws LexicalException {
		String input = "\"abc\b\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	
	@Test
	public void integerLiteralCheck() throws LexicalException {
		String input = "01\n29";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 1, 1, 2);
		checkNext(scanner, INTEGER_LITERAL, 3, 2, 2, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void integerLiteralCheck_2() throws LexicalException {
		String input = "25.20";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void invalidCharacters() throws LexicalException {
		String input = "abc`";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(3,e.getPos());
			throw e;
		}
	}
	
	
	
	@Test
	public void integerStringLiteralCheck() throws LexicalException {
		String input = "23$_";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 2, 1, 1);
		checkNext(scanner, IDENTIFIER, 2, 2, 1, 3);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void booleanIntegerLiteral() throws LexicalException {
		String input = "true10";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 6, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failUnclosedStringLiteral_2() throws LexicalException {
		String input = "\" greetings \\ \"";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failedStringLiteral_2() throws LexicalException {
		String input = "greet\\ings";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(5,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failedStringLiteral_3() throws LexicalException {
		String input = "\"hello\\ ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(7,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failedIntegerLiteral() throws LexicalException {
		String input = "25.20";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
	}


}
