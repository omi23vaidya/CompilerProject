package cop5556fa17;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
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
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		FieldVisitor fv;
		fv = cw.visitField(ACC_STATIC, "x", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "y", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "X", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Y", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "r", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "a", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "R", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "A", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "DEF_X", "I", null, new Integer(256));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "DEF_Y", "I", null, new Integer(256));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Z", "I", null, new Integer(16777215));
		fv.visitEnd();

		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");

		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);

		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);

		//terminate construction of main method
		mv.visitEnd();

		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		String fieldName = declaration_Variable.name;
		String fieldType = declaration_Variable.type.kind == Kind.KW_int ? "I" : "Z";
		Object initValue = new Integer(0); //****TODO: What to do for boolean??
		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();

		if(declaration_Variable.e != null)
		{
			declaration_Variable.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);

		switch(expression_Binary.op)
		{
		case OP_EQ:
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l2); //TODO: check this opcode
			break;

		case OP_NEQ:
			Label l3 = new Label();
			Label l4 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l3);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l3);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l4); //TODO: check this opcode
			break;

		case OP_GE:
			Label l5 = new Label();
			Label l6 = new Label();
			mv.visitJumpInsn(IF_ICMPGE, l5);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l6);
			mv.visitLabel(l5);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l6); //TODO: check this opcode
			break;


		case OP_LE:
			Label l7 = new Label();
			Label l8 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l7);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l8);
			mv.visitLabel(l7);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l8); //TODO: check this opcode
			break;

		case OP_LT:
			Label l9 = new Label();
			Label l10 = new Label();
			mv.visitJumpInsn(IF_ICMPLT, l9);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l10);
			mv.visitLabel(l9);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l10); //TODO: check this opcode
			break;

		case OP_GT:
			Label l11 = new Label();
			Label l12 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l11);
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, l12);
			mv.visitLabel(l11);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l12); //TODO: check this opcode
			break;

		case OP_AND:
			mv.visitInsn(IAND);
			break;

		case OP_OR:
			mv.visitInsn(IOR);
			break;

		case OP_PLUS:
			mv.visitInsn(IADD);
			break;

		case OP_MINUS:
			mv.visitInsn(ISUB);
			break;

		case OP_TIMES:
			mv.visitInsn(IMUL);
			break;

		case OP_DIV:
			mv.visitInsn(IDIV);
			break;

		case OP_MOD:
			mv.visitInsn(IREM);
			break;

		default:
			break;
		}

		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		expression_Unary.e.visit(this, null);

		switch(expression_Unary.op)
		{
			case OP_EXCL:
				if(expression_Unary.e.newType == Type.INTEGER)
				{
					//mv.visitInsn(INEG);
					mv.visitLdcInsn(Integer.MAX_VALUE);
					mv.visitInsn(IXOR);
				}
				else if(expression_Unary.e.newType == Type.BOOLEAN)
				{
					Label l1 = new Label();
					Label l2 = new Label();
					mv.visitLdcInsn(new Boolean(true));
					mv.visitJumpInsn(IF_ICMPEQ, l1);
					mv.visitLdcInsn(new Boolean(true));
					mv.visitJumpInsn(GOTO, l2);
					mv.visitLabel(l1);
					mv.visitLdcInsn(new Boolean(false));
					mv.visitLabel(l2);
				}

				break;

			case OP_PLUS:
				//mv.visitInsn(IADD); //TODO: Check this. What to do for plus and minus in unary
				break;

			case OP_MINUS:
				mv.visitLdcInsn(new Integer(-1));
				mv.visitInsn(IMUL);
				break;

			default:
				break;
		}

		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		//TODO: Check this function
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(!(index.isCartesian()))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);
			index.e0.visit(this, arg);
			index.e1.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, "Ljava/awt/image/BufferedImage;");
		expression_PixelSelector.index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getPixel", ImageSupport.getPixelSig, false);
		//ImageSupport.getPixel(image, x, y);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		expression_Conditional.condition.visit(this, arg);
		mv.visitLdcInsn(new Boolean(true));
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitJumpInsn(IF_ICMPEQ, l1);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, l2);
		mv.visitLabel(l1);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitLabel(l2);

		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
		//TODO: To be checked....
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		String fieldName = declaration_Image.name;
		String fieldType = "Ljava/awt/image/BufferedImage;";
		//BufferedImage initValue = new BufferedImage(0,0,0); //TODO: **HW6 - Doubt

		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, null);
		fv.visitEnd();

		if(declaration_Image.source != null)
		{
			declaration_Image.source.visit(this, arg);
			//mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
			if(declaration_Image.xSize == null && declaration_Image.ySize == null)
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);
			}
			else
			{
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);
			}
		}
		else{
			if(declaration_Image.xSize == null && declaration_Image.ySize == null){
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "makeImage", ImageSupport.makeImageSig, false);
			}
			else
			{
				declaration_Image.xSize.visit(this, arg);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "makeImage", ImageSupport.makeImageSig, false);
			}

		}
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		return null;
	}


	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
