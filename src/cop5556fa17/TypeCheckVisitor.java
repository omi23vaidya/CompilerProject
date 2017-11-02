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
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
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
		Expression exp = declaration_Variable.e;
		if(exp!=null)
			exp.visit(this, arg);
		
		if(symbolTableObj.lookupType(declaration_Variable.name) == null)
		{
			symbolTableObj.insert(declaration_Variable.name, declaration_Variable);
			declaration_Variable.newType = TypeUtils.getType(declaration_Variable.type);
			if(exp != null)
			{
				if(declaration_Variable.newType != exp.newType)
					throw new SemanticException(declaration_Variable.firstToken,
							"Error in second requirement of visitDeclaration_Variable");
			}
		}
		else throw new SemanticException(declaration_Variable.firstToken,
				"Error in first Requirement of visitDeclaration_Variable");
		
		return arg;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression exp0 = expression_Binary.e0;
		Expression exp1 = expression_Binary.e1;
		if(exp0!=null)
			exp0.visit(this, arg);
		
		if(exp1!=null)
			exp1.visit(this, arg);
		
		
			if(expression_Binary.op == Kind.OP_EQ || expression_Binary.op == Kind.OP_NEQ)
			{
				expression_Binary.newType = Type.BOOLEAN;
			}
			else if((expression_Binary.op == Kind.OP_GE
					|| expression_Binary.op == Kind.OP_GT
					|| expression_Binary.op == Kind.OP_LT
					|| expression_Binary.op == Kind.OP_LE) && exp0.newType == Type.INTEGER)
			{
				expression_Binary.newType = Type.BOOLEAN;
			}
			else if((expression_Binary.op == Kind.OP_AND
					|| expression_Binary.op == Kind.OP_OR) 
					&& (exp0.newType == Type.INTEGER || exp0.newType == Type.BOOLEAN))
			{
				expression_Binary.newType = exp0.newType;
			}
			else if((expression_Binary.op == Kind.OP_DIV
					|| expression_Binary.op == Kind.OP_MINUS
					|| expression_Binary.op == Kind.OP_MOD
					|| expression_Binary.op == Kind.OP_PLUS
					|| expression_Binary.op == Kind.OP_POWER
					|| expression_Binary.op == Kind.OP_TIMES)
					&& exp0.newType == Type.INTEGER)
			{
				expression_Binary.newType = Type.INTEGER;
			}
			else expression_Binary.newType = null;
		if((exp0.newType == exp1.newType)
					&& expression_Binary.newType != null)
		{
			;//do nothing
		}
		else throw new SemanticException(expression_Binary.firstToken, 
				"Error in visitExpression_Binary in -- e0 and e1 types not equal or expression.binary is null");
		return arg;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression exp = expression_Unary.e;
		if(exp!=null)
			exp.visit(this, arg);
		
		Type t = exp.newType;
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
		
		return arg;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression exp0 = index.e0;
		Expression exp1 = index.e1;
		
		if(exp0!=null)
			exp0.visit(this, arg);
		if(exp1!=null)
			exp1.visit(this, arg);
		
		if(exp0.newType == Type.INTEGER && exp1.newType == Type.INTEGER)
		{
			if(exp0 instanceof Expression_PredefinedName && exp1 instanceof Expression_PredefinedName)
			{
				index.setCartesian(!(((Expression_PredefinedName)exp0).kind == Kind.KW_r 
						&& ((Expression_PredefinedName)exp1).kind == Kind.KW_a));
			}
			else index.setCartesian(true);
			
		}
		else throw new SemanticException(index.firstToken, "Either of the Expressions do not have Type as Integer");
		
		return arg;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Index i = expression_PixelSelector.index;
		if(i != null)
		{
			i.visit(this, arg);			
		}
		if(symbolTableObj.symbolTable.containsKey(expression_PixelSelector.name))
		{
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
		}
		else throw new SemanticException(expression_PixelSelector.firstToken, "Error in visitExpression_PixelSelector due to null on lookup");
		
		if(expression_PixelSelector.newType != null)
		{
			;// correct condition
		}
		else
			throw new SemanticException(expression_PixelSelector.firstToken, 
					"Error in visitExpression_PixelSelector because type of expression_PixelSelector is null");
		
		return arg;
	}
	

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression cond = expression_Conditional.condition;
		Expression trueExp = expression_Conditional.trueExpression;
		Expression falseExp = expression_Conditional.falseExpression;
		
		if(cond != null)
		{
			cond.visit(this, arg);			
		}
		if(trueExp != null)
		{
			trueExp.visit(this, arg);			
		}
		if(falseExp != null)
		{
			falseExp.visit(this, arg);			
		}
		
		if(cond.newType == Type.BOOLEAN
				&& (trueExp.newType == falseExp.newType))
		{
			expression_Conditional.newType = trueExp.newType;
		}
		else throw new SemanticException(expression_Conditional.firstToken, 
				"Error in visitExpression_Conditional -- type of true and false expression not equal or condition expression not boolean");
		
		return arg;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression xs = declaration_Image.xSize;
		Expression ys = declaration_Image.ySize;
		Source sr = declaration_Image.source;
		
		if(xs != null)
		{
			xs.visit(this, arg);			
		}
		if(ys != null)
		{
			ys.visit(this, arg);
		}
		if(sr != null)
		{
			sr.visit(this, arg);
		}
		
		if(symbolTableObj.lookupType(declaration_Image.name) == null)
		{
			symbolTableObj.insert(declaration_Image.name, declaration_Image);
			declaration_Image.newType = Type.IMAGE;
			if(xs != null)
			{
				if(ys != null
						&& xs.newType == Type.INTEGER
						&& ys.newType == Type.INTEGER)
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
		
		return arg;
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
		
		return arg;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression pn = source_CommandLineParam.paramNum;
		if(pn != null)
			pn.visit(this, arg);
		
		source_CommandLineParam.newType = pn.newType;
		if(source_CommandLineParam.newType == Type.INTEGER)
		{
			;//correct condition
		}
		else throw new SemanticException(source_CommandLineParam.firstToken, 
				"Error in visitSource_CommandLineParam -- Type of source_CommandLineParam not Integer");
		
		return arg;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(symbolTableObj.symbolTable.containsKey(source_Ident.name))
		{
			source_Ident.newType = symbolTableObj.lookupType(source_Ident.name);
		}
		else throw new SemanticException(source_Ident.firstToken, "Error in visitSource_Ident due to null on lookup");
		
		if(source_Ident.newType == Type.FILE || source_Ident.newType == Type.URL)
		{
			; // correct condition
		}
		else throw new SemanticException(source_Ident.firstToken, 
				"Error in visitSource_Ident	as Type not FILE or URL");
		return arg;
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Source sr = declaration_SourceSink.source;
		if(sr != null)
			sr.visit(this, arg);
		
		if(symbolTableObj.lookupType(declaration_SourceSink.name) == null)
		{
			symbolTableObj.insert(declaration_SourceSink.name, declaration_SourceSink);
			declaration_SourceSink.newType = TypeUtils.getType(declaration_SourceSink.forTokenType);
			if(sr.newType != declaration_SourceSink.newType)
			{
				throw new SemanticException(declaration_SourceSink.firstToken,
						"Error in visitDeclaration_SourceSink in 2nd requirement");
			}
		}
		else throw new SemanticException(declaration_SourceSink.firstToken,
				"Error in first requirement of visitDeclaration_SourceSink");
		
		return arg;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_IntLit.newType = Type.INTEGER;
		
		return arg;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Expression exp = expression_FunctionAppWithExprArg.arg;
		if(exp != null)
			exp.visit(this, arg);
		
		if(exp.newType == Type.INTEGER)
		{
			expression_FunctionAppWithExprArg.newType = Type.INTEGER;
		}
		else throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, 
				"Error in visitExpression_FunctionAppWithExprArg because Expression type not Integer");
		
		return arg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Index i = expression_FunctionAppWithIndexArg.arg;
		if(i != null)
			i.visit(this, arg);
		expression_FunctionAppWithIndexArg.newType = Type.INTEGER;
		
		return arg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_PredefinedName.newType = Type.INTEGER;
		
		return arg;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Sink s = statement_Out.sink;
		if(s!=null)
			s.visit(this, arg);
		if(symbolTableObj.lookupDec(statement_Out.name)!=null)
		{
			statement_Out.setDec(symbolTableObj.lookupDec(statement_Out.name));
		}
		else throw new SemanticException(statement_Out.firstToken, "Error in visitStatement_Out due to null on lookup of name");
		if(symbolTableObj.lookupDec(statement_Out.name) != null)
			; //correct -- so do nothing
		else
			throw new SemanticException(statement_Out.firstToken,
					"Error in visitStatement_Out -- name Declaration is null");
		if(symbolTableObj.symbolTable.containsKey(statement_Out.name))
		{
		if(((symbolTableObj.lookupType(statement_Out.name) == Type.INTEGER || symbolTableObj.lookupType(statement_Out.name) == Type.BOOLEAN) 
				&& s.newType == Type.SCREEN) 
			|| symbolTableObj.lookupType(statement_Out.name) == Type.IMAGE && (s.newType == Type.FILE || s.newType == Type.SCREEN))
		{
			; //do nothing as it satisfies everything
		}
		else
			throw new SemanticException(statement_Out.firstToken, 
					"Error in visitStatement_Out in second require");
		}
		else throw new SemanticException(statement_Out.firstToken, "Error in visitStatement_Out due to null on lookup of name");
		return arg;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Source sr = statement_In.source;
		if(sr!=null)
			sr.visit(this, arg);
		
		if(symbolTableObj.lookupDec(statement_In.name)!=null)
		{	
			statement_In.setDec(symbolTableObj.lookupDec(statement_In.name));
			if(symbolTableObj.lookupDec(statement_In.name) != null
				&& symbolTableObj.lookupType(statement_In.name) == sr.newType)
			{
				;//do nothing as condition satisfies
			}
			else throw new SemanticException(statement_In.firstToken,
				"Error in visitStatement_In -- name not declared or name type and source type not equal");
		}
		else throw new SemanticException(statement_In.firstToken, "Error in statement_In due to null while doing lookup");
		
		return arg;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		LHS l1 = statement_Assign.lhs;
		Expression exp = statement_Assign.e;
		
		if(l1!=null)
			l1.visit(this, arg);
		if(exp!=null)
			exp.visit(this, arg);
		
		if(l1.newType == exp.newType)
		{
			statement_Assign.setCartesian(l1.isCartesian);
		}
		else throw new SemanticException(statement_Assign.firstToken,
				"LHS type and Expression Type not equal");
		
		return arg;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Index i = lhs.index;
		if(i!=null)
			i.visit(this, arg);
		
		if(symbolTableObj.lookupDec(lhs.name)!=null)
		{
			lhs.dec = symbolTableObj.lookupDec(lhs.name);
			lhs.newType = lhs.dec.newType;
			if(i!=null)
				lhs.isCartesian = i.isCartesian();
		}
		else throw new SemanticException(lhs.firstToken, "Error in visitLHS because lhs.name lookup returned a null from symbolTable");
		
		
		return arg;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		sink_SCREEN.newType = Type.SCREEN;
		return arg;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		if(symbolTableObj.symbolTable.containsKey(sink_Ident.name))
		{
		sink_Ident.newType = symbolTableObj.lookupType(sink_Ident.name);
		if(sink_Ident.newType == Type.FILE)
		{
			;//correct condition
		}
		else throw new SemanticException(sink_Ident.firstToken, 
				"Error in visitSink_Ident -- Type of Sink_Ident not FILE");
		}
		else throw new SemanticException(sink_Ident.firstToken, "Error in visitSink_Ident because sink_Ident.name gave null on lookup");
		
		return arg;
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
		if(symbolTableObj.symbolTable.containsKey(expression_Ident.name))
		{
			expression_Ident.newType = symbolTableObj.lookupType(expression_Ident.name);
		}
		else throw new SemanticException(expression_Ident.firstToken, "Error in visitExpression_Ident due to null while lookup of name");
		
		return expression_Ident;
	}

}
