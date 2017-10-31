package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
		
				
	@Test
	public void exp1() throws SyntaxException, LexicalException {
	String input = "Z-old";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_Binary.class, e.getClass());
	Expression_Binary ebin = (Expression_Binary)e;
	assertEquals(Expression_PredefinedName.class, ebin.e0.getClass());
	assertEquals(KW_Z, ((Expression_PredefinedName)(ebin.e0)).kind);
	assertEquals(Expression_Ident.class, ebin.e1.getClass());
	assertEquals("old", ((Expression_Ident)(ebin.e1)).name);
	assertEquals(OP_MINUS, ebin.op);
	}

	@Test
	public void exp2() throws SyntaxException, LexicalException {
	String input = "x";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_PredefinedName.class, e.getClass());	
	assertEquals(KW_x, ((Expression_PredefinedName)(e)).kind);	
	}
	
	@Test
	public void exp3() throws SyntaxException, LexicalException {
	String input = "X ? Y : Z";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_Conditional.class, e.getClass());
	Expression_Conditional ebin = (Expression_Conditional)e;
	assertEquals(Expression_PredefinedName.class, ebin.condition.getClass());
	assertEquals(KW_X, ((Expression_PredefinedName)(ebin).condition).kind);
	assertEquals(Expression_PredefinedName.class, ebin.trueExpression.getClass());
	assertEquals(KW_Y, ((Expression_PredefinedName)(ebin).trueExpression).kind);
	assertEquals(Expression_PredefinedName.class, ebin.falseExpression.getClass());
	assertEquals(KW_Z, ((Expression_PredefinedName)(ebin).falseExpression).kind);
	}	
	
}
