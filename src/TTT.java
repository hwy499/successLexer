import java.awt.print.Printable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import parser.CFG;
import parser.ParseResult;
import parser.ParserTree;
import parser.SyntaxParser;

public class TTT {
	
	
	
	/*
	 * if(a <= b) min = a; else min = b; result = min * PI * (4/3); if(a < b){a =
	 * b;} if(a <= b) a = b; if(a <= b) a= b;else{ a =c;} if(a >= b) { a = b; }
	 * else{a = c;}
	 */
	public static void main(String[] args) {
		SyntaxParser.main(null);
		Stack<ParseResult> results = SyntaxParser.result;
		ParseResult item = results.pop();
		ParserTree root = new ParserTree(item.value);
		Stack<ParserTree> stack = new Stack<ParserTree>();
		stack.add(root);
		while(!stack.empty()) {
			ParserTree pTree = stack.pop();
			int count = 0;
			System.out.println(item.list);
			for (String name : item.list) {
				pTree.addNode(new ParserTree(name));
				if(CFG.VN.contains(name)) {
					stack.add(pTree.getChild(count));
				}
				count ++;
			}
			if(!stack.empty()) {
				item = results.pop();
			}
		}
		StringBuilder sb = new StringBuilder();
		root.printTree(0,root,sb);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("Output/ParserTree.txt");
			fileWriter.write(sb.toString());
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\n\n从上到下遍历叶子节点");
		root.leftToRight(root);
	
		
		
		/*
		 * while(!results.empty()) { ParseResult item = results.pop(); if(item != null)
		 * { System.out.println(item.value + "==>>" + item.list); } }
		 */
	}
	
	@Test
	public  void test() {
		ParserTree p = new ParserTree("B");
		System.out.println(CFG.VN.contains(p.getRootData()));
	}
}
