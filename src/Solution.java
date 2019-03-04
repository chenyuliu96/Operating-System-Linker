import java.util.ArrayList;
import java.util.Scanner;
/*
 * This is a two-pass linker which accepts a series of object modules and outputs the symbol table, memory map, and corresponding
 * warnings generated by each module.
 * How to compile: call "javac Solution.java" from Terminal
 * How to run: call "java Solution" from Terminal
 * How to test(directly from keyboard): copy and paste the entire test input, then hit return/enter key
 * How to test(input redirection): call "java Solution < test.txt" from Terminal
 */
/**
 * 
 * @author chenyuliu
 * @version 1.0
 */

public class Solution {
	
	public static void main(String[] args) {
		ArrayList<Symbol> symbolTable = new ArrayList<Symbol>();//symbol table
		Scanner sc = new Scanner(System.in);
		int modNum = sc.nextInt();
		ArrayList<Module> modList = new ArrayList<Module>();
		for(int i=0;i<modNum;i++) {
			Module m = new Module();//construct a new module 
			m.Index = i;
			int defNum = sc.nextInt();
			for(int k=0;k<defNum;k++) {//scan in the definition list
				String def=sc.next();
				def+=" "+ sc.next();
				m.defList.add(def);
			}
			int useNum = sc.nextInt();
			for(int x=0;x<useNum;x++) {//scan in the Use list
				String use = sc.next();
				use+=" "+sc.next();
				m.useList.add(use);
			}
			int pNum = sc.nextInt();
			for(int x=0;x<pNum;x++) {//scan in the Instruction 
				String p = sc.next();
				p+=" "+sc.next();
				m.PText.add(p);
			}
			
			modList.add(m);
		}
		
		//default set the second module:(index1) absolute address
		Module m1 = modList.get(1);
		Module m0 = modList.get(0);
		m1.absoluteAddress = m0.PText.size();
		for(int x=2;x<modNum;x++) {
			//getting the absolute address for each module
			Module previousM = modList.get(x-1);
			Module current = modList.get(x);
			current.absoluteAddress=previousM.absoluteAddress+previousM.PText.size();
		}
		
		
		for(Module m:modList) {//go through the definition list 
			if(!m.defList.isEmpty()) {
				
				for(String s:m.defList) {
					Symbol sym= new Symbol();//creating a symbol object
					String[] result = s.split(" ");
					sym.symbol=result[0];
					sym.relaAddress = Integer.parseInt(result[1]);
					sym.AbsAddress = m.absoluteAddress + sym.relaAddress;
					
				
					for(Symbol s1 : symbolTable) {//update symbol table to avoid multiply defined symbol
						if (s1.symbol.equals(sym.symbol)) {
							System.out.println("Error: "  +  s1.symbol +" is multiply defined; last value used.");	
							symbolTable.remove(s1);
							break;
							}
							
						}
					sym.definingModule = m.Index;
					symbolTable.add(sym);
					}
					
					
				}
				
			}
		ArrayList<String> usedSymbol = new ArrayList<String>();
		for(Module m:modList) {
					//updating the output
					for(String x : m.PText) {
						String[] foo = x.split(" ");
						String type = foo[0];
						int address = Integer.parseInt(foo[1]);
						int threeDigit = Integer.parseInt(foo[1].substring(1, 4));
						
						
						if(type.equals("R")) {//if address is relative, first check if it exceed, then calculate the absolute address
							if(threeDigit > m.PText.size() ) {
								address = Integer.parseInt(foo[1].substring(0,1)+"000");
								System.out.println(" Error: Type R address exceeds module size: 0 (relative) used");
							}
							int absAddress = address + m.absoluteAddress;
							m.PTextModified.add(absAddress + "");
							
						}
						else if(type.equals("I")) {
							m.PTextModified.add(address + "");
						}
						else if(type.equals("A")) {//if address is absolute, first check if it exceed limit, then add it to the list
							if( threeDigit> 299) {//error checking
								System.out.println("Error: A type address exceeds machine size; max legal value used");
								String newAddress=  foo[1].charAt(0)+"299";
								m.PTextModified.add(newAddress);
							}
							else {
								m.PTextModified.add(address + "");
							}
						}
						else if(type.equals("E")) {//here, we leave the External address unchanged for now
							m.PTextModified.add(address + "");
						}
					}
				
					for(int x=0;x<m.useList.size();x++) {// this for loop check there is any multiple symbol used error
						String foo = m.useList.get(x).split(" ")[1];
						String bar="";
						for(int y=x+1;y<m.useList.size();y++)
							bar = m.useList.get(y).split(" ")[1];
							
							if (foo.equals(bar)) {//if yes, then remove the previous ones
								m.useList.remove(x);
								System.out.println("Error: Multiple symbols used: last one used");
							}
					}
					for(String x : m.useList) {//
						boolean symbolDefined = true;
						String symbolName = x.split(" ")[0];
						int index =0;
						for(Symbol s: symbolTable) {//check if the symbol is defined
							if(s.symbol.equals(symbolName)) {
								 index = s.AbsAddress;
								 symbolDefined = false;
							} 
						}
						if(!usedSymbol.contains(x)) {
							usedSymbol.add(x.split(" ")[0]);
						}
						String leftPaded = "";
						if(symbolDefined) {// if the symbol is not defined, leftpad the address with "1111"
							 leftPaded = "111";
							 System.out.printf("Error: %s is not defined; 111 used \n",symbolName);
						}
						else {//else leftpad the string with the last three digit of the symbol used
						 leftPaded = String.format("%03d", index);}
						boolean flag = false;
						boolean multiple = false;
							int modifyindex = Integer.parseInt(x.split(" ")[1]);
							String target = m.PTextModified.get(modifyindex);
							m.PTextModified.set(modifyindex, target.charAt(0)+ leftPaded);
							
							String lastThreeDigit = target.substring(1,4);
							while(!lastThreeDigit.equals("777")) {//if the last three digit is 777, we terminate the loop
								multiple = true;
								int newindex = Integer.parseInt(lastThreeDigit);
								
								lastThreeDigit = m.PTextModified.get(newindex).substring(1,4);
								m.PTextModified.set(newindex, m.PTextModified.get(newindex).charAt(0)+leftPaded);
							}
					}
				}
	ArrayList<String> stringOnlySymbol = new ArrayList<String>();
	for(Symbol s:symbolTable) {
		stringOnlySymbol.add(s.symbol);
	}
	//remove any used symbols to see if there is any symbol unused
	for(String x: usedSymbol) {
		if(stringOnlySymbol.contains(x)) {
			stringOnlySymbol.remove(x);
		}
	}
	//error checking if there is any unused symbol
	for(String x: stringOnlySymbol) {
		for(Symbol s : symbolTable) {
			if(s.symbol.equals(x)) {
				System.out.println("Warning: " + x+" was defined in module "+ s.definingModule+" but never used.");
			}
		}
	}
	//print symbol table
	System.out.println("");
	System.out.println("Symbol Table");
	for(Symbol s: symbolTable) {
		System.out.println(s.symbol + " = "+ s.AbsAddress );
	}
	int counter = 0;
	//print memory map
	System.out.println("\nMemory Map");
	for(Module m: modList) {
		for(String x: m.PTextModified) {
			System.out.printf("%-2s： %s \n", counter, x);
			counter++;
		}
	}
	//System.out.print(symbolTable.toString());
	}
	
	
	
}


class Symbol{
	String symbol;
	int AbsAddress;
	int relaAddress;
	int definingModule;
	boolean used = false;
	public String toString() {
		return "symbol "+ symbol + " absolute address is "+ this.AbsAddress + "\n it is defined in module "+ this.definingModule;
		
	}
}
class Module {
	int Index;
	int absoluteAddress=0;
	ArrayList<String> defList = new ArrayList<String>();
	ArrayList<String> useList = new ArrayList<String>();
	ArrayList<String> PText = new ArrayList<String>();
	ArrayList<String> PTextModified = new ArrayList<String>();

	
	
}