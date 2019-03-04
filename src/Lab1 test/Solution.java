import java.util.ArrayList;
import java.util.Scanner;

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
		
		
		//find out the symbol table
		for(Module m:modList) {
			//int indexOfModule = m.Index;
			if(!m.defList.isEmpty()) {
				
				for(String s:m.defList) {
					Symbol sym= new Symbol();
					String[] result = s.split(" ");
					sym.symbol=result[0];
					sym.relaAddress = Integer.parseInt(result[1]);
					sym.AbsAddress = m.absoluteAddress + sym.relaAddress;
					
				
					for(Symbol s1 : symbolTable) {
						//System.out.println("testing" + symbolTable.toString());
						if (s1.symbol.equals(sym.symbol)) {
							//<ERROR1> : multiply defined symbol
							System.out.println("Error<1>:("  +  s1 +") is multiply defined; last value used.");	
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
					/*
					System.out.println("module number " + m.Index + " has defList" + m.defList.toString());
					System.out.println(" has useList " + m.useList.toString());
					System.out.println(" has programList " + m.PText.toString());
					System.out.println(" has absolute address " + m.absoluteAddress);
					*/
					//updating the output
					for(String x : m.PText) {
						String[] foo = x.split(" ");
						String type = foo[0];
						int address = Integer.parseInt(foo[1]);
						int threeDigit = Integer.parseInt(foo[1].substring(1, 4));
						
						
						if(type.equals("R")) {
							int absAddress = address + m.absoluteAddress;
							m.PTextModified.add(absAddress + "");
						}
						else if(type.equals("I")) {
							m.PTextModified.add(address + "");
						}
						else if(type.equals("A")) {
							if( threeDigit> 299) {
								System.out.println("Error<5>: A type address exceeds machine size; max legal value used");
								String newAddress=  foo[1].charAt(0)+"299";
								m.PTextModified.add(newAddress);
							}
							else {
								m.PTextModified.add(address + "");
							}
						}
						else if(type.equals("E")) {
							m.PTextModified.add(address + "");
						}
					}
					
					for(int i=0;i<m.useList.size();i++) {
						String foo = m.useList.get(i).split(" ")[1];
						for(int j=1;j<m.useList.size();j++) {
							if( m.useList.get(j).split(" ")[1].equals(foo)) {
								m.useList.remove(i);
								System.out.println("multiple symbols used");
							}
						}
					}
					for(String x : m.useList) {
						String symbolName = x.split(" ")[0];
						int index =0;
						//check for multiple use
						
						for(Symbol s: symbolTable) {
							if(s.symbol.equals(symbolName)) {
								 index = s.AbsAddress;
							}
						}
						if(!usedSymbol.contains(x)) {
							usedSymbol.add(x.split(" ")[0]);
						}
						
						
						String leftPaded = String.format("%03d", index);
						//System.out.println(leftPaded);
						boolean flag = false;
						boolean multiple = false;
							int modifyindex = Integer.parseInt(x.split(" ")[1]);
							String target = m.PTextModified.get(modifyindex);
							//System.out.println(target);
							m.PTextModified.set(modifyindex, target.charAt(0)+ leftPaded);
							
							String lastThreeDigit = target.substring(1,4);
							//System.out.println(lastThreeDigit);
							while(!lastThreeDigit.equals("777")) {
								multiple = true;
								int newindex = Integer.parseInt(lastThreeDigit);
								
								lastThreeDigit = m.PTextModified.get(newindex).substring(1,4);
								//System.out.println("new index is "+ newindex);
								m.PTextModified.set(newindex, m.PTextModified.get(newindex).charAt(0)+leftPaded);
							}
							
							
							
					}
					//System.out.println("====="+m.PTextModified);
					for(Symbol s:symbolTable) {
						//<ERROR3>error printed when a symbol is defined but was never used PRINT A WARNING MESSAGE
						//<ERROR2> printed when a symbol is used but not defined/ USE THE VALUE 111
						//<ERROR6>if multiple symbol is used in the same instruction 
						
					}
				}
		//System.out.print("printing symbol not used" + dupSymbol.toString());
		
				
	//System.out.print(usedSymbol.toString());
	ArrayList<String> stringOnlySymbol = new ArrayList<String>();
	for(Symbol s:symbolTable) {
		stringOnlySymbol.add(s.symbol);
	}
	for(String x: usedSymbol) {
		if(stringOnlySymbol.contains(x)) {
			stringOnlySymbol.remove(x);
		}
	}
	for(String x: stringOnlySymbol) {
		for(Symbol s : symbolTable) {
			if(s.symbol.equals(x)) {
				System.out.println(x+" was defined in module "+ s.definingModule+" but never used.");
			}
		}
	}
	for(Symbol s: symbolTable) {
		System.out.println(s.symbol + " = "+ s.AbsAddress );
		System.out.println("");
	}
	int counter = 0;
	System.out.println("Memory Map");
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
	
	public void update(ArrayList<String> result, int index, String content) {

		
	}
	
	
}
//first run: ouput the symbol table. go through