//		String fieldName = source_StringLiteral.fileOrUrl;
//		String fieldType = "Ljava/lang/String;";
//		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		mv.visitLdcInsn(new String(source_StringLiteral.fileOrUrl));
		return null;
	}



	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		String fieldName = source_Ident.name;
		String fieldType = "Ljava/lang/String;";
		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		return null;
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		String fieldName = declaration_SourceSink.name;
		String fieldType = "Ljava/lang/String;";
		String initValue = new String("");

		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();

		if(declaration_SourceSink.source != null)
		{
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		return null;

	}



	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		mv.visitLdcInsn(new Integer(expression_IntLit.value));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function == Kind.KW_abs)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "abs", RuntimeFunctions.absSig, false);
		}
		else if(expression_FunctionAppWithExprArg.function == Kind.KW_log)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "log", RuntimeFunctions.logSig, false);
		}

		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_x)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_y)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_a)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_r)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);
		}

		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
//		String fieldName = expression_PredefinedName.name;
//		String fieldType = declaration_Variable.type.kind == Kind.KW_int ? "I" : "Z";
//		Object initValue = new Integer(0); //****TODO: What to do for boolean??
//		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
//		fv.visitEnd();

		switch(expression_PredefinedName.kind)
		{
			case KW_x:
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				break;

			case KW_y:
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				break;

			case KW_X:
				mv.visitFieldInsn(GETSTATIC, className, "X", "I");
				break;

			case KW_Y:
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				break;

			case KW_r:
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);
				break;

			case KW_a:
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);
				break;

			case KW_R:
				mv.visitFieldInsn(GETSTATIC, className, "X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);
				break;

			case KW_A:
				mv.visitLdcInsn(new Integer(0));
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);
				break;

			case KW_DEF_X:
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				break;

			case KW_DEF_Y:
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
				break;

			case KW_Z:
				mv.visitFieldInsn(GETSTATIC, className, "Z", "I");
				break;

			default:
				break;
		}

		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases
		//throw new UnsupportedOperationException();
		String fieldName = statement_Out.name;
		if(statement_Out.getDec().newType == Type.INTEGER || statement_Out.getDec().newType == Type.BOOLEAN)
		{
			String fieldType = statement_Out.getDec().newType == Type.INTEGER ? "I" : "Z";
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().newType);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "("+fieldType+")V", false);
		}
		else if(statement_Out.getDec().newType == Type.IMAGE)
		{
			String fieldType = "Ljava/awt/image/BufferedImage;";
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().newType);
			statement_Out.sink.visit(this, arg);
		}


		return null;
		//TODO: Check invokevirtual
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 *
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean
	 *  to convert String to actual type.
	 *
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		//throw new UnsupportedOperationException();
		if(statement_In.source.newType == Type.INTEGER || statement_In.source.newType == null)
		{
			String fieldName = statement_In.name;
			statement_In.source.visit(this, arg);
			if(statement_In.getDec().newType == Type.INTEGER)
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				mv.visitFieldInsn(PUTSTATIC, className, fieldName, "I");
			}
			else if(statement_In.getDec().newType == Type.BOOLEAN)
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				mv.visitFieldInsn(PUTSTATIC, className, fieldName, "Z");
			}
			else if(statement_In.getDec().newType == Type.IMAGE)
			{

				if(((Declaration_Image)statement_In.getDec()).xSize == null && ((Declaration_Image)statement_In.getDec()).ySize == null)
				{
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);
				}
				else
				{
					((Declaration_Image)statement_In.getDec()).xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					((Declaration_Image)statement_In.getDec()).ySize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);
				}
				mv.visitFieldInsn(PUTSTATIC, className, fieldName, "Ljava/awt/image/BufferedImage;");
			}
		}
			return null;
		}


	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		//throw new UnsupportedOperationException();

		if(statement_Assign.lhs.newType == Type.INTEGER || statement_Assign.lhs.newType == Type.BOOLEAN)
		{
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}
		else if(statement_Assign.lhs.newType == Type.IMAGE)
		{

			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");
			//load X using getX, Y using getY, set x = 0
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getX", ImageSupport.getXSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");
			mv.visitLdcInsn(new Integer(0));
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");

			Label l1 = new Label();
			Label l2 = new Label();
			Label l3 = new Label();
			Label l4 = new Label();
			Label l5 = new Label();

			mv.visitJumpInsn(GOTO, l5);
			mv.visitLabel(l3);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitLdcInsn(new Integer(1));
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitLabel(l5);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitJumpInsn(IF_ICMPGE, l1);

			mv.visitLdcInsn(new Integer(0));
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitLabel(l4);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitJumpInsn(IF_ICMPGE, l3);

