package cop5556fa17;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.CodeGenUtils.DynamicClassLoader;
import cop5556fa17.AST.Program;

public class CodeGenVisitorTest implements ImageResources{

	static boolean doPrint = true;
	static boolean doCreateFile = false;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private boolean devel = false;
	private boolean grade = true;


	public static final int Z = 0xFFFFFF;



	/**
	 * Generates bytecode for given input.
	 * Throws exceptions for Lexical, Syntax, and Type checking errors
	 *
	 * @param input   String containing source code
	 * @return        Generated bytecode
	 * @throws Exception
	 */
	byte[] genCode(String input) throws Exception {

		//scan, parse, and type check
		Scanner scanner = new Scanner(input);
		show(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		//output the generated bytecode
		show(CodeGenUtils.bytecodeToString(bytecode));

		//write byte code to file
		if (doCreateFile) {
			String name = ((Program) program).name;
			String classFileName = "bin/" + name + ".class";
			OutputStream output = new FileOutputStream(classFileName);
			output.write(bytecode);
			output.close();
			System.out.println("wrote classfile to " + classFileName);
		}

		//return generated classfile as byte array
		return bytecode;
	}

	/**
	 * Run main method in given class
	 *
	 * @param className
	 * @param bytecode
	 * @param commandLineArgs  String array containing command line arguments, empty array if none
	 * @throws Exception
	 */
	void runCode(String className, byte[] bytecode, String[] commandLineArgs) throws Exception {
		RuntimeLog.initLog(); //initialize log used for grading.
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(className, bytecode);
		Class[] argTypes = {commandLineArgs.getClass()};
		Method m = testClass.getMethod("main", argTypes );
		show("Output from " + m + ":");  //print name of method to be executed
		Object passedArgs[] = {commandLineArgs};  //create array containing params, in this case a single array.
		m.invoke(null, passedArgs);
	}


	/** Delays for 5 seconds.
	 * May be useful during development to delay closing frames displaying images
     */
	void sleepFor5() throws Exception {
		Thread.sleep(5000);
	}

	/** Blocks program until a key is pressed to the console.
	 * May be useful during development to delay closing frames displaying images
	 */
	void waitForKey() throws IOException {
		System.out.println("enter any char to exit");
		int b = System.in.read();
	}

	/**
	 * Used in most test cases.  Change once here to change behavior in all tests.
	 *
	 * @throws Exception
	 */
	void keepFrame() throws Exception {
		sleepFor5();
	}

//
//	@Test
//	/**
//	 * Empty program.  Test updated for new logging instructions.
//	 * @throws Exception
//	 */
//	public void emptyProg() throws Exception {
//		String prog = "emptyProg";
//		String input = prog;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//		assertEquals("",RuntimeLog.globalLog.toString());
//	}
//
//
//	@Test
//	/** The program in our language creates and displays a 512 x 512 image
//	 * with all red pixels.  Then it compares that to an image created
//	 * by ImageSupport.makeConstantImage.
//	 *
//	 * @throws Exception
//	 */
//	public void imageGenRed() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageGenRed";
//		String input = prog
//				+ "\nimage[512,512] g; \n"
//				+ "g[[x,y]] = 16711680;"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//		BufferedImage imageRef = ImageSupport.makeConstantImage(0xFF0000, 512, 512);
//		BufferedImage image = RuntimeLog.globalImageLog.get(0);
//		ImageSupport.compareImages(imageRef, image);
//		keepFrame();
//	}
//
//	@Test
//	/**
//	 * Creates a default-sized green image.
//	 * @throws Exception
//	 */
//	public void imageGenGreen() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageGenGreen";
//		String input = prog
//				+ "\nimage g; \n"
//				+ "g[[x,y]] = 65280;"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//		BufferedImage imageRef = ImageSupport.makeConstantImage(0xFF0000, 256, 256);
//		BufferedImage image = RuntimeLog.globalImageLog.get(0);
//		ImageSupport.compareImages(imageRef, image);
//		keepFrame();
//	}
//
//	@Test
//	/** This is the same test case as before, but the assert statement has been updated to reflect the new instructions
//	 * for where to put log statements in assignment 6.
//	 *
//	 * @throws Exception
//	 */
//	public void prog1() throws Exception {
//		String prog = "prog1";
//		String input = prog + "\nint g;\ng = 3;\ng -> SCREEN; ";
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case
//		runCode(prog, bytecode, commandLineArgs);
//		assertEquals("3;",RuntimeLog.globalLog.toString());
//	}
//
//	@Test
//	public void prog2() throws Exception {
//		String prog = "prog2";
//		String input = prog  + "\nboolean g;\ng = true;\ng -> SCREEN;\ng = false;\ng -> SCREEN;";
//		show(input);
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case
//		runCode(prog, bytecode, commandLineArgs);
//		assertEquals("true;false;",RuntimeLog.globalLog.toString() );
//	}
//
//	@Test
//	public void prog3() throws Exception {
//		//scan, parse, and type check the program
//		String prog = "prog3";
//		String input = prog
//				+ " boolean g;\n"
//				+ "g <- @ 0;\n"
//				+ "g -> SCREEN;\n"
//				+ "int h;\n"
//				+ "h <- @ 1;\n"
//				+ "h -> SCREEN;";
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {"true", "55"}; //create command line argument array to initialize params, none in this case
//		runCode(prog, bytecode, commandLineArgs);
//		assertEquals("true;55;",RuntimeLog.globalLog.toString());
//	}
//
//	@Test
//	public void prog4() throws Exception {
//		//scan, parse, and type check the program
//		String prog = "prog4";
//		String input = prog
//				+ " boolean g;\n"
//				+ "g <- @ 0;\n"
//				+ "g -> SCREEN;\n"
//				+ "int h;\n"
//				+ "h <- @ 1;\n"
//				+ "h -> SCREEN;\n"
//				+ "int k;\n"
//				+ "k <- @ 2;\n"
//				+ "k -> SCREEN;\n"
//				+ "int chosen;"
//				+ "chosen = g ? h : k;\n"
//				+ "chosen -> SCREEN;"
//				;
//		show(input);
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {"true", "34", "56"}; //create command line argument array to initialize params, none in this case
//		runCode(prog, bytecode, commandLineArgs);
//		assertEquals("true;34;56;34;",RuntimeLog.globalLog.toString());
//	}
//
//
//	@Test
//	//  reads an image from the filename passed as command line argument and displays it.
//	//  Compares the image output (and logged) from our language and compares with the same image read directly from the file.
//	public void image1() throws Exception{
//		String prog = "image1";
//		String input = prog
//				+ "\nimage g; \n"
//				+ "g <- @ 0;\n"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {imageFile1};
//		runCode(prog, bytecode, commandLineArgs);
//		BufferedImage refImage0 = ImageSupport.readFromFile(imageFile1);
//		BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
//		assertTrue(ImageSupport.compareImages(refImage0, loggedImage0 ));
//		keepFrame();
//	}
//
//
//
//	@Test
//	/** reads and resizes image with filename taken from command line
//	 *
//	 * @throws Exception
//	 */
//	public void image2() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "image2";
//		String input = prog
//				+ "\nimage[128,128] g; \n"
//				+ "g <- @ 0;\n"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {imageFile1};
//		runCode(prog, bytecode, commandLineArgs);
//
//		BufferedImage refImage0 = ImageSupport.readImage(imageFile1, 128, 128);
//		BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
//		assertTrue(ImageSupport.compareImages(refImage0,loggedImage0));
//		keepFrame();
//	}
//
//
//
//	@Test
//	public void imageGen3() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageGen3";
//		String input = prog
//				+ "\nimage[1024,512] g; \n"
//				+ "g[[x,y]] = x*y;"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//
//		BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//		for(int y = 0; y < 512; y++) {
//			for (int x = 0; x < 1024; x++) {
//				int pixelRef = x*y;
//				int pixel = ImageSupport.getPixel(loggedImage, x,y);
//				assertEquals(pixelRef, pixel);
//			}
//		}
//		keepFrame();
//	}
//
//	@Test
//	public void imageGen4() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageGen4";
//		String input = prog
//				+ "\nimage[1024,1024] g; \n"
//				+ "g[[r,a]] = r;"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//
//		BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//		for(int y = 0; y < 1024; y++) {
//			for (int x = 0; x < 1024; x++) {
//				int pixelRef = RuntimeFunctions.polar_r(x, y);
//				int pixel = ImageSupport.getPixel(loggedImage, x,y);
//				assertEquals(pixelRef, pixel);
//			}
//		}
//		keepFrame();
//
//	}
//
//
//
//	@Test
//	public void imageCopy() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageCopy";
//		String input = prog
//				+ "\nimage[1024,1024] g; \n"
//				+ "\nimage[1024,1024] h; \n"
//				+ "g <- @ 0;\n"
//				+ "g -> SCREEN;\n"
//				+ "h[[x,y]] =  g[x,y];\n"
//				+ "h -> SCREEN; \n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {imageFile1};
//		runCode(prog, bytecode, commandLineArgs);
//
//		BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
//		BufferedImage loggedImage1 = RuntimeLog.globalImageLog.get(1);
//		assertTrue(ImageSupport.compareImages(loggedImage0,loggedImage1));
//
//		keepFrame();
//	}
//
//
//
//	@Test
//	/**
//	 * Create a grid with white lines and black background.
//	 * @throws Exception
//	 */
//	public void imageGen7() throws Exception{
//		devel = false;
//		grade = true;
//		String prog = "imageGen7";
//		String input = prog
//				+ "\nimage[512,512] g; \n"
//				+ "g[[x,y]] = (x%20>1)?(y%20>1)? 0 : Z : Z;"
//				+ "g -> SCREEN;\n"
//				;
//		byte[] bytecode = genCode(input);
//		String[] commandLineArgs = {};
//		runCode(prog, bytecode, commandLineArgs);
//
//		BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//		for(int y = 0; y < 512; y++) {
//			for (int x = 0; x < 512; x++) {
//				int pixelRef = (x%20>1)?(y%20>1)? 0 : Z : Z;
//				int pixel = ImageSupport.getPixel(loggedImage, x,y);
//				assertEquals(pixelRef, pixel);
//			}
//		}
//		keepFrame();
//	}
//
//
//@Test
//public void checkConstants() throws Exception{
//	String prog = "checkConstants";
//	String input = prog + " \n"
//			+"int z = Z; z -> SCREEN;\n"
//			+"int def_X = DEF_X; def_X -> SCREEN;\n"
//			+"int def_Y = DEF_Y; def_Y -> SCREEN;\n"
//			;
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//	System.out.println("Z=" + 0xFFFFFF);
//	assertEquals(Z + ";256;256;", RuntimeLog.getGlobalString());
//}
//
//@Test
//public void prof1() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g <- @ 0;"
//			+ "g -> SCREEN;\n"
//			+"\nimage[1024,1024] h;\nh[[x,y]] = !g[x,y];h -> SCREEN; \n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {imageFile1};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
//	BufferedImage img = ImageSupport.readImage(imageFile1, 1024, 1024);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int pixelRef = Z-ImageSupport.getPixel(img, x, y);
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
////below gives error
//@Test
//public void prof2() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g <- @ 0;"
//			+ "g -> SCREEN;\n"
//			+"\nimage[1024,1024] h;\nh[[x,y]] = g[x,y];h -> SCREEN; \n"
//			+"\nimage[1024,1024] average; \naverage[[x,y]] = h[x,y]*3;average -> SCREEN; \n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {imageFile1};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(2);
//	BufferedImage img = ImageSupport.readImage(imageFile1, 1024, 1024);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int pixelRef = (Z-ImageSupport.getPixel(img, x, y))*3;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//@Test
//public void prof3() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g <- @ 0;"
//			+ "g -> SCREEN;\n"
//			+"\nimage[1024,1024] h;\nh[[x,y]] = Z-g[x,y];h -> SCREEN; \n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {imageFile1};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
//	BufferedImage img = ImageSupport.readImage(imageFile1, 1024, 1024);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int pixelRef = Z-ImageSupport.getPixel(img, x, y);
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//@Test
//public void prof4() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g <- @ 0;"
//			+ "g -> SCREEN;\n"
//			+"\nimage[1024,1024] h;"
//			+ "\nh[[x,y]] = (g[x,y] > Z/2) ? Z-g[x,y] : g[x,y];"
//			+ "h -> SCREEN; \n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {imageFile1};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
//	BufferedImage img = ImageSupport.readImage(imageFile1, 1024, 1024);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int g =  ImageSupport.getPixel(img, x, y);
//			int pixelRef = (g > Z/2) ? Z-g : g;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//@Test
//public void prof5() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[512,512] g; \n"
//			+ "g[[x, y]] = 150;"
//			+ "g -> SCREEN;\n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 512; y++) {
//		for (int x = 0; x < 512; x++) {
//			int pixelRef = 150;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//
//@Test
//public void prof6() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[512,512] g; \n"
//			+ "g[[x, y]] = (x%7>1)?(y%7>1)? 0 : Z : Z;"
//			+ "g -> SCREEN;\n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 512; y++) {
//		for (int x = 0; x < 512; x++) {
//			int pixelRef = (x%7>1)?(y%7>1)? 0 : Z : Z;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//@Test
//public void prof7() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[512,512] g; \n"
//			+ "g[[x, y]] = 	(x%20>1)?(y%20>1)? 0 : Z : Z;"
//			+ "g -> SCREEN;\n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 512; y++) {
//		for (int x = 0; x < 512; x++) {
//			int pixelRef = (x%20>1)?(y%20>1)? 0 : Z : Z;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//
//@Test
//public void prof8() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[512,512] g; \n"
//			+ "g[[x, y]] = 	(x%20>1)?(y%20>1)? 16711680 : x/2+ 65280 : x/2+ 65280;"
//			+ "g -> SCREEN;\n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 512; y++) {
//		for (int x = 0; x < 512; x++) {
//			int pixelRef = (x%20>1)?(y%20>1)? 16711680 : x/2+ 65280 : x/2+ 65280;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//
//@Test
//public void prof9() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen4";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g[[r,a]] = r*100;"
//			+ "g -> SCREEN;\n"
//			;
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int pixelRef = RuntimeFunctions.polar_r(x, y)*100;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//			assertEquals(pixelRef, pixel);
//			//System.out.println(pixelRef+" "+pixel);
//		}
//	}
//	keepFrame();
//
//}
//
//@Test
//public void prof10() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen4";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g[[r,a]] = cart_x[r,a];"
//			+ "g -> SCREEN;\n"
//			;
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(0);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int r = RuntimeFunctions.polar_r(x, y);
//			int a = RuntimeFunctions.polar_a(x, y);
//			int pixelRef = RuntimeFunctions.cart_x(r, a);
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//			assertEquals(pixelRef, pixel);
//			//System.out.println(pixelRef+" "+pixel);
//		}
//	}
//	keepFrame();
//
//}
//
//@Test
//public void prof11() throws Exception{
//	devel = false;
//	grade = true;
//	String prog = "imageGen3";
//	String input = prog
//			+ "\nimage[1024,1024] g; \n"
//			+ "g <- @ 0;"
//			+ "g -> SCREEN;\n"
//			+"\nimage[1024,1024] h;"
//			+ "\nh[[x,y]] = g[x,Y-y];"
//			+ "h -> SCREEN; \n"
//			;
//
//	byte[] bytecode = genCode(input);
//	String[] commandLineArgs = {imageFile1};
//	runCode(prog, bytecode, commandLineArgs);
//
//	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
//	BufferedImage img = ImageSupport.readImage(imageFile1, 1024, 1024);
//	for(int y = 0; y < 1024; y++) {
//		for (int x = 0; x < 1024; x++) {
//			int g =  ImageSupport.getPixel(img, x, 1024-y);
//			int pixelRef = g;
//			int pixel = ImageSupport.getPixel(loggedImage, x,y);
//
//			assertEquals(pixelRef, pixel);
//		}
//	}
//	keepFrame();
//}
//

@Test
public void prof12() throws Exception{
	devel = false;
	grade = true;
	String prog = "imageGen3";
	String input = prog
			+"\nimage[1024,1024] g; "
			+ "\nimage[1024,1024] h; "
			+ "\ng <- @ 0; "
			+ "\n file f = @ 1; "
			+ "\ng -> SCREEN; "
			+ "\nh[[r,a]] =  g[r,a];"
			+ "h -> SCREEN; "
			+ "\nh -> f;"
			;

	byte[] bytecode = genCode(input);
	String[] commandLineArgs = {imageFile1, imageFile2};
	runCode(prog, bytecode, commandLineArgs);

	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
	BufferedImage img = ImageSupport.readImage(imageFile2, 1024, 1024);
	assertTrue(ImageSupport.compareImages(loggedImage,img));
	keepFrame();
}

@Test
public void prof13() throws Exception{
	devel = false;
	grade = true;
	String prog = "imageGen3";
	String input = prog
			+"\nimage[1024,1024] g; "
			+ "\nimage[1024,1024] h; "
			+ "\ng <- @ 0; "
			+ "\n file f = @ 1; "
			+ "\ng -> SCREEN; "
			+ "\nh[[x,y]] =  g[x,y];"
			+ "h -> SCREEN; "
			+ "\nh -> f;"
			;

	byte[] bytecode = genCode(input);
	String[] commandLineArgs = {imageFile1, imageFile2};
	runCode(prog, bytecode, commandLineArgs);

	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
	BufferedImage img = ImageSupport.readImage(imageFile2, 1024, 1024);
	assertTrue(ImageSupport.compareImages(loggedImage,img));
	keepFrame();
}

@Test
public void prof14() throws Exception{
	devel = false;
	grade = true;
	String prog = "imageGen3";
	String input = prog
			+"\n image g; "
			+ "\n file f = @ 1; "
			+ "\ng <- @ 0;"
			+ "\ng -> SCREEN;"
			+ "\ng -> f;"
			+ "\nimage h;"
			+ "\nh <- f; "
			+ "\nh -> SCREEN;"
			;

	byte[] bytecode = genCode(input);
	String[] commandLineArgs = {imageFile1, imageFile2};
	runCode(prog, bytecode, commandLineArgs);

	BufferedImage loggedImage = RuntimeLog.globalImageLog.get(1);
	BufferedImage img = RuntimeLog.globalImageLog.get(0);
	assertTrue(ImageSupport.compareImages(loggedImage,img));
	keepFrame();
}


@Test
public void failed01() throws Exception {
String prog = "progImage";
String input = prog +
" image [1024,1024] g; \n"+
"image [1024,1024] h; \n" +
"g <- @ 0; \n"+
"g -> SCREEN; \n"+
"h[[x,y]] = ! g[x,y]; \n"+
"h -> SCREEN; \n";
byte[] bytecode = genCode(input);
String[] commandLineArgs = {imageFile1}; //create command line argument array to initialize params, none in this case
runCode(prog, bytecode, commandLineArgs);
show("Log:\n"+RuntimeLog.globalLog);
BufferedImage refImage0 = ImageSupport.readImage(imageFile1, 1024, 1024);
BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(1);
for (int x=0; x<1024; x++) {
	for (int y=0; y<1024; y++) {
		int pixelRef = ImageSupport.getPixel(refImage0, x, y);
		pixelRef = Z - pixelRef;
		int pixel = ImageSupport.getPixel(loggedImage0, x, y);
		assertEquals(pixelRef, pixel);
	}
}
keepFrame();
}

@Test
public void failed02() throws Exception {
String prog = "progImage";
String input = prog +
" image[1024,1024] g;" +
"image[1024,1024] h;" +
"g <- @ 0;" +
"g -> SCREEN;" +
"h[[x,y]] = ! g[x,y];" +
"h -> SCREEN; " +
"image[1024,1024] average; " +
"average[[x,y]] = h[x,y]*3;" +
"average -> SCREEN;";
byte[] bytecode = genCode(input);
String[] commandLineArgs = {imageFile1}; //create command line argument array to initialize params, none in this case
runCode(prog, bytecode, commandLineArgs);
show("Log:\n"+RuntimeLog.globalLog);
BufferedImage refImage0 = ImageSupport.readImage(imageFile1, 1024, 1024);
BufferedImage refImage1 = ImageSupport.makeImage(1024, 1024);
BufferedImage refImage2 = ImageSupport.makeImage(1024, 1024);
for (int x=0; x<1024; x++) {
	for (int y=0; y<1024; y++) {
		int pixelRef = ImageSupport.getPixel(refImage0, x, y);
		ImageSupport.setPixel(pixelRef ^ Integer.MAX_VALUE, refImage1, x, y);
		ImageSupport.setPixel((pixelRef ^ Integer.MAX_VALUE) * 3, refImage2, x, y);
	}
}
BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
BufferedImage loggedImage1 = RuntimeLog.globalImageLog.get(1);
BufferedImage loggedImage2 = RuntimeLog.globalImageLog.get(2);
for (int x=0; x<1024; x++) {
	for (int y=0; y<1024; y++) {
		assertEquals(ImageSupport.getPixel(refImage0, x, y), ImageSupport.getPixel(loggedImage0, x, y));
		assertEquals(ImageSupport.getPixel(refImage1, x, y), ImageSupport.getPixel(loggedImage1, x, y));
		assertEquals(ImageSupport.getPixel(refImage2, x, y), ImageSupport.getPixel(loggedImage2, x, y));
	}
}
keepFrame();
}

@Test
public void failed03() throws Exception {
String prog = "progImage";
String input = prog +
		"\nimage[1024,1024] g;" +
		"\nimage[1024,1024] h;" +
		"\ng <- @ 0;" +
		"\nfile f = @ 1;" +
		"\ng -> SCREEN;" +
		"\nh[[r,a]] = g[r,a];" +
		"\nh -> SCREEN;" +
		"\nh -> f;";
byte[] bytecode = genCode(input);
//paste path of image2 in arg @1
String[] commandLineArgs = {imageFile1,  "C:\\Users\\Omkar Vaidya\\2.jpg"}; //create command line argument array to initialize params, none in this case
runCode(prog, bytecode, commandLineArgs);
show("Log:\n"+RuntimeLog.globalLog);
BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
BufferedImage loggedImage1 = RuntimeLog.globalImageLog.get(1);
for (int x=0; x < 1024; x++) {
	for (int y=0; y < 1024; y++) {
		int r = (int) Math.hypot(x,y);
		int a = (int) Math.toDegrees( Math.atan2(y, x));
		double xx = r * Math.cos(Math.toRadians(a));
		double yy =  r * Math.sin(Math.toRadians(a));
		int pixelRef = ImageSupport.getPixel(loggedImage0, (int) xx, (int) yy);
		//assertEquals(pixelRef, ImageSupport.getPixel(loggedImage1, x, y));
	}
}
BufferedImage img = ImageIO.read(new File(imageFile2));
assertTrue(ImageSupport.compareImages(loggedImage1,img));
keepFrame();
}

@Test
public void failed04() throws Exception {
String prog = "progImage";
String input = prog +
		"\nimage g;" +
		"\nfile f = @ 1;" +
		"\ng <- @ 0;" +
		"\ng -> SCREEN;" +
		"\ng -> f;" +
		"\nimage h;" +
		"\nh <- f;" +
		"\nh -> SCREEN;";
byte[] bytecode = genCode(input);
//paste a new file name path in arg @1
String[] commandLineArgs = {imageFile1, "C:\\Users\\Omkar Vaidya\\1.jpg"}; //create command line argument array to initialize params, none in this case
runCode(prog, bytecode, commandLineArgs);
show("Log:\n"+RuntimeLog.globalLog);
BufferedImage loggedImage0 = RuntimeLog.globalImageLog.get(0);
BufferedImage loggedImage1 = RuntimeLog.globalImageLog.get(1);
//paste same path here
BufferedImage img = ImageIO.read(new File("C:\\Users\\Omkar Vaidya\\1.jpg"));
for (int x=0; x <ImageSupport.getX(loggedImage0); x++) {
	for (int y=0; y <ImageSupport.getY(loggedImage0); y++) {
		assertEquals(ImageSupport.getPixel(loggedImage0, x, y), ImageSupport.getPixel(loggedImage1, x, y));
	}
}
assertTrue(ImageSupport.compareImages(loggedImage0,img));
assertTrue(ImageSupport.compareImages(loggedImage1,img));
keepFrame();
}

}