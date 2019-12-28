package parser;

import java.util.Stack;


public class CreateParseTree {
	private static ParserTree root;
	
	static {
		SyntaxParser.main(null);
		Stack<ParseResult> results = SyntaxParser.result;
		ParseResult item = results.pop();
		root = new ParserTree(item.value);
		Stack<ParserTree> stack = new Stack<ParserTree>();
		stack.add(root);
		while(!stack.empty()) {
			ParserTree pTree = stack.pop();
			int count = 0;
			System.out.println(item.value+"===>"+item.list);
			for (String name : item.list) {
				System.out.println("name:"+name);
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
	}
	
	public static ParserTree getParseTree() {
		return root;
	}
	
	
}
