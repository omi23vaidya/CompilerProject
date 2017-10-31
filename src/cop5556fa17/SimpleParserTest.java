package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

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
	 * Simple test case with an empty program.  This test 
	 * expects an SyntaxException because all legal programs must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testInvalid() throws LexicalException, SyntaxException {
		String input = "Om!ar;";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testInvalid2() throws LexicalException, SyntaxException {
		String input = "xyz=dmg;";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testInvalid3() throws LexicalException, SyntaxException {
		String input = "prog @expr k=12;";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	
	/** Another example.  This is a legal program and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog prog[[x,y]] = false;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDec2() throws LexicalException, SyntaxException {
		String input = "Prog prog = sin(a+b);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDec2_1() throws LexicalException, SyntaxException {
		String input = "prog image_name = false ;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "prog true !*= false";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
	@Test
	public void testDec4() throws LexicalException, SyntaxException {
		String input = "prog int k\n;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDec5() throws LexicalException, SyntaxException {
		String input = "prog int a; + boolean b;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
		
	@Test
	public void testDec7() throws LexicalException, SyntaxException {
		String input = "; $1__myProg__";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
	@Test
	public void testDec8() throws LexicalException, SyntaxException {
		String input = "omkar int UF = + - x ? 23 : sin (A);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		//thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
		
	@Test
	public void testDec10() throws LexicalException, SyntaxException {
		String input = "omkar boolean Mumbai;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		//thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
	
	@Test
	public void testDec12() throws LexicalException, SyntaxException {
		String input = "omkar boolean Mumbai = - + !(Y) ? true : polar_a [x,r];";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		//thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
	@Test
	public void testDec13() throws LexicalException, SyntaxException {
		String input = "omkar boolean Mumbai = - + !(Y) ? true : polar_a [x,r);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		} catch (SyntaxException e)
		{
			show(e);
			throw(e);
		}
	}
	
	
	
	@Test
	public void VariableDeclaration_02() throws LexicalException, SyntaxException {
		String input = "image ident1 <- x";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.ImageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void sourceSinkDeclaration() throws LexicalException, SyntaxException {
		String input = "file "; 
		
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			thrown.expect(SyntaxException.class);
			try {
				parser.SourceSinkDeclaration();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
		
	}

	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression2() throws SyntaxException, LexicalException {
		String input = "a+b;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression3() throws SyntaxException, LexicalException {
		String input = "a + b / c";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression4() throws SyntaxException, LexicalException {
		String input = "true === false";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try{
		parser.expression();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
		//Call expression directly.  
	}
	
	@Test
	public void expression5() throws SyntaxException, LexicalException {
		String input = "sin(cos(atan(abs(++true)))) & abs[true>x, y!=false]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression6() throws SyntaxException, LexicalException {
		String input = "true *= false";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		//parser.expression();  //Call expression directly.
		try{
			parser.expression();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void expression7() throws SyntaxException, LexicalException {
		String input = "true !*= false";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		//parser.expression();  //Call expression directly.
		try{
			parser.expression();
			parser.matchEOF_test();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void expression8() throws SyntaxException, LexicalException {
		String input = "x | @";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		//parser.expression();  //Call expression directly.
		try{
			parser.expression();
			parser.matchEOF_test();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void expression9() throws SyntaxException, LexicalException {
		String input = "sin(a+b)]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression10() throws SyntaxException, LexicalException {
		String input = "( (tyrion==dead) ? false : what_the_heck? )";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		//parser.expression();  //Call expression directly.
		try{
			parser.expression();
			parser.matchEOF_test();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void expression11() throws SyntaxException, LexicalException {
		String input = "a:b:c";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();//Call expression directly.  
	}
	
	@Test
	public void expression12() throws SyntaxException, LexicalException {
		String input = "2=";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try{
		parser.expression();//Call expression directly.  
		parser.matchEOF_test();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression13() throws SyntaxException, LexicalException {
		String input = "+!-x +!-x +!-x +!-x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();//Call expression directly.  
		parser.matchEOF_test();
	}
	
	//----------------------------------------------------------------------------------------------------
	
	
	}



