package parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import parser.Grammar;
import parser.ParserTreeNode;
import parser.Production;
import parser.Parsing;

public class ParserTree {
	
	private static StringBuilder sb = new StringBuilder();
	public static void PrintTree() {
		Stack<Production> results = Parsing.productionStack;
		int con = Parsing.index1;
		while (!results.empty()) {
			results.pop();
			Production item = results.pop();
			ParserTreeNode root = new ParserTreeNode(item.left);
			Stack<ParserTreeNode> stack = new Stack<ParserTreeNode>();
			stack.add(root);
			while (!stack.empty()) {
				ParserTreeNode pTree = stack.pop();
				int count = 0;
				for (String name : item.list) {
					pTree.addNode(new ParserTreeNode(name));
					if (Grammar.VN.contains(name)) {
						stack.add(pTree.getChild(count));
					}
					count++;
				}
				if (!stack.empty()) {
					item = results.pop();
				}
			}
			System.out.println("语句" + con + ":");
			sb.append("语句" + con-- + ":" + "\n");
			leftToRight(root, sb);
			System.out.println("\n语法树:");
			sb.append("\n语法树:" + "\n");
			printTree(0, root, sb);
			sb.append("\n");
			System.out.println();

		}
	}
	
	// 从左到右扫描叶子节点
	public static void leftToRight(ParserTreeNode root, StringBuilder sb) {
		if (!root.isEmpty()) {
			if (root.isLeaf() && Grammar.VT.contains(root.getRootData())) {
				System.out.print("" + root.getRootData());
				sb.append("" + root.getRootData());
			}
			for (ParserTreeNode child : root.getChilds()) {
				if (child != null) {
					leftToRight(child, sb);
				}
			}
		}
	}

	
	public static void saveParserTree(String fileName) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("out/"+fileName);
			fileWriter.write(sb.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 打印语法树
	public static StringBuilder printTree(int level, ParserTreeNode root, StringBuilder sb) {
		if (level == 0) {
			System.out.println(levelSign(level) + root.getRootData());
			sb.append(levelSign(level) + root.getRootData() + "\n");
		}
		level++;
		if (root.isEmpty()) {
			return new StringBuilder("");
		}
		for (ParserTreeNode child : root.getChilds()) {
			if (child != null) {
				if (child.isLeaf()) {
					sb.append(levelSign(level) + child.getRootData() + "\n");
					System.out.println(levelSign(level) + child.getRootData());
				} else {
					System.out.println(levelSign(level) + child.getRootData());
					sb.append(levelSign(level) + child.getRootData() + "\n");
					printTree(level, child, sb);
				}
			}
		}
		return sb;
	}

	private static String levelSign(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(" |————");
		for (int x = 0; x < level; x++) {
			sb.insert(0, " |   ");
		}
		return sb.toString();
	}

}
