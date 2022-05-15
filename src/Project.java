import java.io.*;
import java.util.*;

public class Project {
	static HashMap<String,Integer> instructionSet;
	static int [] RAM;
	static int z,c;
	static int A,B,C,IR;
	static int stackPointer;
	static String code;
	static int pc,mar,clock;
	public static void main(String[] args) {
		 z = 0;c = 0;
		 instructionSet = new HashMap<>();
		 putinstructions();
		 RAM = new int[256];
		 //point to last filled location
		 stackPointer = 256;
		 pc = 0;mar = pc;
		 B = 3;
		 executeCode("movab",clock);
		 System.out.println(A+" "+B);
	}
	static void executeCode(String e,int clock) {

		  String INSTRUC = e;
		  INSTRUC = INSTRUC.toLowerCase();
		  String[] parts = INSTRUC.trim().split("[\\s+|\\,]");
		  if(!instructionSet.containsKey(parts[0])) {
		     System.out.println("instruction not present");
		  }else {
			  int hexCode = instructionSet.get(parts[0]);
			  RAM[mar] = hexCode;
			  if(hexCode==0) {

			  }else
			  callmethod(hexCode,parts,clock);
		  }

	}

	static int hexToDec(String hex) {
		//remove first two Character 0x
	  return Integer.parseInt(hex.substring(2),16);
	}
	static String decToHex(int dec) {
	   return Integer.toHexString(dec);
	 }
	 static void putinstructions() {
		instructionSet.put("hlt", 0);
		instructionSet.put("movab", 1);
		instructionSet.put("add", 2);
		instructionSet.put("sub", 3);
		instructionSet.put("jmp", 4);
	}
		private static void callmethod(int hexCode,String[] parts,int clock) {
			 if(hexCode==1) {//mov
				  movAB(parts,clock);
			  }
			  else if(hexCode==2) {//add
				  
			  }
			  else if(hexCode==3) {//sub
				  
			  }
			  else if(hexCode==4) {//jmp
				  
			  }
			  else if(hexCode==5) {
				  
			  }
			  else if(hexCode==6) {
				  
			  }
			 
		}
	 static void movAB(String[] parts,int clock) {
		  if(clock==0){
			  pc = 0;
			  mar =pc;
		  }else if(clock==1){
			  pc++;
			  IR = instructionSet.get(parts[0]);
		  }
		  else{
			  A =B;
		  }
		}

}
 
