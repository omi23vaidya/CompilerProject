package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		ASTNode ast;
		ArrayList<ASTNode> dAndS = new ArrayList<ASTNode>();
		Token firstToken = t;
		Token name;
		if(t.kind == IDENTIFIER)
		{
			name = t;
			check(Kind.IDENTIFIER);//Doubt: how to handle kleene star?			
		}
		else throw new SyntaxException(t, "Invalid Start to the program");
		
		while(t.kind == Kind.KW_int 
				|| t.kind == Kind.KW_boolean 
				|| t.kind == Kind.KW_image
				|| t.kind == Kind.KW_url
				|| t.kind == Kind.KW_file
				|| t.kind == Kind.IDENTIFIER)
		{
			ast = Program_Part();
			if(ast instanceof Declaration || ast instanceof Statement)
			{
				dAndS.add(ast);
			}
		}
		//Doubt -- write condition for kleene star
		//throw new UnsupportedOperationException();
		return new Program(firstToken, name, dAndS);
	}

	public ASTNode Program_Part() throws SyntaxException{
		Declaration d;
		Statement s;
		
		if(t.kind == Kind.KW_int 
				|| t.kind == Kind.KW_boolean 
				|| t.kind == Kind.KW_image
				|| t.kind == Kind.KW_url
				|| t.kind == Kind.KW_file)
		{
			d = Declaration();
			check(Kind.SEMI);
			return d;
		}
		else if(t.kind == Kind.IDENTIFIER)
		{
			s = Statement();
			check(Kind.SEMI);
			return s;
		}
		else throw new SyntaxException(t, "Invalid character after first identifier");
	}
	
	public Statement Statement() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Statement receive;
		check(Kind.IDENTIFIER);
		if(t.kind == Kind.LSQUARE
				|| t.kind == Kind.OP_RARROW
				|| t.kind == Kind.OP_LARROW
				|| t.kind == Kind.OP_ASSIGN)
		{
			receive = StatementPart(); 
			if(receive instanceof Statement_Assign){
				Index tempIndex;
				if(((Statement_Assign) receive).lhs == null)
				{
					tempIndex = null;
				} else
				{
					tempIndex = ((Statement_Assign) receive).lhs.index;					
				}
				LHS temp = new LHS(firstToken, firstToken, tempIndex);
				return new Statement_Assign(firstToken, temp, ((Statement_Assign) receive).e);				
			}
			else if( receive instanceof Statement_Out) 
			{
				return new Statement_Out(firstToken, firstToken, ((Statement_Out) receive).sink);
			} else
			{
				Source sourceTemp = ((Statement_In) receive).source;
				return new Statement_In(firstToken, firstToken, sourceTemp);
			}
			
		}
		else throw new SyntaxException(t, "Invalid character in statement after IDENTIFIER");
	}

	public Statement StatementPart() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Statement_Assign s1;
		Statement_Out s2;
		//Statement_In s3;
		Source s4;
		if(t.kind == Kind.LSQUARE || t.kind == Kind.OP_ASSIGN)
		{
			s1 = AssignPart();
			return s1;
		}
		else if(t.kind == Kind.OP_RARROW)
		{
			s2 = ImageOutPart();
			return s2;
		}
		else if(t.kind == Kind.OP_LARROW)
		{
			s4 = ImageInPart();
			return new Statement_In(firstToken, firstToken, s4); //check
		}
		else throw new SyntaxException(t, "Invalid in StatementPart");
		
	}

	public Statement_Assign AssignPart() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		LHS s0 = null;
		Index s1;
		Statement_Assign s2;
		Expression s3;
		if(t.kind == Kind.LSQUARE)
		{
			s1 = LHSPart();
			s0 = new LHS(firstToken, t, s1); //check
		}
		else {
			s0 = null;
		}
		check(Kind.OP_ASSIGN);
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				){
			s3 = expression();
		}
		else throw new SyntaxException(t, "Error in AssignPart");
		return new Statement_Assign(firstToken,s0,s3);
	}

	public Index LHSPart() throws SyntaxException {
		// TODO Auto-generated method stub
		Index s0;
		check(Kind.LSQUARE);
		if(t.kind == Kind.LSQUARE)
		{
			s0 = LHSSelector();			
		}
		else throw new SyntaxException(t, "Error in LHSPart");
		check(Kind.RSQUARE);
		
		return s0;
	}

	public Index LHSSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index s0;
		
		check(Kind.LSQUARE);
		if(t.kind == Kind.KW_x || t.kind == Kind.KW_r)
		{
			s0 = SelectorBody();			
		}
		else throw new SyntaxException(t, "Error in LHSSelector");
		check(Kind.RSQUARE);
		return s0;
	}
	

	public Index SelectorBody() throws SyntaxException {
		// TODO Auto-generated method stub
		Index s0;
		
		if(t.kind == Kind.KW_x)
		{
			s0 = XYSelector();
			return s0;
		}
		else if(t.kind == Kind.KW_r)
		{
			s0 = RaSelector();
			return s0;
		}
		else throw new SyntaxException(t, "Invalid in SelectorBody");
		
	}

	public Index RaSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression_PredefinedName e1;
		Token firstToken;
		Expression_PredefinedName e2;
		firstToken = t;
		e1 = new Expression_PredefinedName(firstToken, Kind.KW_r);		
		check(Kind.KW_r);
		
		//op = t;
		check(Kind.COMMA);
		
		e2 = new Expression_PredefinedName(t, Kind.KW_A);
		check(Kind.KW_A);
		
		Index i = new Index(firstToken, e1, e2); //Doubt hw3 -- firstToken??
		return i;
	}

	public Index XYSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression_PredefinedName e1;
		Token firstToken;
		Expression_PredefinedName e2;
		firstToken = t;
		e1 = new Expression_PredefinedName(firstToken, Kind.KW_x);
		check(Kind.KW_x);
		
		//op = t;
		check(Kind.COMMA);
		
		e2 = new Expression_PredefinedName(t, Kind.KW_y);
		check(Kind.KW_y);
		
		Index i = new Index(firstToken, e1, e2); //Doubt hw3 -- firstToken is what here?
		return i;
		
	}
	
	public Statement_Out ImageOutPart() throws SyntaxException
	{
		Token firstToken = t;
		Sink s0;
		check(Kind.OP_RARROW);
		if(t.kind == Kind.IDENTIFIER || t.kind == Kind.KW_SCREEN)
		{
			s0 = Sink();
			return new Statement_Out(firstToken, firstToken,s0);
		}
		else throw new SyntaxException(t, "Invalid after RARROW in ImageOut");
	}

	public Sink Sink() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Sink_Ident s1;
		Sink_SCREEN s2;
		if(t.kind == Kind.IDENTIFIER)
		{
			s1 = new Sink_Ident(firstToken, t);
			check(Kind.IDENTIFIER);
			return s1;
		}
		else if(t.kind == Kind.KW_SCREEN)
		{
			s2 = new Sink_SCREEN(firstToken);
			check(Kind.KW_SCREEN);
			
			return s2;
		}
		else throw new SyntaxException(t, "Invalid in Sink");
		
	}
	
	public Source ImageInPart() throws SyntaxException
	{
		Source s1;
		check(Kind.OP_LARROW);
		if(t.kind == Kind.STRING_LITERAL
				|| t.kind == Kind.OP_AT
				|| t.kind == Kind.IDENTIFIER)
		{
			s1 = Source();
			return s1;
		}
		else throw new SyntaxException(t, "Invalid in ImageInPart after OP_LARROW");
	}

	public Source Source() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Source_StringLiteral s1;
		Expression s2;
		
		if(t.kind == Kind.STRING_LITERAL)
		{	s1 = new Source_StringLiteral(firstToken, t.getText());
			check(Kind.STRING_LITERAL);
			return s1;
		}
		else if(t.kind == Kind.OP_AT)
		{
			Token firstTokenInside = t;
			check(Kind.OP_AT);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
					s2 = expression();
					} else throw new SyntaxException(t, "Error in Source at Expression");
			return new Source_CommandLineParam(firstTokenInside, s2); //*************check********firsttoken********hw3
		}
		else if(t.kind == Kind.IDENTIFIER)
		{	Token name = t;		
			check(Kind.IDENTIFIER);	
			return new Source_Ident(name, name); //*************check********firsttoken********hw3
		}			
		else throw new SyntaxException(t, "Invalid in Source");
	}

	public Declaration Declaration() throws SyntaxException
	{
		Declaration_Variable d1;
		Declaration_Image d2;
		Declaration_SourceSink d3;
		if(t.kind == Kind.KW_int 
				|| t.kind == Kind.KW_boolean)
		{
			d1 = VariableDeclaration();
			return d1;
		}
		else if(t.kind == Kind.KW_image)
		{
			d2 = ImageDeclaration();
			return d2;
		}
		else if(t.kind == Kind.KW_url 
				|| t.kind == Kind.KW_file)
		{
			d3 = SourceSinkDeclaration();
			return d3;
		}
		else throw new SyntaxException(t, "Invalid character in Declaration");
	}
	
	public Declaration_Variable VariableDeclaration() throws SyntaxException
	{
		Token firstToken = t;
		Token name = null;
		Token type;
		Expression e;
		if(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean)
		{
			type = VarType();			
		} else throw new SyntaxException(t, "Invalid character in VariableDeclaration at VarType");
		if(t.kind == Kind.IDENTIFIER)
		{
			name = t;
			check(Kind.IDENTIFIER);	
		} else throw new SyntaxException(t, "Invalid character in VariableDeclaration at IDENTIFIER");		
		if(t.kind == Kind.OP_ASSIGN)
		{
			e = VariableDeclarationPart();			
		} else e = null;//throw new SyntaxException(t, "Error in VariableDeclarationPart in VariableDeclaration");
		
		return new Declaration_Variable(firstToken, type, name, e); 
	}
	
	public Token VarType() throws SyntaxException
	{
		Token type = t;
		if(t.kind == Kind.KW_int)
			check(Kind.KW_int);
		else if(t.kind == Kind.KW_boolean)
			check(Kind.KW_boolean);
		else throw new SyntaxException(t, "Invalid character in VarType");
		
		return type;
	}
	
	public Expression VariableDeclarationPart() throws SyntaxException
	{
		Expression e;
		if(t.kind == Kind.OP_ASSIGN)
		{
			check(Kind.OP_ASSIGN);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e = expression();
			} else throw new SyntaxException(t, "Error in VariableDeclarationPart");
			return e;
		}
		else //if(t.kind == Kind.SEMI)
		{
			return null;//what to do for epsilon?? should we go to the next token??
		}
		//else throw new SyntaxException(t, "Invalid character in VarDeclarationPart");
	}
	
	public Declaration_Image ImageDeclaration() throws SyntaxException
	{
		Declaration_Image d1;
		Token firstToken = t;
		Expression e1;
		Expression e2;
		Token name = null;
		Source s1;
		
		check(Kind.KW_image);
		if(t.kind == Kind.LSQUARE)
		{
			d1 = ImagePart1();
			e1 = d1.xSize;
			e2 = d1.ySize;
		}
		else 
		{
			e1 = null;
			e2 = null;
		}//throw new SyntaxException(t, "Invalid in ImageDeclaration after KW_image");
		
		if(t.kind == Kind.IDENTIFIER)
		{
			name = t;
			check(Kind.IDENTIFIER);
		} else new SyntaxException(t, "Error in ImageDeclaration at Identifier");
		
		if(t.kind == Kind.OP_LARROW)
		{
			s1 = ImagePart2();
		}
		else s1 = null;//throw new SyntaxException(t, "Invalid in ImageDeclaration after IDENTIFIER");
		
		return new Declaration_Image(firstToken, e1, e2, name, s1);
		
	}	
	
	private Declaration_Image ImagePart1() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Declaration_Image d1;
		Expression e1;
		Expression e2;
		check(Kind.LSQUARE);
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e1 = expression();
		} else throw new SyntaxException(t, "Error in ImagePart1 at Expression 1");
		check(Kind.COMMA);
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e2 = expression();
		} else throw new SyntaxException(t, "Error in Image Part 1 at Expression 2");
		check(Kind.RSQUARE);
		
		return new Declaration_Image(firstToken, e1, e2, t, null); //check
	}
	
	private Source ImagePart2() throws SyntaxException {
		Source s1;
		check(Kind.OP_LARROW);
		if(t.kind == Kind.STRING_LITERAL || t.kind == Kind.OP_AT || t.kind == Kind.IDENTIFIER)
		{
			s1 = Source();
		} else throw new SyntaxException(t, "Error in ImagePart2 at Source");
		return s1;
	}
	
	public Declaration_SourceSink SourceSinkDeclaration() throws SyntaxException
	{
		Token firstToken = t;
		Token type;
		Token name;
		Source s1;
		if(t.kind == Kind.KW_url || t.kind == Kind.KW_file)
		{
			type = SourceSinkType();			
		}
		else throw new SyntaxException(t, "Error in SourceSinkDeclaration at SourceSinkType");
		if(t.kind == Kind.IDENTIFIER)
		{
			name = t;
			check(Kind.IDENTIFIER);			
		} else throw new SyntaxException(t, "Error in SourceSinkDeclaration in IDENTIFIER");
		check(Kind.OP_ASSIGN);
		if(t.kind == Kind.STRING_LITERAL || t.kind == Kind.OP_AT || t.kind == Kind.IDENTIFIER)
		{
			s1 = Source();
		} else throw new SyntaxException(t, "Error in SourceSinkDeclaration at Source");
		
		return new Declaration_SourceSink(firstToken, type, name, s1);
	}

	private Token SourceSinkType() throws SyntaxException {
		// TODO Auto-generated method stub
		Token type = t;
		if(t.kind == Kind.KW_url)
			check(Kind.KW_url);
		else if(t.kind == Kind.KW_file)
			check(Kind.KW_file);
		
		return type;
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		//TODO implement this.
		Expression condition;
		Expression trueExpression;
		Expression falseExpression;
		Expression_Conditional receive;
		Token op;
		
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				){
			op = t;
			condition = OrExpression();
		}
		else throw new SyntaxException(t, "Invalid character in Expression");
			
		
		if(t.kind == Kind.OP_Q)
		{
			receive = ExpressionPart();
			trueExpression = receive.trueExpression;
			falseExpression = receive.falseExpression;
			return new Expression_Conditional(op, condition, trueExpression, falseExpression);
		}
		else ; //****************************check*************************doubt hw3*****************************************************
		
		return condition; //****************************check********firstToken***********doubt hw3***********************************************
		//throw new UnsupportedOperationException();
	}
	
	private Expression_Conditional ExpressionPart() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Expression trueExpr = null;
		Expression falseExpr = null;
		
		if(t.kind == Kind.OP_Q)
		{
			check(Kind.OP_Q); //change after first test case
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					)
			{
				trueExpr = expression();
			} else throw new SyntaxException(t, "Invalid character in ExpressionStart");
			 
			check(Kind.OP_COLON);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				falseExpr = expression();
			} else throw new SyntaxException(t, "Invalid character in ExpressionStart in Expression 2");
			
		}
		else ;
		
		return new Expression_Conditional(firstToken, null, trueExpr, falseExpr); //check
	}

	public Expression OrExpression() throws SyntaxException
	{
		Expression e0;
		Token firstToken;
		Expression e1;
		Token op;
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				){
			firstToken = t;
			e0 = AndExpression();
		}
		else throw new SyntaxException(t, "Invalid character in OrExpression");
		
		//Doubt -- How to handle Kleene star waala part
		while(t.kind == Kind.OP_OR)
		{
			op = t;
			check(Kind.OP_OR);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = AndExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Invalid in OrExpression at second AndExpression"); 
		}
		return e0;
		
	}
	
	public Expression AndExpression() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0;
		Token op;
		Expression e1;
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN		
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				){
			e0 = EqExpression();
		}
		else throw new SyntaxException(t, "Invalid character in EqExpression of AndExpression");
		
		while(t.kind == Kind.OP_AND)
		{
			op = t;
			check(Kind.OP_AND);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN		
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = EqExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Error in EqExpression_2 in AndExpression");
		}
		
		return e0;
		//Doubt -- how to handle kleene star 
	}
	
	public Expression EqExpression() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0;
		Token op;
		Expression e1;
		
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN				
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e0 = RelExpression();
		}
		else throw new SyntaxException(t, "Invalid character in RelExpression");
		
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ)
		{
			op = EqSignPart();
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN				
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = RelExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Error in RelExpression of EqExpression");
		}
		return e0;
		
	}
	
	public Token EqSignPart() throws SyntaxException
	{
		Token op = t;
		if(t.kind == Kind.OP_EQ)
		{
			check(Kind.OP_EQ);
		}
		else if(t.kind == Kind.OP_NEQ)
		{
			check(Kind.OP_NEQ);
		}
		else throw new SyntaxException(t, "Error in EqSignPart");
		
		return op;
	}
	
	public Expression RelExpression() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0;
		Token op;
		Expression e1;
		
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e0 = AddExpression();
		} else throw new SyntaxException(t, "Error in RwlException in AddExpression 1");
		
		while(t.kind == Kind.OP_LT
				|| t.kind == Kind.OP_GT
				|| t.kind == Kind.OP_LE
				|| t.kind == Kind.OP_GE
				)
		{
			op = RelSignPart();
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = AddExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Error in RelExpression in AddExpression 2");
		}
		
		return e0;
	}
	
	public Token RelSignPart() throws SyntaxException
	{
		Token op = t;
		if(t.kind == Kind.OP_LT)
			check(Kind.OP_LT);
		else if(t.kind == Kind.OP_GT)
			check(Kind.OP_GT);
		else if(t.kind == Kind.OP_LE)
			check(Kind.OP_LE);
		else if(t.kind == Kind.OP_GE)
			check(Kind.OP_GE);
		
		return op;
	}
	
	public Expression AddExpression() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0;
		Token op;
		Expression e1;
		
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e0 = MultExpression();
		} else throw new SyntaxException(t, "Error in AddException in MultExpression 1");
		
		while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS)
		{
			op = AddSignPart();
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = MultExpression();				
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Error in AddException in MultExpression 2");
		}
		return e0;
	}
	
	public Token AddSignPart() throws SyntaxException
	{
		Token op = t;
		if(t.kind == Kind.OP_PLUS)
			check(Kind.OP_PLUS);
		else if(t.kind == Kind.OP_MINUS)
			check(Kind.OP_MINUS);
		
		return op;
	}
	
	public Expression MultExpression() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0;
		Token op;
		Expression e1;
		
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			e0 = UnaryExpression();
		} else throw new SyntaxException(t, "Error in MultException in UnaryExpression 1");
		
		while(t.kind == Kind.OP_TIMES 
				|| t.kind == Kind.OP_DIV
				|| t.kind == Kind.OP_MOD)
		{
			op = MulSignPart();
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = UnaryExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			} else throw new SyntaxException(t, "Error in UnaryException in MultExpression 1");
		}
		return e0;
	}

	public Token MulSignPart() throws SyntaxException
	{
		Token op = t;
		if(t.kind == Kind.OP_TIMES)
			check(Kind.OP_TIMES);
		else if(t.kind == Kind.OP_DIV)
			check(Kind.OP_DIV);
		else if(t.kind == Kind.OP_MOD)
			check(Kind.OP_MOD);
		
		return op;
	}
	
	public Expression UnaryExpression() throws SyntaxException
	{
		Token firstToken = t;
		Token op;
		Expression e0;
		if(t.kind == Kind.OP_PLUS)
		{
			op = t;
			check(Kind.OP_PLUS);
			e0 = UnaryExpression();
			return new Expression_Unary(firstToken, op, e0);
		}
		else if(t.kind == Kind.OP_MINUS)
		{
			op = t;
			check(Kind.OP_MINUS);
			e0 = UnaryExpression();
			return new Expression_Unary(firstToken, op, e0);
		}
		else if(t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL)
		{
			e0 = UnaryExpressionNotPlusMinus();
			return e0;
		} else throw new SyntaxException(t, "Error in UnaryExpression in UnaryExpressionNotPlusMinus");
	}
	
	public Expression UnaryExpressionNotPlusMinus() throws SyntaxException
	{
		Token firstToken = t;
		Expression e0; //for Expression_Unary
		Expression e1; //for return from Primary()
		Expression e2; //for IdentorPixelSelectorExpression
		Token op;
		if(t.kind == Kind.OP_EXCL)
		{
			op = t;
			check(Kind.OP_EXCL);
			e0 = UnaryExpression();
			return new Expression_Unary(firstToken, op, e0); //*******************check************firstToken*********hw3**********
		}
		else if(t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.BOOLEAN_LITERAL)
		{
			e1 = Primary();
			return e1;
		}
		else if(t.kind == Kind.IDENTIFIER)
		{
			e2 = IdentOrPixelSelectorExpression();
			return e2;
		}		
		else {
			Kind kind = t.kind;
		if(t.kind == Kind.KW_x)
			check(Kind.KW_x);
		else if(t.kind == Kind.KW_y)
			check(Kind.KW_y);
		else if(t.kind == Kind.KW_r)
			check(Kind.KW_r);
		else if(t.kind == Kind.KW_a)
			check(Kind.KW_a);
		else if(t.kind == Kind.KW_X)
			check(Kind.KW_X);
		else if(t.kind == Kind.KW_Y)
			check(Kind.KW_Y);
		else if(t.kind == Kind.KW_Z)
			check(Kind.KW_Z);
		else if(t.kind == Kind.KW_A)
			check(Kind.KW_A);
		else if(t.kind == Kind.KW_R)
			check(Kind.KW_R);
		else if(t.kind == Kind.KW_DEF_X)
			check(Kind.KW_DEF_X);
		else if(t.kind == Kind.KW_DEF_Y)
			check(Kind.KW_DEF_Y);
		
		return new Expression_PredefinedName(firstToken, kind);
		}
	}
	
	public Expression IdentOrPixelSelectorExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Index receive;
		Expression_Ident e0 = new Expression_Ident(firstToken, t);
		check(Kind.IDENTIFIER);
		if(t.kind == Kind.LSQUARE)
		{
			receive = PixelPart();
			return new Expression_PixelSelector(firstToken, firstToken, receive);
			//Doubt hw3 -- what is name in Expression_PixelSelector?
		}
		else return e0; //********************************************************************************************************
		
	}

	public Index PixelPart() throws SyntaxException {
		// TODO Auto-generated method stub
		Index e0;
		check(Kind.LSQUARE);
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				){
			e0 = Selector();			
		} else throw new SyntaxException(t, "Error in PixelPart in Selector");
		check(Kind.RSQUARE);
		return e0;
	}

	public Expression Primary() throws SyntaxException
	{
		Token firstToken = t;
		Expression_IntLit e0; //For INTEGER_LITERAL
		Expression e1; //for LPAREN Expression RPAREN
		Expression e2; //for FunctionApplication()
		Expression_BooleanLit e3;
		
		if(t.kind == Kind.INTEGER_LITERAL)
		{
			e0 = new Expression_IntLit(firstToken, t.intVal());
			check(Kind.INTEGER_LITERAL);
			return e0;
		}			
		else if(t.kind == Kind.LPAREN)
		{
			check(Kind.LPAREN);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = expression();		//Doubt hw3 -- what to return for this Primary?		
			}else throw new SyntaxException(t, "Error Primary in Expression");
			check(Kind.RPAREN);	
			return e1;
		}
		else if(t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r)
		{
			e2 = FunctionApplication();
			return e2; // Doubt hw3 -- what to return?
		}
		else if(t.kind == Kind.BOOLEAN_LITERAL)
		{
			String text = t.getText();
			if(text.equals("true"))
				e3 = new Expression_BooleanLit(t, true); //********************firstToken***check***hw3**********
			else 
				e3 = new Expression_BooleanLit(t, false);
			check(Kind.BOOLEAN_LITERAL);	
			return e3;
		}			
		else throw new SyntaxException(t, "Error in Primary");
	}
	
	public Expression FunctionApplication() throws SyntaxException
	{
		Token firstToken = t;
		Expression receive;
		Token function = FunctionName();
		if(t.kind == Kind.LPAREN || t.kind == Kind.LSQUARE)
		{
			receive = FunctionTail();
			if(receive instanceof Expression_FunctionAppWithExprArg)
			{
				Expression arg = ((Expression_FunctionAppWithExprArg) receive).arg;
				return new Expression_FunctionAppWithExprArg(firstToken, function.kind, arg);
			}
			else {
				Index arg2 = ((Expression_FunctionAppWithIndexArg) receive).arg;
				return new Expression_FunctionAppWithIndexArg(firstToken, function.kind, arg2);
			}
		}else throw new SyntaxException(t, "Error in FunctionApplication in FunctionTail");
	}
	

	public Token FunctionName() throws SyntaxException
	{
		Token function = t;
		if(t.kind == Kind.KW_sin)
			check(Kind.KW_sin);
		else if(t.kind == Kind.KW_cos)
			check(Kind.KW_cos);
		else if(t.kind == Kind.KW_atan)
			check(Kind.KW_atan);
		else if(t.kind == Kind.KW_abs)
			check(Kind.KW_abs);
		else if(t.kind == Kind.KW_cart_x)
			check(Kind.KW_cart_x);
		else if(t.kind == Kind.KW_cart_y)
			check(Kind.KW_cart_y);
		else if(t.kind == Kind.KW_polar_a)
			check(Kind.KW_polar_a);
		else if(t.kind == Kind.KW_polar_r)
			check(Kind.KW_polar_r);
		
		return function;
	}
	

	private Expression FunctionTail() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		//Token function;
		Expression e0;
		Index e1;
		//Expression_FunctionAppWithExprArg e1;
		if(t.kind == Kind.LPAREN)
		{
			check(Kind.LPAREN);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e0 = expression();				
			} else throw new SyntaxException(t, "Error in FunctionTail in Expression");
			check(Kind.RPAREN);
			
			return new Expression_FunctionAppWithExprArg(firstToken, t.kind, e0); //check
		}
		else //if(t.kind == Kind.LSQUARE)
		{
			check(Kind.LSQUARE);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				e1 = Selector();				
			} else throw new SyntaxException(t, "Error in FunctionPart in Selector");
			check(Kind.RSQUARE);
			
			return new Expression_FunctionAppWithIndexArg(firstToken, t.kind, e1); //check
		}
		
	}
	
	public Index Selector() throws SyntaxException
	{
		Expression eSelector1 = null;
		Token firstToken = t;
		Expression eSelector2 = null;
		if(t.kind == Kind.OP_PLUS
				|| t.kind == Kind.OP_MINUS
				|| t.kind == Kind.OP_EXCL
				|| t.kind == Kind.INTEGER_LITERAL
				|| t.kind == Kind.LPAREN
				|| t.kind == Kind.KW_sin
				|| t.kind == Kind.KW_cos
				|| t.kind == Kind.KW_atan
				|| t.kind == Kind.KW_abs
				|| t.kind == Kind.KW_cart_x
				|| t.kind == Kind.KW_cart_y
				|| t.kind == Kind.KW_polar_a
				|| t.kind == Kind.KW_polar_r
				|| t.kind == Kind.IDENTIFIER
				|| t.kind == Kind.KW_x
				|| t.kind == Kind.KW_y
				|| t.kind == Kind.KW_r
				|| t.kind == Kind.KW_a
				|| t.kind == Kind.KW_X
				|| t.kind == Kind.KW_Y
				|| t.kind == Kind.KW_Z
				|| t.kind == Kind.KW_A
				|| t.kind == Kind.KW_R
				|| t.kind == Kind.KW_DEF_X
				|| t.kind == Kind.KW_DEF_Y
				|| t.kind == Kind.BOOLEAN_LITERAL
				)
		{
			//firstToken = t;
			eSelector1 = expression();
			//expression();
		} else throw new SyntaxException(t, "Error in Selector in Expression 1");
		//op = t;	
		check(Kind.COMMA);
			if(t.kind == Kind.OP_PLUS
					|| t.kind == Kind.OP_MINUS
					|| t.kind == Kind.OP_EXCL
					|| t.kind == Kind.INTEGER_LITERAL
					|| t.kind == Kind.LPAREN
					|| t.kind == Kind.KW_sin
					|| t.kind == Kind.KW_cos
					|| t.kind == Kind.KW_atan
					|| t.kind == Kind.KW_abs
					|| t.kind == Kind.KW_cart_x
					|| t.kind == Kind.KW_cart_y
					|| t.kind == Kind.KW_polar_a
					|| t.kind == Kind.KW_polar_r
					|| t.kind == Kind.IDENTIFIER
					|| t.kind == Kind.KW_x
					|| t.kind == Kind.KW_y
					|| t.kind == Kind.KW_r
					|| t.kind == Kind.KW_a
					|| t.kind == Kind.KW_X
					|| t.kind == Kind.KW_Y
					|| t.kind == Kind.KW_Z
					|| t.kind == Kind.KW_A
					|| t.kind == Kind.KW_R
					|| t.kind == Kind.KW_DEF_X
					|| t.kind == Kind.KW_DEF_Y
					|| t.kind == Kind.BOOLEAN_LITERAL
					){
				eSelector2 = expression();
				//expression();				
			} else throw new SyntaxException(t, "Error in Selector in Expression 2");
		
			Index iSelector = new Index(firstToken, eSelector1, eSelector2); //Doubt hw3- what's the first token here
			return iSelector;
	}

	private void check(Kind k) throws SyntaxException {
		if (t.kind == k) {
			t = scanner.nextToken();
		}
		else
		{
			throw new SyntaxException(t, "unexpected token after : " +t.kind.toString());
		}		
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	public Token matchEOF_test() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
