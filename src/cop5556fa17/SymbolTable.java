package cop5556fa17;

import java.util.HashMap;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.Declaration;

public class SymbolTable {

	HashMap<String, Declaration> symbolTable = new HashMap<String, Declaration>();
	
	public Type lookupType(String name)
	{
		return symbolTable.containsKey(name) ? symbolTable.get(name).newType : null ;
	}
	
	public Declaration lookupDec(String name)
	{
		return symbolTable.get(name);
	}
	
	public void insert(String name, Declaration value)
	{
		symbolTable.put(name, value);
	}
	
	public boolean contains(String name)
	{
		 if(symbolTable.containsKey(name))
			return true;
		 else 
			return false;
	}
}
