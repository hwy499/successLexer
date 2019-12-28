package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import lex.LexicalAnalyzer;
import lex.Token;
import lex.Type;

public class SyntaxParser {
	public static Stack<ParseResult> result = new Stack<ParseResult>(); // 用于存储相应的状态
	private LexicalAnalyzer lex; // 词法分析器
	private ArrayList<Token> tokenList; // 从词法分析器获得的所有token,相当于模型中的输入
	private int length; // tokenlist的长度
	private int index; // 现在所指的token位置
	private int index1; // 现在所指语句的位置
	private ForecastAnalysisTable table; // 构造的语法分析表
	private Stack<Integer> stateStack; // 用于存储相应的状态
	private Map<Integer, Integer> map;
	private FileWriter fileWriter; // 保存分析过程
	private FileWriter fileWriter1; // 保存识别文法规范句型的活前缀DFA

	public static void main(String[] args) {
		SyntaxParser parser = new SyntaxParser("source.txt"); // 调用
		try {
			parser.fileWriter = new FileWriter(new File("Output/分析过程.txt"));
			String str = "";
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("====================分析过程 Start=======================");
			if (parser.analyze()) {
				System.out.println("程序语法正确");
				str = "程序语法正确";
			} else {
				System.out.println("程序语法错误");
				str = "程序语法错误";
			}
			System.out.println("====================分析过程 END=======================");
			parser.fileWriter.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				parser.fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public SyntaxParser(String filename) {
		try {
			fileWriter1 = new FileWriter(new File("Output/活前缀DFA活前缀DFA.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map = new HashMap<Integer, Integer>();
		// new 一个词法分析器
		this.lex = new LexicalAnalyzer(filename);
		// 获取token序列
		this.tokenList = lex.getTokenList();

		// 将token序列写入文件
		try {
			lex.output(this.tokenList, "result.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 在每一个语句最后追加一个结束标记
		int col = 1;
		boolean isFlag = true;
		for (int i = 0; i < tokenList.size(); i++) {
			if (tokenList.get(i).value == "{") {
				isFlag = false;
			}
			if (isFlag) {
				if (tokenList.get(i).value == ";") {
					if (!(i != tokenList.size() - 1 && tokenList.get(i + 1).value == "else")) {
						this.tokenList.add(i + 1, new Token(-1, "$"));
						col = 1;
					}
				}
			} else {
				if (tokenList.get(i).value == "}"
						&& !(i != tokenList.size() - 1 && tokenList.get(i + 1).value == "else")) {
					isFlag = true;
					this.tokenList.add(i + 1, new Token(-1, "$"));
					col = 1;
				}
			}
			map.put(i, col);
			col++;
		}

		// 获取tokenList的大小
		this.length = this.tokenList.size();
		// 索引初始化为0
		this.index = 0;
		this.index1 = 1;
		// 初始化分析表
		this.table = new ForecastAnalysisTable();
		// 初始化状态栈
		this.stateStack = new Stack<Integer>();
		// 压入0
		this.stateStack.push(0);
		System.out.println("====================二元组 Start=======================");
		for (int i = 0; i < tokenList.size(); i++) {
			System.out.println(tokenList.get(i).toString());
		}
		System.out.println("====================二元组 END=======================");
		System.out.println();
		System.out.println();
		System.out.println();
		// 打印所有状态
		System.out.println("====================活前缀DFA Start=======================");
		String states = this.table.dfa.printAllStates();
		// 将所有状态写入活前缀DFA活前缀DFA.txt
		try {
			fileWriter1.write(states);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("====================活前缀DFA END=======================");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("====================语法分析表 Start=======================");
		// 打印语法分析表
		this.table.print();
		System.out.println("====================语法分析表 END=======================");
		System.out.println();
		System.out.println();
		System.out.println();

	}

	public Token readToken() {
		if (index < length) {
			return tokenList.get(index++);
		} else {
			return null;
		}
	}

	public boolean analyze() {
		while (true) {
			Token token = readToken();
			int valueType = token.type;
			String value = getValue(valueType);
			if (value == null) {
				error();
				return false;
			}
			int state = stateStack.lastElement();
			String action = table.ACTION(state, value);

			if (action.startsWith("s")) {
				int newState = Integer.parseInt(action.substring(1));
				stateStack.push(newState);
				System.out.print("移入" + "\t");
				System.out.print("状态表:" + stateStack.toString() + "\t");
				System.out.print("输入:");
				printInput();
				String str = "移入" + "\t" + "状态表:" + stateStack.toString() + "\t";
				str += "输入:" + printInput() + "\n";
				writeFile(str);
				System.out.println();
			} else if (action.startsWith("r")) {
				Derivation derivation = Grammar.listDerivation.get(Integer.parseInt(action.substring(1)));
				int r = derivation.list.size();
				index--;
				for (int i = 0; i < r; i++) {
					stateStack.pop();
				}
				int s = table.GOTO(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				System.out.print("规约" + "\t");
				System.out.print("状态表:" + stateStack.toString() + "\t");
				System.out.print("输入:");
				printInput();
				System.out.println("\n" + derivation.left + "=====>>" + derivation.list);
				result.add(new ParseResult(derivation.left, derivation.list));
				String str = "规约" + "\t" + "状态表:" + stateStack.toString() + "\t";
				str += "输入:" + printInput() + "\n";
				writeFile(str);
				System.out.println();
			} else if (action.equals(ForecastAnalysisTable.acc)) {
				System.out.print("第条" + index1 + "+语句语法分析完成" + "\t");
				System.out.print("状态表:" + stateStack.toString() + "\t");
				System.out.print("输入:");
				printInput();
				System.out.println();
				String str = "第条" + index1 + "+语句语法分析完成" + "\t" + "状态表:" + stateStack.toString() + "\t";
				str += "输入:" + printInput() + "\n";
				writeFile(str);
				if (readToken() != null) {
					index1++;
					stateStack.pop();
					index--;
					analyze();
				} else {

				}
				return true;
			} else {
				error();
				return false;
			}

		}
	}

	private String getValue(int valueType) {
		switch (valueType) {
		case Type.ADD:
			return "+";
		case Type.SUB:
			return "-";
		case Type.MUL:
			return "*";
		case Type.DIV:
			return "/";
		case Type.ID:
			return "<id>";
		case Type.NUM:
			return "<num>";
		case Type.IF:
			return "if";
		case Type.ELSE:
			return "else";
		case Type.SEMICOLON:
			return ";";
		case Type.PARENTHESIS_L:
			return "(";
		case Type.PARENTHESIS_R:
			return ")";
		case Type.GE:
			return ">=";
		case Type.ASSIGN:
			return "=";
		case Type.GT:
			return ">";
		case Type.LE:
			return "<=";
		case Type.LT:
			return "<";

		case Type.BRACE_L:
			return "{";
		case Type.BRACE_R:
			return "}";
		case Type.WHILE:
			return "while";
		case -1:
			return "$";
		default:
			return null;
		}
	}

	/**
	 * 出错
	 */
	public void error() {
		System.out.println("在源文件中第" + index1 + "个语句中" + "第" + map.get(index - 1) + "个词法分析元素处发现了错误:"
				+ tokenList.get(index - 1).toString());
		String str = "在源文件中第" + index1 + "个语句中" + "第" + map.get(index - 1) + "个词法分析元素处发现了错误:"
				+ tokenList.get(index - 1).toString();
		writeFile(str);
	}

	private String printInput() {
		String output = "";
		for (int i = index; i < tokenList.size(); i++) {
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.print(output);
		return output;
	}

	private void writeFile(String str) {
		try {
			fileWriter.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
