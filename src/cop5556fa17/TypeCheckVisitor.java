package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

		SymbolTable symbolTableObj = new SymbolTable();
	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(declaration_Variable.name == null)
		{
			symbolTableObj.insert(declaration_Variable.name, declaration_Variable);
			declaration_Variable.newType = TypeUtils.getType(declaration_Variable.type);
			if(declaration_Variable.e != null)
			{
				if(declaration_Variable.newType != declaration_Variable.e.newType)
					throw new SemanticException(declaration_Variable.firstToken,
							"Error in second requirement of visitDeclaration_Variable");
			}
		}
		else throw new SemanticException(declaration_Variable.firstToken,
				"Error in first Requirement of visitDeclaration_Variable");
		
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if((expression_Binary.e0.newType == expression_Binary.e1.newType)
				&& expression_Binary.newType != null)
		{
			if(expression_Binary.op == Kind.OP_EQ || expression_Binary.op == Kind.OP_NEQ)
			{
				expression_Binary.newType = Type.BOOLEAN;
			}
			else if((expression_Binary.op == Kind.OP_GE
					|| expression_Binary.op == Kind.OP_GT
					|| expression_Binary.op == Kind.OP_LT
					|| expression_Binary.op == Kind.OP_LE) && expression_Binary.e0.newType == Type.INTEGER)
			{
				expression_Binary.newType = Type.BOOLEAN;
			}
			else if((expression_Binary.op == Kind.OP_AND
					|| expression_Binary.op == Kind.OP_OR) 
					&& ( expression_Binary.e0.newType == Type.INTEGER || expression_Binary.e0.newType == Type.BOOLEAN))
			{
				expression_Binary.newType = expression_Binary.e0.newType;
			}
			else if((expression_Binary.op == Kind.OP_DIV
					|| expression_Binary.op == Kind.OP_MINUS
					|| expression_Binary.op == Kind.OP_MOD
					|| expression_Binary.op == Kind.OP_PLUS
					|| expression_Binary.op == Kind.OP_POWER
					|| expression_Binary.op == Kind.OP_TIMES)
					&& expression_Binary.e0.newType == Type.INTEGER)
			{
				expression_Binary.newType = Type.INTEGER;
			}
			else expression_Binary.newType = null;
		}
		else throw new SemanticException(expression_Binary.firstToken, 
				"Error in visitExpression_Binary in -- e0 and e1 types not equal or expression.binary is null");
		return expression_Binary;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Type t = expression_Unary.e.newType;
		if(expression_Unary.op == Kind.OP_EXCL && (t == Type.BOOLEAN || t == Type.INTEGER))
		{
			expression_Unary.newType = t;
		}
		else if((expression_Unary.op == Kind.OP_PLUS || expression_Unary.op == Kind.OP_MINUS)
				&& (t == Type.INTEGER))
		{
			expression_Unary.newType = Type.INTEGER;
		}
		else 
		{
			expression_Unary.newType = null;
		}
		
		if(expression_Unary.newType != null)
		{
			;//do nothing since condition is correct
		}
		else throw new SemanticException(expression_Unary.firstToken, 
				"Error in visitExpression_Unary -- Type of Expression_Unary is null");
		
		return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(index.e0.newType == Type.INTEGER && index.e1.newType == Type.INTEGER)
		{
			if(index.e0 instanceof Expression_PredefinedName && index.e1 instanceof Expression_PredefinedName)
			{
				index.setCartesian(!(((Expression_PredefinedName)index.e0).kind == Kind.KW_r 
						&& ((Expression_PredefinedName)index.e1).kind == Kind.KW_a));
			}
			else throw new SemanticException(index.firstToken, 
					"Error in visitIndex -- Expressions in Index not of Expression_PredefinedName");
			
		}
		else throw new SemanticException(index.firstToken, "Either of the Expressions do not have Type as Integer");
		
		return index;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(symbolTableObj.lookupType(expression_PixelSelector.name) == Type.IMAGE)
		{
			expression_PixelSelector.newType = Type.INTEGER;
		}
		else if(expression_PixelSelector.index == null)
		{
			expression_PixelSelector.newType = symbolTableObj.lookupType(expression_PixelSelector.name);
		}
		else
		{
			expression_PixelSelector.newType = null;
		}
		
		if(expression_PixelSelector.newType != null)
		{
			;// correct condition
		}
		else
			throw new SemanticException(expression_PixelSelector.firstToken, 
					"Error in visitExpression_PixelSelector because type of expression_PixelSelector is null");
		
		return expression_PixelSelector;
	}
	

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(expression_Conditional.condition.newType == Type.BOOLEAN
				&& (expression_Conditional.trueExpression.newType == expression_Conditional.falseExpression.newType))
		{
			expression_Conditional.newType = expression_Conditional.trueExpression.newType;
		}
		else throw new SemanticException(expression_Conditional.firstToken, 
				"Error in visitExpression_Conditional -- type of true and false expression not equal or condition expression not boolean");
		
		return expression_Conditional;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(symbolTableObj.lookupType(declaration_Image.name) == null)
		{
			symbolTableObj.insert(declaration_Image.name, declaration_Image);
			declaration_Image.newType = Type.IMAGE;
			if(declaration_Image.xSize != null)
			{
				if(declaration_Image.ySize != null
						&& declaration_Image.xSize.newType == Type.INTEGER
						&& declaration_Image.ySize.newType == Type.INTEGER)
				{
					;//do nothing as it is correct
				}
				else
					throw new SemanticException(declaration_Image.firstToken,
							"Error in visitDeclaration_Image in second Requirement");
			}
		}
		else
			throw new SemanticException(declaration_Image.firstToken,
					"Error in visitDeclaration_Image in first Requirement");
		
		return declaration_Image;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		try
		{
			URL u = new URL(source_StringLiteral.fileOrUrl); // this would check for the protocol
			u.toURI(); // does the extra checking required for validation of URI 
			source_StringLiteral.newType = Type.URL;
		} catch(MalformedURLException malformedURLException)
		{
			source_StringLiteral.newType = Type.FILE;
		}
		
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		source_CommandLineParam.newType = source_CommandLineParam.paramNum.newType;
		if(source_CommandLineParam.newType == Type.INTEGER)
		{
			;//correct condition
		}
		else throw new SemanticException(source_CommandLineParam.firstToken, 
				"Error in visitSource_CommandLineParam -- Type of source_CommandLineParam not Integer");
		
		return source_CommandLineParam;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		source_Ident.newType = symbolTableObj.lookupType(source_Ident.name);
		if(source_Ident.newType == Type.FILE || source_Ident.newType == Type.URL)
		{
			; // correct condition
		}
		else throw new SemanticException(source_Ident.firstToken, 
				"Error in visitSource_Ident	as Type not FILE or URL");
		return source_Ident;
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(symbolTableObj.lookupType(declaration_SourceSink.name) == null)
		{
			symbolTableObj.insert(declaration_SourceSink.name, declaration_SourceSink);
			declaration_SourceSink.newType = TypeUtils.getType(declaration_SourceSink.forTokenType);
			if(declaration_SourceSink.source.newType != declaration_SourceSink.newType)
			{
				throw new SemanticException(declaration_SourceSink.firstToken,
						"Error in visitDeclaration_SourceSink in 2nd requirement");
			}
		}
		else throw new SemanticException(declaration_SourceSink.firstToken,
				"Error in first requirement of visitDeclaration_SourceSink");
		
		return declaration_SourceSink;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_IntLit.newType = Type.INTEGER;
		
		return expression_IntLit;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(expression_FunctionAppWithExprArg.arg.newType == Type.INTEGER)
		{
			expression_FunctionAppWithExprArg.newType = Type.INTEGER;
		}
		else throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, 
				"Error in visitExpression_FunctionAppWithExprArg because Expression type not Integer");
		
		return expression_FunctionAppWithExprArg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithIndexArg.newType = Type.INTEGER;
		
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_PredefinedName.newType = Type.INTEGER;
		
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		statement_Out.setDec(symbolTableObj.lookupDec(statement_Out.name));
		if(symbolTableObj.lookupDec(statement_Out.name) != null)
			; //correct -- so do nothing
		else
			throw new SemanticException(statement_Out.firstToken,
					"Error in visitStatement_Out -- name Declaration is null");
		
		if(((symbolTableObj.lookupType(statement_Out.name) == Type.INTEGER || symbolTableObj.lookupType(statement_Out.name) == Type.BOOLEAN) 
				&& statement_Out.sink.newType == Type.SCREEN) 
			|| symbolTableObj.lookupType(statement_Out.name) == Type.IMAGE && (statement_Out.sink.newType == Type.FILE || statement_Out.sink.newType == Type.SCREEN))
		{
			; //do nothing as it satisfies everything
		}
		else
			throw new SemanticException(statement_Out.firstToken, 
					"Error in visitStatement_Out in second require");
		
		return statement_Out;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		statement_In.setDec(symbolTableObj.lookupDec(statement_In.name));
		if(symbolTableObj.lookupDec(statement_In.name) != null
				&& symbolTableObj.lookupType(statement_In.name) == statement_In.source.newType)
		{
			;//do nothing as condition satisfies
		}
		else throw new SemanticException(statement_In.firstToken,
				"Error in visitStatement_In -- name not declared or name type and source type not equal");
		
		return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(statement_Assign.lhs.newType == statement_Assign.e.newType)
		{
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);
		}
		else throw new SemanticException(statement_Assign.firstToken,
				"LHS type and Expression Type not equal");
		
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		lhs.dec = symbolTableObj.lookupDec(lhs.name);
		lhs.newType = lhs.dec.newType;
		lhs.isCartesian = lhs.index.isCartesian();
		
		return lhs;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		sink_SCREEN.newType = Type.SCREEN;
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		sink_Ident.newType = symbolTableObj.lookupType(sink_Ident.name);
		if(sink_Ident.newType == Type.FILE)
		{
			;//correct condition
		}
		else throw new SemanticException(sink_Ident.firstToken, 
				"Error in visitSink_Ident -- Type of Sink_Ident not FILE");
		
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_BooleanLit.newType = Type.BOOLEAN;
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_Ident.newType = symbolTableObj.lookupType(expression_Ident.name);
		
		return expression_Ident;
	}

}
