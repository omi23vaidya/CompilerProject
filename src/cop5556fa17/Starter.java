package cop5556fa17;

import cop5556fa17.Scanner.LexicalException;

public class Starter {

	public static void main(String[] args) throws LexicalException {
		// TODO Auto-generated method stub
		Scanner s = new Scanner("\" \\\\ \"").scan();
		System.out.println(s.nextToken().getText());
		System.out.println(s.toString());

	}

}
