package main;

import parser.*;
public class Main {
	public static void main(String[] args) {
		Parsing parsing = new Parsing("source.txt");
		boolean succeess = parsing.analyze();
		if (succeess) {
			ParserTree.PrintTree();
			ParserTree.saveParserTree("语法树");
		} 
	}
}