//			if (!statement_Assign.isCartesian()) {
//				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
//				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
//				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", "(II)I", false);
//				mv.visitFieldInsn(PUTSTATIC, className, "r", "I");
//				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
//				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
//				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", "(II)I", false);
//				mv.visitFieldInsn(PUTSTATIC, className, "a", "I");
//			}

			statement_Assign.e.visit(this, arg);
			//mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");

			statement_Assign.lhs.visit(this, arg);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitLdcInsn(new Integer(1));
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitJumpInsn(GOTO, l4);

			mv.visitLabel(l1);

		}
		//TODO: **HW6 - Doubt how to handle polar coordinates
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		//throw new UnsupportedOperationException();
		String fieldName = lhs.name;
//		if(lhs.newType == Type.INTEGER || lhs.newType == Type.BOOLEAN)
//		{
//			mv.visitFieldInsn(PUTSTATIC, className, fieldName, "Ljava/lang/String;");
//		}
		if(lhs.dec.newType == Type.INTEGER || lhs.dec.newType == Type.BOOLEAN)
		{
			String desc = lhs.dec.newType == Type.INTEGER ? "I" : "Z";
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, desc);
		}
		else if(lhs.dec.newType == Type.IMAGE)
		{
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, "Ljava/awt/image/BufferedImage;");
			mv.visitFieldInsn(GETSTATIC, className, "x", "I"); //to load x
			mv.visitFieldInsn(GETSTATIC, className, "y", "I"); //to load y
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "setPixel", ImageSupport.setPixelSig, false);
			//ImageSupport.setPixel(rgb, image, x, y); <-- to be used this in the line above
		}

		return null;
	}


	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageFrame", "makeFrame", "(Ljava/awt/image/BufferedImage;)Ljavax/swing/JFrame;", false);
		//ImageFrame.makeFrame(image) <--- use this method above
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		//throw new UnsupportedOperationException();
		String fieldName = sink_Ident.name;
		String fieldType = "Ljava/lang/String;";
		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "write", ImageSupport.writeSig, false);
		//ImageSupport.write(image, filename); <-- use this method above
		return null;

	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO
		//throw new UnsupportedOperationException();
		mv.visitLdcInsn(new Boolean(expression_BooleanLit.value));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		//throw new UnsupportedOperationException();
		String fieldName = expression_Ident.name;
		String fieldType = expression_Ident.newType == Type.INTEGER ? "I" : "Z";
		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;
	}

}
