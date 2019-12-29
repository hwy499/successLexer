package parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import Lexical.Token;
import Lexical.WordType;
import Lexical.lexicalAnalysis;

public class Parsing {
	
	public static Stack<Production> productionStack ; // 用于存储相应的状态
	private lexicalAnalysis lex; // 词法分析器
	private ArrayList<Token> tokenList; // 从词法分析器获得的所有token,相当于模型中的待输入序列
	private int length; // tokenlist的长度
	private int index; // 现在所指的token位置
	public static int index1; // 现在所指语句，的位置
	private ForecastAnalysisTable table; // 构造的语法分析表
	private Stack<Integer> stateStack; // 用于存储相应的状态
	private Map<Integer, Integer> map;

	public static StringBuilder analysisProcess = new StringBuilder();
	public static StringBuilder errorMessage = new StringBuilder();


	public Parsing(String filename) {
		productionStack = new Stack<Production>();
		map = new HashMap<Integer, Integer>();
		this.lex = new lexicalAnalysis(filename);
		this.tokenList = lex.getTokenList();
		saveToFile(this.tokenList, "词法分析.txt");
		add$();
		this.length = this.tokenList.size();
		this.index = 0;
		Parsing.index1 = 1;
		this.table = new ForecastAnalysisTable();
		this.stateStack = new Stack<Integer>();
		this.stateStack.push(0);
		String states = this.table.lr1ProjectCluster.getAllStatesToString();
		// 将所有状态集写入文件
		saveToFile(states,"LR1项目集簇.txt");
		saveToFile(this.table.print(), "预测分析表.txt");
	}

	public Token readToken() {
		if (index < length) {
			return tokenList.get(index++);
		} else {
			return null;
		}
	}

	public boolean analyze() {
		analysisProcess.append("开始分析第"+index1+"个语句"+"\n");
		int col = 1;
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
				analysisProcess
						.append("第"+col++ +"步  "+"动作为："+"移进" + "\t" + "状态栈:" + stateStack.toString() + "\t" + "待输入序列:" + printToken() + "\n");
			} else if (action.startsWith("r")) {
				Production production = Grammar.listDerivation.get(Integer.parseInt(action.substring(1)));
				int r = production.list.size();
				index--;
				while (r-- > 0) {
					stateStack.pop();
				}
				int s = table.GOTO(stateStack.lastElement(), production.left);
				stateStack.push(s);
				productionStack.add(production);
				analysisProcess
						.append("第"+col++ +"步  "+"动作为："+"规约" + "\t" + "状态栈:" + stateStack.toString() + "\t" + "待输入序列:" + printToken() + "\n");
			} else if (action.equals(ForecastAnalysisTable.acc)) {
				productionStack.add(null);
				System.out.println("第条" + index1 + "语句，语法分析完成" + "\t");
				analysisProcess.append("第条" + index1 + "语句，语法分析完成" + "\t");
				analysisProcess.append("第条" + index1 + "+语句，语法分析完成" + "\t" + "状态栈:" + stateStack.toString() + "\t"
						+ "待输入序列:" + printToken() + "\n");
				ParserTree.PrintTree();
				if (readToken() != null) {
					index1++;
					stateStack.pop();
					index--;
					try {
						analyze();
					}catch (Exception e) {
						System.out.println("语句"+index1+"发生符号不匹配错误");
					}
				}
				saveToFile(analysisProcess.toString(), "分析过程.txt");
				return true;
			} else {
				error();
				System.out.println("程序语法错误:\n"+errorMessage.toString());
				analysisProcess.append(errorMessage.toString());
				analysisProcess.append("程序语法错误");
				saveToFile(analysisProcess.toString(), "分析过程.txt");
				return false;
			}
		}
	}

	private String getValue(int valueType) {
		switch (valueType) {
		case WordType.ADD:
			return "+";
		case WordType.SUB:
			return "-";
		case WordType.MUL:
			return "*";
		case WordType.DIV:
			return "/";
		case WordType.ID:
			return "<id>";
		case WordType.NUM:
			return "<num>";
		case WordType.IF:
			return "if";
		case WordType.ELSE:
			return "else";
		case WordType.SEMICOLON:
			return ";";
		case WordType.PARENTHESIS_L:
			return "(";
		case WordType.PARENTHESIS_R:
			return ")";
		case WordType.GE:
			return ">=";
		case WordType.ASSIGN:
			return "=";
		case WordType.GT:
			return ">";
		case WordType.LE:
			return "<=";
		case WordType.LT:
			return "<";
		case WordType.BRACE_L:
			return "{";
		case WordType.BRACE_R:
			return "}";
		case WordType.WHILE:
			return "while";
		case -1:
			return "$";
		default:
			return null;
		}
	}

	public void error() {
		errorMessage.append("在源文件中第" + index1 + "个语句，中" + "第" + map.get(index - 1) + "个词法分析元素处发现了错误:"
				+ tokenList.get(index - 1).toString()+"\n");
	}

	private StringBuilder printToken() {
		StringBuilder output = new StringBuilder();
		for (int i = index; i < tokenList.size(); i++) {
			output.append(tokenList.get(i).value);
			output.append(" ");
		}
		return output;
	}

	private static void saveToFile(String str, String fileName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter("out/" + fileName);
			fileWriter.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 将词法分析结果输出到文件中
	private static void saveToFile(ArrayList<Token> list, String filename) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append("<" + list.get(i).type + "," + list.get(i).value + ">\n");
		}
		File file = new File("out/" + filename);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	private void add$(){
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
	}
}
