package main;


import parser.*;
import swing.MainJFrame;
public class Main {
	public static void main(String[] args) {
		Parsing parsing = new Parsing("source.txt");
		parsing.analyze();
		ParserTree.saveParserTree("语法树.txt");	
		MainJFrame.main(args);
	}
}
